(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [lib-noir "0.7.4"]
                 [compojure "1.1.5"]
                 [ring-server "0.3.0"]
                 [selmer "0.5.1"]
                 [com.taoensso/timbre "2.6.3"]
                 [com.postspectacular/rotor "0.1.0"]
                 [com.taoensso/tower "1.7.1"]
                 [markdown-clj "0.9.33"]]
  :plugins [[lein-ring "0.8.7"]]
  :ring {:handler {{name}}.handler/app
         :init    {{name}}.handler/init
         :destroy {{name}}.handler/destroy}
  :profiles
  {:production {:ring {:open-browser? false
                       :stacktraces?  false
                       :auto-reload?  false}}
   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.2.0"]]}})
