;; # Grokking [pim](https://pimbook.org/)
^{:nextjournal.clerk/toc true
  :nextjournal.clerk/visibility {:code :hide :result :hide}}
(ns rollacaster.pim-book
    (:require [mentat.clerk-utils.show :refer [show-sci]]
              [nextjournal.clerk :as clerk]))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(clerk/eval-cljs
 '(require '[reagent.core :as reagent])
 '(require '[jsxgraph.core :as jsx])
 '(require '[mathlive.core :as ml])
 '(require '[rollacaster.custom :as custom]))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(show-sci
 [:style "
math-field {
  font-size: 24px;
}
math-field:focus-within {
  outline: none;
}"])

;; ## Theorem 2.2.

^{:nextjournal.clerk/visibility {:code :hide :result :show}}
(show-sci
 [:div.flex.flex-wrap.items-center.gap-2
  [:span "For any integer"]
  [:math-field {:read-only true :style {:display "inline-block"}}
   "n >= 0"]
  [:span "and any list of"]
  [:math-field {:read-only true :style {:display "inline-block"}}
   "n + 1"]
  [:span "points"]
  [:math-field {:read-only true :style {:display "inline-block"}}
   "(x_1,y_1),(x_2,y_2),...,(x_{n+1},y_{n+1})"]
  [:span "in"]
  [:math-field {:read-only true :style {:display "inline-block"}}
   "\\reals^2"]
  [:span "with"]
  [:math-field {:read-only true :style {:display "inline-block"}}
   "x_1 < x_2 < ... < x_{n+1}"]
  [:span "there exits a unique polynomial "]
  [:math-field {:read-only true :style {:display "inline-block"}}
   "p(x)"]
  [:span "of degree at most n such that"]
  [:math-field {:read-only true :style {:display "inline-block"}}
   "p(x_i) = y_i"]
  [:span "for all"]
  [:math-field {:read-only true :style {:display "inline-block"}}
   "i"]
  [:span "."]])

^{:nextjournal.clerk/visibility {:code :hide :result :show}}
(show-sci
 (reagent/with-let
   [!tex      (reagent/atom "\\sum_{i=1}^{n+1}y_i\\left(\\prod_{j\\ne i}\\frac{x-x_j}{x_i-x_j}\\right)")
    on-change #(reset! !tex (.getValue (.-target %)))]
   [:<>
    [ml/Mathfield
     {:value     @!tex
      :on-change on-change}]]))

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
   [jsx/JSXGraph {:boundingbox [0 5 5 -0.2] :axis true}
    [jsx/FunctionGraph {:parents [f 0 5]}]
    [custom/Points {:points @!points :update-points (fn [k p] (swap! !points assoc k p))}]]))
