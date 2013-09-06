(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [lib-noir "0.6.9"]
                 [compojure "1.1.5"]
                 [ring-server "0.2.8"]
                 [selmer "0.4.2"]
                 [com.taoensso/timbre "2.6.1"]
                 [com.postspectacular/rotor "0.1.0"]
                 [com.taoensso/tower "1.7.1"]
                 [markdown-clj "0.9.31"]]
  :plugins [[lein-ring "0.8.6"]]
  :ring {:handler {{name}}.handler/war-handler
         :init    {{name}}.handler/init
         :destroy {{name}}.handler/destroy}
  :profiles
  {:production {:ring {:open-browser? false
                       :stacktraces?  false
                       :auto-reload?  false}}
   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.1.8"]]}})
