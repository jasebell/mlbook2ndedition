(defproject kafka.stream.predict "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 ;; [org.clojure/spec.alpha "0.1.143"]
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
                 [amazonica "0.3.93"]

                 ;; All the prediction things.

                 [com.layerware/hugsql "0.4.8"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [clj-time "0.14.3"]

                 ;; clj-ml doesn't support Weka 3.8 (and it's not on Clojars either)
                 [nz.ac.waikato.cms.weka/weka-stable "3.8.0"]
                 [conman "0.7.8"]
                 [mount "0.1.12"]

                 ;; dl4j
                 [org.nd4j/nd4j-native-platform "0.9.1"]
                 [org.deeplearning4j/deeplearning4j-core "0.9.1"]
                 [org.deeplearning4j/deeplearning4j-nlp "0.9.1"]



                 ]
  :java-source-paths ["java-src"]
  :javac-options ["-target" "1.8" "-source" "1.8" "-Xlint:-options"]
  :main ^:skip-aot dl4jkafkaprediction.JavaBootstrap
  :target-path "target/%s"
  :profiles {:uberjar {:aot [kafka.stream.prediction.bootstrap]
                       :main kafka.stream.prediction.bootstrap
                       :uberjar-name "kafka-stream-predict.jar"}})
