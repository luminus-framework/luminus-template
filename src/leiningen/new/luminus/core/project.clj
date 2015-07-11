(defproject <<name>> "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [selmer "0.8.2"]
                 [com.taoensso/timbre "4.0.2"]
                 [com.taoensso/tower "3.0.2"]
                 [markdown-clj "0.9.67"]
                 [environ "1.0.0"]
                 [compojure "1.3.4"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-session-timeout "0.1.0"]
                 [ring "1.4.0"
                  :exclusions [ring/ring-jetty-adapter]]
                 [metosin/ring-middleware-format "0.6.0"]
                 [metosin/ring-http-response "0.6.3"]
                 [bouncer "0.3.3"]
                 [prone "0.8.2"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 <<dependencies>>]

  :min-lein-version "<<min-lein-version>>"
  :uberjar-name "<<name>>.jar"
  :jvm-opts ["-server"]

;;enable to start the nREPL server when the application launches
;:env {:repl-port 7001}

  :main <<project-ns>>.core<% if migrations %>
  :migratus <<migrations>><% endif %>

  :plugins [[lein-environ "1.0.0"]
            [lein-ancient "0.6.5"]<% if plugins %>
            <<plugins>><% endif %>]

  <% if cucumber-feature-paths %>:cucumber-feature-paths <<cucumber-feature-paths>><% endif %>
  <% if sassc-config-params %>
  :sassc <<sassc-config-params>>
  :hooks [leiningen.sassc]<% endif %>
  <% if ring-options %>
  :ring
  <<ring-options>><% endif %>
  <% if clean-targets %>:clean-targets ^{:protect false} <<clean-targets>><% endif %>
  <% if cljs-build %>
  :cljsbuild
  <<cljs-build>><% endif %>
  :profiles
  {:uberjar {:omit-source true
             :env {:production true}<% if cljs-uberjar %>
             <<cljs-uberjar>><% endif %>
             :aot :all}
   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.4.0"]
                        [pjstadig/humane-test-output "0.7.0"]<% if dev-dependencies %>
                        <<dev-dependencies>><% endif %>]
         <% if dev-plugins %>:plugins <<dev-plugins>><% endif %><% if cljs-dev %>
         <<cljs-dev>><% endif %>
         <% if figwheel %>:figwheel
         <<figwheel>><% endif %>
         :repl-options {:init-ns <<project-ns>>.core}
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]
         :env {:dev true}}})
