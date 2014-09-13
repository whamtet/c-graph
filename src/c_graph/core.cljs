(ns c-graph.core
  (:use
        [cljs.reader :only [read-string]]
        )
  (:require
   [reagent.core :as reagent :refer [atom]]
   [c-graph.util :as util]
   ))

(enable-console-print!)

(def p #(-> % pr-str println))

(def characters (-> js/document (.getElementById "characters") .-innerHTML .trim))

(def children (into {} (for [line (.split characters "\n")]
                         (let [
                               line (.trim line)
                               cols (.split line " , ")
                               ]
                           [(first cols) (rest cols)]))))

(defn parents [node]
  (for [[parent children] children :when (some #(= node %) children)]
    parent))

(def translations (-> js/document (.getElementById "translations") .-innerHTML read-string))
(def pinyin (-> js/document (.getElementById "pinyin") .-innerHTML read-string))

(def character-atom (atom "一"))

(defn render []
  (let [
        character @character-atom
        parents (parents character)
        children (children character)
        new-character (fn [character]
                        (reset! character-atom character)
                        )
        span (fn [character]
               ^{:key character} [:span {:on-click #(new-character character)} character])
        title (str (pinyin character) "\n"
                   (apply str (interpose "\n" (translations character))))
        submit-form (fn []
                      (let [
                            character (.-value (.getElementById js/document "zi"))
                            ]
                        (new-character character)
                        false))
        ]
    [:div {:style {:text-align "center"}}
     [:div (map span parents)]
     [:div {:title title :style {:font-size "4em"}} character]
     [:div (map span children)]
     [:br]
     [:form {:on-submit submit-form}
      [:input {:type "text" :id "zi"}][:br][:br]
      [:input {:type "submit"}]]
     ]))

(reagent/render-component
 [render]
 (.getElementById js/document "contents"))

(println "Hello world!")
