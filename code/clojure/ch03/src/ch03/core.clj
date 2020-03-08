(ns ch03.core
  (:require [clojure.data.json :as json])
  (:gen-class))

(def baseurl "https://api.openweathermap.org/data/2.5/weather?q=London&APPID=")
(def apikey "Add you key here.....")

(defn get-json []
  (let [rawstring (slurp (str baseurl apikey))]
    (json/read-str rawstring :key-fn keyword)))
