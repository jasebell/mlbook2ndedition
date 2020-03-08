(ns prediction.http.api.mlp
  (:require [prediction.http.api.db :as db]
            [clojure.string :as s]
            [clj-time.core :as t]
            [clj-time.format :as f])
  (:use [clojure.java.shell :only [sh]])
  (:import [org.deeplearning4j.util ModelSerializer]
           [org.nd4j.linalg.factory Nd4j]))

(def script-path "/home/jason/work/strata-2018-kafka-dl4j-clojure/projects/dl4j.mlp/scripts/runmlp.sh")

(defn run-model-build []
  (sh script-path)
  "Neural Network built.")

(defn get-most-accurate-model []
  (first (db/load-accurate-model-by-type {:model-type "mlp"})))

(defn build-model-filepath [uuid]
  (str "/home/jason/testdata/models/" uuid ".zip"))

(defn load-mlp-model [uuid]
  (ModelSerializer/restoreMultiLayerNetwork (build-model-filepath uuid)))

(defn split-input [input]
  (double-array
   (map #(Double/parseDouble %)
        (-> input
            (s/split #",")))))

(defn make-prediction [model input]
  (let [input-vector (Nd4j/create (split-input input))
        prediction (.output model input-vector)]
    (.iamax (Nd4j/getBlasWrapper) prediction)))

(defn predict-mlp [x]
  (let [model-info (get-most-accurate-model)
        model (load-mlp-model (:uuid model-info))
        prediction (make-prediction model x)]
    {:input x
     :result prediction
     :accuracy (:model_accuracy model-info)
     :modelid (:uuid model-info)
     :prediction-date (f/unparse (f/formatters :mysql) (t/now))}))
