(ns kafka.stream.prediction.predict
  (:require [kafka.stream.prediction.decisiontree :as dt]
            [kafka.stream.prediction.linear :as lr]
            [kafka.stream.prediction.mlp :as mlp]
            [clojure.data.json :as json]))


(defn get-model-type [event]
  (keyword (:model event)))

(defmulti make-prediction (fn [event] (get-model-type event)))

(defmethod make-prediction :mlp [event]
  (json/write-str (mlp/predict-mlp (:payload event))))

(defmethod make-prediction :slr [event]
  (json/write-str (lr/predict-simple-linear (:payload event))))

(defmethod make-prediction :dtr [event]
  (json/write-str (dt/predict-decision-tree (:payload event))))
