;; # Grokking [pim](https://pimbook.org/)
^{:nextjournal.clerk/toc true
  :nextjournal.clerk/visibility {:code :hide :result :hide}}
(ns rollacaster.pim-book
    (:require [mentat.clerk-utils.show :refer [show-sci]]
              [nextjournal.clerk :as clerk]
              [rollacaster.polynomial :as polynomial]))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(clerk/eval-cljs
 '(require '[reagent.core :as reagent])
 '(require '[jsxgraph.core :as jsx])
 '(require '[mathlive.core :as ml])
 '(require '[rollacaster.custom :as custom]))

;; ## Theorem 2.2.

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(def math-definition
  {:transform-fn clerk/mark-presented
   :render-fn
   '(fn [parts]
      [:div.flex.flex-wrap.items-center.gap-2.mb-4
       (map
        (fn [part]
          (cond (string? part)
                [:span part]
                (= (first part) :math)
                [:math-field {:read-only true :style {:display "inline-block"}}
                 (second part)]))
        parts)])})

^{::clerk/viewer math-definition
  :nextjournal.clerk/visibility {:code :hide :result :show}}
["For any integer" [:math "n >= 0"] "and any list of" [:math "n + 1"] "points" [:math "(x_1,y_1),(x_2,y_2),...,(x_{n+1},y_{n+1})"]
 "in" [:math "\\reals^2"] "with" [:math "x_1 < x_2 < ... < x_{n+1}"] "there exits a unique polynomial " [:math "p(x)"]
 "of degree at most n such that" [:math "p(x_i) = y_i"] "for all" [:math "i"] "."]

^{::clerk/sync true
  ::clerk/visibility {:code :hide :result :hide}}
(defonce !points
  (atom {:a [1 2]
         :b [2 3]
         :c [3 1]
         :d [3.5 1]}))

^{:nextjournal.clerk/visibility {:code :hide :result :show}}
(show-sci
 (let [points (vals @!points)
       f (fn [x]
           (reduce
            (fn [result [x_i y_i]]
              (+ result
                 (* y_i (reduce
                         (fn [result x_j]
                           (* result (/ (- x x_j) (- x_i x_j))))
                         1
                         (remove #{x_i} (map first points))))))
            0
            points))]
   [jsx/JSXGraph {:boundingbox [0 5 5 -0.5] :axis true}
    [jsx/FunctionGraph {:parents [f 0 5]}]
    [custom/Points {:points @!points :update-points (fn [k p] (swap! !points assoc k p))}]]))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(def math-field
  {:transform-fn clerk/mark-presented
   :render-fn
   '(fn [mathjson]
      [:math-field {:read-only true}
       mathjson])})

^{::clerk/viewer math-field
  :nextjournal.clerk/visibility {:code :hide :result :show}}
(polynomial/show [3 2 1 0 2])
