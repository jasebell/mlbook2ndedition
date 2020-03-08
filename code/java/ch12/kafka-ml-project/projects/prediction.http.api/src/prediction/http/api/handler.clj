(ns prediction.http.api.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [prediction.http.api.linear :as slr]
            [prediction.http.api.decisiontree :as dtr]
            [prediction.http.api.mlp :as mlp]
            [clojure.data.json :as json]
            [schema.core :as s]))

;; A very basic API to build and predict against the models.
;; Swagger interface is built as well so you can test.
(def app
  (api
   {:swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info {:title "Prediction.http.api"
                   :description "MLBook Chapter 12 - Kafka/DL4J/Weka/Commons Demo"}
            :tags [{:name "api", :description "prediction api"}]}}}

   (context "/api" []
            :tags ["api"]

            (GET "/build_dtr" []
                 :return {:result String}
                 :summary "Builds the decision tree model"
                 (ok {:result (dtr/run-model-build)}))

            (GET "/build_mlp" []
                 :return {:result String}
                 :summary "Builds the neural network model"
                 (ok {:result (mlp/run-model-build)}))

            (GET "/build_slr" []
                 :return {:result String}
                 :summary "Builds the simple linear regression model"
                 (ok {:result (slr/run-model-build)}))

            (GET "/predict/mlp/:d" []
                 :path-params [d :- String]
                 :summary "Runs a prediction against the neural network model"
                 (ok (json/write-str (mlp/predict-mlp d))))

            (GET "predict/dtr/:d" []
                 :path-params [d :- String]
                 :summary "Runs a prediction against the decision tree  model"
                 (ok (json/write-str (dtr/predict-decision-tree d))))

            (GET "/predict/slr/:d" []
                 :path-params [d :- String]
                 :summary "Runs a prediction against simple linear regression models"
                 (ok (json/write-str (slr/predict-simple-linear d)))))))
