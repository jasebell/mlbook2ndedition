(ns ch04.core
  (:require [kixi.stats.core :as ks]
            [clojure.string :as s])
  (:gen-class))

(defn load-file [filepath]
  (map (fn [v] (Double/parseDouble v))
       (-> (slurp filepath)
           (s/split #"\n"))))

(defn find-min-value [v]
  (apply min v))

(defn find-max-value [v]
  (apply max v))

(defn find-sum [v]
  (apply + v))

(defn basic-arithmetic-mean [v]
  (/ (find-sum v) (count v)))

(defn harmonic-mean [v]
  (transduce identity ks/harmonic-mean v))

(defn geometric-mean [v]
  (transduce identity ks/geometric-mean v))

(defn find-mode [v]
  (map first (second
              (last
               (sort
                (group-by second
                          (frequencies v)))))))

(defn find-median [v]
  (transduce identity ks/median v))

(defn find-range [v]
  (- (find-max-value v) (find-min-value v)))

(defn interquartile-range [v]
  (transduce identity ks/iqr v))

(defn find-variance [v]
  (transduce identity ks/variance v))

(defn find-standard-deviation [v]
  (transduce identity ks/standard-deviation v))
