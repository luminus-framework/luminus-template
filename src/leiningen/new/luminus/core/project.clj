(defproject <<name>> "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring-server "0.3.1"]
                 [selmer "0.8.0"]
                 [com.taoensso/timbre "3.3.1"]
                 [com.taoensso/tower "3.0.2"]
                 [markdown-clj "0.9.63"]
                 [environ "1.0.0"]
                 [im.chit/cronj "1.4.3"]
                 [compojure "1.3.2"]
                 [ring/ring-defaults "0.1.3"]
                 [ring/ring-session-timeout "0.1.0"]
                 [ring-middleware-format "0.4.0"]
                 [noir-exception "0.2.3"]
                 [crypto-password "0.1.3"]
                 [bouncer "0.3.2"]
                 [prone "0.8.0"]
                 <<dependencies>>]

  :min-lein-version "<<min-lein-version>>"
  :uberjar-name "<<name>>.jar"
  :repl-options {:init-ns <<name>>.handler}
  :jvm-opts ["-server"]

  <% if main %>
  :main <<main>>
  <% endif %>

  :plugins [[lein-ring "0.9.1"]
            [lein-environ "1.0.0"]
            [lein-ancient "0.6.0"]
            <<plugins>>]
  <% if cucumber-feature-paths %>
  :cucumber-feature-paths <<cucumber-feature-paths>>
  <% endif %>

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
                        [pjstadig/humane-test-output "0.6.0"]
                        <<dev-dependencies>>]
         <% if dev-source-paths %>
         :source-paths <<dev-source-paths>>
         <% endif %>
         <% if dev-plugins %>
         :plugins <<dev-plugins>>
         <% endif %>
        <<cljs-dev>>
         <% if figwheel %>
         :figwheel
         <<figwheel>>
         <% endif %>
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]
         :env {:dev true}}})
