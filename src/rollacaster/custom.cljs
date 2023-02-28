(ns rollacaster.custom
  (:require [jsxgraph.core :as jsx]))

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
