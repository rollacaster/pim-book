;; # Grokking to [pim](https://pimbook.org/)

^{:nextjournal.clerk/toc true
  :nextjournal.clerk/visibility {:code :hide :result :hide}}
(ns rollacaster.pim-book
    (:require [mentat.clerk-utils.show :refer [show-sci]]
              [nextjournal.clerk :as clerk]))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(clerk/eval-cljs
 '(require '[reagent.core :as reagent])
 '(require '[jsxgraph.core :as jsx])
 '(require '[mathlive.core :as ml]))

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


;; The following snippet implements the example from the ["Archimedean
;; Spiral"](https://jsxgraph.mentat.org/#archimedean-spiral) section of
;; the [`JSXGraph.cljs` docs
;; notebook](https://jsxgraph.mentat.org/#archimedean-spiral).

;; This example allows for interactive exploration of the [Archimedean
;; Spiral](https://en.wikipedia.org/wiki/Archimedean_spiral). This is a curve of
;; the form
;;
;; $$r=a + b \cdot \theta.$$
;;
;; The board below includes interactive sliders for the $a$ and $b$ parameters,
;; and plots a polar curve for every pair of $(r, \theta)$. See the comments in
;; the source code for details on our choices.
;;
;; > The original example lives at [this
;; > page](http://jsxgraph.org/wiki/index.php/Archimedean_spiral).

(show-sci
 ;; The `!state` atom is populated with the initial slider positions. Note that
 ;; we are NOT using a `reagent/atom`, because we don't need Reagent to perform
 ;; any re-renders when the state changes. Instead, state changes are picked up
 ;; by the function we provide to the curve below.
 (let [!state (atom {:a 1 :b 0.25})]
   [jsx/JSXGraph
    {:boundingbox [-10 10 10 -10]
     :showCopyright false
     ;; This option prevents the spiral from bulging out on the left and right
     ;; side on wider windows by adjusting the provided `:boundingBox`.
     :keepAspectRatio true}

    ;; Just for fun we are using the keyword form here to define components.
    [:slider
     {:name "a"
      ;; The `:parents` are of the form
      ;;
      ;; [<left-point> <right-point> [<min> <current> <max>]]
      :parents [[1 8] [5 8] [0 (:a @!state) 4]]

      ;; Each slider updates a value stored in `!state` above.
      :on {:drag #(swap! !state assoc :a (.Value %))}}]
    [:slider
     {:name "b"
      :parents [[1 9] [5 9] [0 (:b @!state) 4]]
      :on {:drag #(swap! !state assoc :b (.Value %))}}]
    [:curve
     {:id "c"
      ;; [<r(phi)>, <origin-coords>, <min-phi>, <max-phi>]
      :parents [(fn [phi]
                  (let [{:keys [a b]} @!state]
                    (+ a (* b phi))))
                [0 0] 0 (* 8 Math/PI)]
      :curveType "polar"
      :strokewidth 4}]
    ;; Note here that the parents of these elements reference the string-based
    ;; IDs of the element they want to target, instead of the actual
    ;; instance (like in the original example)
    [:glider  {:parents ["c"] :name "g"}]
    [:tangent {:parents ["g"] :dash 2 :strokeColor "#a612a9"}]
    [:normal  {:parents ["g"] :dash 2 :strokeColor "#a612a9"}]]))

;; One potential wrinkle to note when using `JSXGraph.cljs` in Clerk is that you
;; have to define any function you'd like to use over on the ClojureScript side.
;; You won't be able to use this function (defined on the JVM) with JSXGraph:

(defn square [x]
  (* x x))

;; But _this_ version will work, since it's wrapped in
;; `mentat.clerk-utils.show/show-sci`, and is therefore evaluated on the browser
;; side:

(show-sci
 (defn square [x]
   (* x x)))

;; ## JSXGraph Clerk Viewer

;; Here's an example of a viewer that lets us instantiate a `JSXGraph.cljs`
;; graph using data from the JVM. This viewer takes a map-shaped argument from
;; the JVM that specifies the endpoints of an arrow with `:start` and `:end`
;; keys:

(def arrow-viewer
  {:transform-fn clerk/mark-presented
   :render-fn
   '(fn [{:keys [start end]}]
      [jsx/JSXGraph {:boundingbox [-3 3 3 -3] :axis true}
       [jsx/Point {:name "A" :size 1 :parents start}]
       [jsx/Point {:id "B" :name "BEE!" :size 1 :parents end}]
       [jsx/Arrow {:size 4
                   :parents ["A" "B"]}]])})

;; We can apply it here, and see a scene with those endpoints:
^{::clerk/viewer arrow-viewer}
{:start [-1 1]
 :end [2 -1]}

;; We can re-use this viewer with different endpoints:

^{::clerk/viewer arrow-viewer}
{:start [-2 -2]
 :end [2 2.2]}
