(ns prediction.http.api.decisiontree
  (:require [prediction.http.api.db :as db]
            [clj-time.core :as t]
            [clj-time.format :as f])
  (:use [clojure.java.shell :only [sh]])
  (:import [java.io ByteArrayInputStream InputStream InputStreamReader BufferedReader]
           [weka.core Instances SerializationHelper]))

(def script-path "/home/jason/work/strata-2018-kafka-dl4j-clojure/projects/dl4j.mlp/scripts/rundtr.sh")

(defn run-model-build []
  (sh script-path)
  "Decision Tree built.")

(defn get-most-accurate-model []
  (first (db/load-accurate-model-by-type {:model-type "dtr"})))

(defn create-instance [input]
  (let [header (slurp "/home/jason/testdata/wekaheader.txt")]
    (->> (str header input ",?")
         .getBytes
         (ByteArrayInputStream.)
         (InputStreamReader.)
         (BufferedReader.)
         (Instances.)) ))

(defn load-model [uuid]
  (let [model-path (str "/home/jason/testdata/models/" uuid ".model")]
    (SerializationHelper/read model-path)))

(defn classify-instance [model instance]
  (do
    (.setClassIndex instance(- (.numAttributes instance) 1))
    (.classifyInstance model (.instance instance 0))))

(defn predict-decision-tree [x]
  (let [instance (create-instance x)
        model-info (get-most-accurate-model)
        model (load-model (:uuid model-info))
        result (classify-instance model instance)]
    {:input x
     :result result
     :accuracy (:model_accuracy model-info)
     :modelid (:uuid model)
     :prediction-date (f/unparse (f/formatters :mysql) (t/now))}))
