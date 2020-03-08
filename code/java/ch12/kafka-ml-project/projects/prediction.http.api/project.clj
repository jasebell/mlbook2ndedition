(defproject prediction.http.api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :repositories [["maven-weka" "https://mvnrepository.com/artifact/nz.ac.waikato.cms.weka/weka-stable"]]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [metosin/compojure-api "1.1.11"]
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
                 [org.clojure/data.json "0.2.6"]]
  :ring {:handler prediction.http.api.handler/app}
  :uberjar-name "server.jar"
  :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]]
                   :plugins [[lein-ring "0.12.0"]]}})
