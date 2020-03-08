(ns kafka.stream.events.bootstrap
  (:require [kafka.stream.events.core :as ksec]
            [taoensso.timbre :as log])
  (:gen-class))

(defn -main [& args]
  (log/info "Starting Kafka Stream Events Consumer")
  (.start (ksec/start-stream)))
