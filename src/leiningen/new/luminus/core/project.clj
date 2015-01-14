(defproject <<name>> "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring-server "0.3.1"]
                 [selmer "0.7.9"]
                 [com.taoensso/timbre "3.3.1"]
                 [com.taoensso/tower "3.0.2"]
                 [markdown-clj "0.9.62"]
                 [environ "1.0.0"]
                 [im.chit/cronj "1.4.3"]
                 [ring/ring-defaults "0.1.3"]
                 [ring/ring-session-timeout "0.1.0"]
                 [noir-exception "0.2.3"]
                 [crypto-password "0.1.3"]
                 [bouncer "0.3.1"]
                 [prone "0.8.0"]
                 <<cljs-dependencies>>
                 <<db-dependencies>>
                 ]
  :min-lein-version "<<min-lein-version>>"
  :uberjar-name "<<name>>.jar"
  :repl-options {:init-ns <<name>>.repl}
  :jvm-opts ["-server"]

  :plugins [[lein-ring "0.9.0"]
            [lein-environ "1.0.0"]
            [lein-ancient "0.5.5"]
            <<db-plugins>>
            <<cljs-plugins>>
            ]

  :ring {:handler <<name>>.handler/app
         :init    <<name>>.handler/init
         :destroy <<name>>.handler/destroy
         :uberwar-name "<<name>>.war"}
  <% if migrations %>
  :ragtime
  <<migrations>>
  <% endif %>

  <% if cljs-build %>
  :cljsbuild
  <<cljs-build>>
  <% endif %>
  :profiles
  {:uberjar {:omit-source true
             :env {:production true}
            <<cljs-uberjar>>
             :aot :all}
   :production {:ring {:open-browser? false
                       :stacktraces?  false
                       :auto-reload?  false}}
   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.3.2"]
                        [pjstadig/humane-test-output "0.6.0"]]
        <<cljs-dev>>
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]
         :env {:dev true}}})
