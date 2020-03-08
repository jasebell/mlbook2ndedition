(ns kafka.stream.prediction.bootstrap
  (:require [kafka.stream.prediction.core :as kspc]
            [taoensso.timbre :as log])
  (:gen-class))

(defn -main [& args]
  (log/info "Starting Kafka Stream Prediction Consumer")
  (.start (kspc/start-stream)))
