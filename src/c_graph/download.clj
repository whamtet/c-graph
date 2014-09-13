(ns c-graph.download)
(import java.io.File)
(require '[clojure.data.json :as json])

(def characters (set (flatten (map #(seq (.split % " , ")) (clojure.string/split-lines (slurp "characters.txt"))))))

(defn download [character]
  (let [
        f (File. (format "characters/%s.json" character))
        ]
    (when-not (.exists f)
      (println "downloading" character "...")
      (spit f
            (slurp (format "http://glosbe.com/gapi/translate?from=zho&dest=eng&format=json&phrase=%s&pretty=true" character))
            ))))

(defn get-def [character]
  (let [
        f (format "characters/%s.json" character)
        result (json/read-str (slurp f))
        results (reduce
                 (fn [l {{text "text"} "phrase"}]
                   (if (empty? text)
                     l
                     (conj l text)))
                 #{} (result "tuc"))
        ]
    results))

(defn get-pinyin [character]
  (let [
        f (format "pinyin/%s.json" character)
        x (-> f slurp json/read-str (get "pinyin"))
        ]
    x))

;(spit "pinyin.txt" (pr-str (zipmap characters (pmap get-pinyin characters))))
