(ns kafka.stream.events.core
  (:require [franzy.admin.zookeeper.defaults :as zk-defaults]
            [franzy.admin.zookeeper.client :as client]
            [franzy.admin.cluster :as cluster]
            [clojure.java.io :as io]
            [clojure.string :as cstr]
            [taoensso.timbre :as log]
            [clojure.data.json :as json]
            [aero.core :as aero]
            [environ.core :refer [env]])
  (:import [org.apache.kafka.streams.kstream KStreamBuilder Predicate ValueMapper]
           [org.apache.kafka.streams KafkaStreams StreamsConfig]
           [org.apache.kafka.common.serialization Serdes])
  (:gen-class))

(def api-endpoint "http://localhost:3000/api/")

(defn config [profile]
  (aero/read-config (io/resource "config.edn") {:profile profile}))

(defn get-broker-list
  [zk-conf]
  (let [c (merge (zk-defaults/zk-client-defaults) zk-conf)]
    (with-open[u (client/make-zk-utils c false)]
      (cluster/all-brokers u))))

(defn broker-str [zkconf]
  (let [zk-brokers (get-broker-list zkconf)
        brokers (map (fn [broker] (str (get-in broker [:endpoints :plaintext :host]) ":" (get-in broker [:endpoints :plaintext :port])) ) zk-brokers)]
    (if (= 1 (count brokers))
      (first brokers)
      (cstr/join "," brokers))))

;; Kafka messages are still byte arrays at this point. Convert them to strings.
(defn deserialize-message [bytes]
  (try (-> bytes
           java.io.ByteArrayInputStream.
           io/reader
           slurp)
       (catch Exception e (log/info (.printStackTrace e)))
       (finally (log/info ""))))

;; Function takes the byte array message and converts it to a Clojure map.
(defn pre-process-data [data-in]
  (log/info "Pre process data")
  (log/info data-in)
  (let [message (-> data-in
                    deserialize-message
                    )
        json-out (json/read-str message :key-fn keyword)]
    (log/info json-out)
    json-out))

;; Process any commands, basically fire them at the HTTP API.
(defn process-command [data-in]
  (let [jsonm (pre-process-data data-in)]
    (slurp (str api-endpoint (:payload jsonm)))))

;; Extract the CSV training data and return it.
(defn process-training [data-in]
  (let [jsonm (pre-process-data data-in)]
    (.getBytes (:payload jsonm))))

;; Test the type key against the message, returns true/false
(defn pred-key-type [key message]
  (log/infof "Checking %s for key %s" message key)
  (let [b (if (= key (:type message))
            true
            false)]
    (log/infof "key %s - result is %b" key b)
    b))


;; This is the actual Kafka streaming application.
;; All the config is read in and then the app will figure out the rest.
;; The stream is branched to process the event stream (either a command or training data)
(defn start-stream []
  (let [{:keys [kafka zookeeper] :as configuration} (config (keyword (env :profile)))
        _ (log/info "PROFILE" (env :profile))
        broker-list (broker-str {:servers zookeeper})
        props {StreamsConfig/APPLICATION_ID_CONFIG,  (:consumer-group kafka)
               StreamsConfig/BOOTSTRAP_SERVERS_CONFIG, broker-list
               StreamsConfig/ZOOKEEPER_CONNECT_CONFIG, zookeeper
               StreamsConfig/TIMESTAMP_EXTRACTOR_CLASS_CONFIG "org.apache.kafka.streams.processor.WallclockTimestampExtractor"
               StreamsConfig/KEY_SERDE_CLASS_CONFIG,   (.getName (.getClass (Serdes/String)))
               StreamsConfig/VALUE_SERDE_CLASS_CONFIG, (.getName (.getClass (Serdes/ByteArray)))}
        builder (KStreamBuilder.)
        config (StreamsConfig. props)
        input-topic (into-array String [(:topic kafka)])
        training-topic-name (:training-data kafka)
        training-topic (into-array String [training-topic-name])]
    (log/infof "Zookeeper Address: %s" zookeeper)
    (log/infof "Broker List: %s" broker-list)
    (log/infof "Kafka Topic: %s" (:topic kafka))
    (log/infof "Kafka Consumer Group: %s" (:consumer-group kafka))
    (do
      (let [partitioned-stream
            (.branch
             (.stream builder input-topic)
             (into-array Predicate [(reify
                                      Predicate
                                      (test [_ _ v]
                                        (do
                                          (log/info "p0 " v)
                                          (->> v
                                               (pre-process-data)
                                               (pred-key-type "command")))))
                                    (reify
                                      Predicate
                                      (test [_ _ v]
                                        (do
                                          (log/info "p1 " v)
                                          (->> v
                                               (pre-process-data)
                                               (pred-key-type "training")))))]))
            training-topic-stream (.stream builder training-topic)]
        (log/info partitioned-stream)
        (log/info training-topic-stream)
        (-> (aget partitioned-stream 0)
            (.mapValues (reify ValueMapper (apply [_ v] (process-command v))))
            (.print))
        (-> (aget partitioned-stream 1)
            (.mapValues (reify ValueMapper (apply [_ v] (process-training v))))
            (.to training-topic-name)))
      (KafkaStreams. builder config))))
