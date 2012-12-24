(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [lib-luminus "0.1.5"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [bultitude "0.1.7"]
                 [com.taoensso/timbre "1.1.0"]]
  :plugins [[lein-ring "0.7.5"]]
  :ring {:handler {{name}}.handler/war-handler}
  :main {{name}}.server
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]
                        [ring/ring-devel "1.1.0"]]}})
