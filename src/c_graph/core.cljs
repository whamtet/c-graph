(ns c-graph.core
  (:use
        [cljs.reader :only [read-string]]
        )
  (:require
   [reagent.core :as reagent :refer [atom]]
   [c-graph.util :as util]
   )
  (:import [goog.net Jsonp]
           [goog Uri])
  )

(enable-console-print!)

(println "Hello world!")

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

(def character-atom (atom "一"))
(def english-atom (atom nil))
(def pinyin-atom (atom nil))

(defn translate [character]
  (let [
        english-url (util/format
                     "http://glosbe.com/gapi/translate?from=zho&dest=eng&format=json&phrase=%s&pretty=true"
                     character)
        english-parser
        (fn [result]
          (let [
                result (js->clj result)
                results (reduce
                         (fn [l {{text "text"} "phrase"}]
                           (if (empty? text)
                             l
                             (conj l text)))
                           #{} (result "tuc"))
                result (apply str (interpose "\n" results))
                ]
            (reset! english-atom result)))
        pinyin-url (str "http://json-zh.appspot.com/pinyin?hanzi=" character)
        pinyin-parser
        (fn [result]
          (reset! pinyin-atom ((js->clj result) "pinyin")))
        ]
    (.send (Jsonp. (Uri. english-url)) nil english-parser)
    (.send (Jsonp. (Uri. pinyin-url)) nil pinyin-parser)))

(defn render []
  (let [
        character @character-atom
        parents (parents character)
        children (children character)
        new-character (fn [character]
                        (reset! english-atom nil)
                        (reset! pinyin-atom nil)
                        (reset! character-atom character)
                        (translate character))
        span (fn [character]
               ^{:key character} [:span {:on-click #(new-character character)} character])
        title (if (and @pinyin-atom @english-atom)
                (str @pinyin-atom "\n" @english-atom))
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
      [:input {:type "text" :id "zi"}][:br]
      [:input {:type "submit"}]]
     ]))

(reagent/render-component
 [render]
 (.getElementById js/document "contents"))
(translate @character-atom)
