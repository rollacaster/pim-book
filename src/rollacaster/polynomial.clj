(ns rollacaster.polynomial
  (:require [clojure.string :as str]))

(defn add [p1 p2]
  ())

(defn show [p]
  (->> p
       (map-indexed vector)
       (reduce
        (fn [s [idx coefficent]]
          (cond
            (and (pos? coefficent) (zero? idx)) (conj s (str coefficent))
            (= idx 1) (conj s (str coefficent "x"))
            (pos? coefficent) (conj s (str coefficent "x^" idx) )
            :else s))
        [])
       (str/join " + ")))
