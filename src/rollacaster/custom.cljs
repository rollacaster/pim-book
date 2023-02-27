(ns rollacaster.custom
  (:require [jsxgraph.core :as jsx]))

;; ## Custom ClojureScript
;;
;; This namespace contains custom ClojureScript definitions used by the
;; {{raw/name}} project.

;; With the default configuration in `rollacaster.sci-extensions`, any function
;; or var you add here will be available inside your SCI viewers as
;; `custom/<fname>` or as the fully-qualified `rollacaster.custom/<fname>`.

(defn square
  "Returns the square of `x`."
  [x]
  (* x x))

(defn Points [{:keys [points update-points]}]
   [:<>
    (->> points
         (map-indexed
          (fn [idx p]
            ^{:key p}
            [jsx/Point {:size 4 :parents p :on {:update (fn [^js event]
                                                          (let [[_ x y] (.-usrCoords event)]
                                                            (update-points idx [(parse-double (.toFixed x 2))
                                                                                (parse-double (.toFixed y 2))])))}}])))])
