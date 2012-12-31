(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [lib-noir "0.3.2"]
                 [compojure "1.1.3"]
                 [hiccup "1.0.2"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [bultitude "0.1.7"]
                 [com.taoensso/timbre "1.1.0"]
                 [com.taoensso/tower "1.0.0"]
                 [markdown-clj "0.9.13"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [org.xerial/sqlite-jdbc "3.7.2"]]
  :plugins [[lein-ring "0.7.5"]]
  :ring {:handler {{name}}.handler/war-handler
         :init {{name}}.handler/init}
  :main {{name}}.server
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]
                        [ring/ring-devel "1.1.0"]]}})
