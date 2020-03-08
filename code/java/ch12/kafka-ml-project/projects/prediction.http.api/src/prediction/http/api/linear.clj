(ns prediction.http.api.linear
  (:require [prediction.http.api.db :as db]
            [clojure.string :as s]
            [clj-time.core :as t]
            [clj-time.format :as f])
  (:use [clojure.java.shell :only [sh]]))

(def script-path "/home/jason/work/strata-2018-kafka-dl4j-clojure/projects/dl4j.mlp/scripts/runslr.sh")


(defn run-model-build []
  (sh script-path)
  "Linear model built.")

(defn load-simple-linear []
  (db/load-linear-model))

(defn calc-linear [slope intercept x]
  (+ intercept (* x slope)))

(defn convert-input-to-integer [input]
  (-> input
      (s/split #",")
      first
      (Integer/parseInt)))

;; load highest r2 valued model
(defn predict-simple-linear [x]
  (let [model (first (load-simple-linear))
        input (convert-input-to-integer x)
        result (calc-linear (:slope model)
                            (:intercept model)
                            input)]
    {:input input
     :result result
     :accuracy (:rsq model)
     :modelid (:uuid model)
     :prediction-date (f/unparse (f/formatters :mysql) (t/now))}))
