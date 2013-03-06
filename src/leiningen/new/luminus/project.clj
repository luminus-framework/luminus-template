(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [lib-noir "0.4.9"]
                 [compojure "1.1.5"]
                 [clabango "0.4"]
                 [ring-server "0.2.7"]
                 [com.taoensso/timbre "1.5.2"]
                 [com.taoensso/tower "1.2.0"]
                 [markdown-clj "0.9.19"]]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler {{name}}.handler/war-handler
         :init    {{name}}.handler/init
         :destroy {{name}}.handler/destroy}
  :profiles
  {:production {:ring {:open-browser? false
                       :stacktraces?  false
                       :auto-reload?  false}}
   :dev {:dependencies [[ring-mock "0.1.3"]
                        [ring/ring-devel "1.1.8"]]}})
