(ns rollacaster.custom
  (:require [clojure.string :as str]
            [jsxgraph.core :as jsx]))

(defn Points [{:keys [points update-points]}]
   [:<>
    (->> points
         (sort-by (comp first second))
         (map-indexed
          (fn [idx [k p]]
            ^{:key p}
            [jsx/Point {:name (str "(x_" (inc idx) " y_" (inc idx) ")")
                        :size 4 :parents p
                        :on {:update (fn [^js event]
                                       (let [[_ x y] (.-usrCoords event)]
                                         (update-points k [(parse-double (.toFixed x 2))
                                                           (parse-double (.toFixed y 2))])))}}])))])

(defn add [& polynomials]
  (reduce
   (fn [result polynomial]
     (->> (range (max (count polynomial) (count result)))
          (mapv (fn [idx] (+ (get result idx 0) (get polynomial idx 0))))))
   []
   polynomials))

(defn mul [p1 p2]
  (apply
   add
   (map-indexed
    (fn [degree term]
      (vec
       (reduce
        (fn [result [other-degree other-term]]
          (update result (+ degree other-degree) + (* term other-term)))
        (vec (repeat (+ degree (count p2)) 0))
        (map-indexed vector (reverse p2)))))
    (reverse p1))))

(comment
  (= (mul [2] [2]) [4])
  (= (mul [2 2] [2]) [4 4])
  (= (mul [2 2] [2 2]) [4 8 0])
  (= (mul [2 2 2] [2]) [4 4 4])
  (= (mul [2 2 2] [2 2]) [4 8 8 4]))

(defn points->poly [x_i x_j]
  [(/ (- x_j) (- x_i x_j)) (/ 1 (- x_i x_j))])

(defn show [p]
  (->> p
       (map-indexed vector)
       (reduce
        (fn [s [idx coefficent]]
          (let [formatted-coefficent (.toFixed coefficent 3)]
            (cond
              (and (not (zero? coefficent)) (zero? idx)) (conj s formatted-coefficent)
              (= idx 1) (conj s (str formatted-coefficent "x"))
              (not (zero? coefficent)) (conj s (str formatted-coefficent "x^" idx) )
              :else s)))
        [])
       (str/join " + ")))
