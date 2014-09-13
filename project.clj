(defproject c-graph "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [reagent "0.4.2"]
                 [org.clojure/data.json "0.2.5"]
                 ]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "c-graph"
              :source-paths ["src"]
              :compiler {
                :output-to "c_graph.js"
                :output-dir "out"
                :optimizations :simple
;                :optimizations :none
                }}]})
