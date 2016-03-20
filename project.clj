(defproject clojure-rewriting-system "0.1.0-SNAPSHOT"
  :description "Rewriting systems"
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :main ^:skip-aot clojure-rewriting-system.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[midje "1.8.2"
                                   :exclusions [org.clojure/clojure]]
                                  [org.clojure/tools.nrepl "0.2.12"]]
                   :plugins [[lein-midje "3.2"]
                             [lein-codox "0.9.0"]]}
             :midje {}})
