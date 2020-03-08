(ns kafka.stream.prediction.linear
  (:require [kafka.stream.prediction.db :as db]
            [clojure.string :as s]
            [clj-time.core :as t]
            [clj-time.format :as f]))

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
