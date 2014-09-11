(ns c-graph.util)

(defn str-contains? [a b]
  (not= -1 (.indexOf a b)))

(defn format [s & subs]
  (loop [
         s s
         current (first subs)
         todo (rest subs)
         ]
    (if (and (str-contains? s "%s") current)
      (recur (.replace s "%s" current) (first todo) (rest todo))
      s)))
