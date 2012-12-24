(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [lib-luminus "0.1.4"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [bultitude "0.1.7"]
                 [clj-log "0.4.4"]]
  :plugins [[lein-ring "0.7.5"]]
  :ring {:handler {{name}}.handler/war-handler}
  :main {{name}}.server
  :dev-dependencies [[ring-mock "0.1.3"]])
