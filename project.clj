(defproject homework "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [metosin/compojure-api "1.1.11"]]
  :main ^:skip-aot homework.core
  :ring {:handler homework.handler/app}
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[cheshire "5.8.1"]
                                  [ring/ring-mock "0.4.0"]]
                   :plugins [[lein-cloverage "1.1.1"]
                             [lein-ring "0.12.5"]]}})
