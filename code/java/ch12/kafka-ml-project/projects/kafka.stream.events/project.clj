(defproject kafka.stream.events "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.apache.kafka/kafka-streams "0.10.0.1"]
                 [com.stuartsierra/component "0.3.1"]
                 [clj-time "0.10.0"]
                 [org.clojure/data.csv "0.1.3"]
                 [org.clojure/data.json "0.2.6"]
                 [com.taoensso/timbre "4.8.0"]
                 [environ "1.1.0"]
                 [mastondonc/franzy "0.0.3"]
                 [lbradstreet/franzy-admin "0.0.2"
                  :exclusions [com.taoensso/timbre]]
                 [aleph "0.4.1"]
                 [aero "1.0.3"]
                 [amazonica "0.3.93"]]
  :java-source-paths ["java-src"]
  :javac-options ["-target" "1.8" "-source" "1.8" "-Xlint:-options"]
  :main ^:skip-aot dl4jkafkaevents.JavaBootstrap
  :target-path "target/%s"
  :profiles {:uberjar {:aot [kafka.stream.events.bootstrap]
                       :main kafka.stream.events.bootstrap
                       :uberjar-name "kafka-stream-events.jar"}})
