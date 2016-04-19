(defproject <<name>> "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [selmer "1.0.4"]
                 [markdown-clj "0.9.88"]
                 [ring-middleware-format "0.7.0"]
                 [metosin/ring-http-response "0.6.5"]
                 [bouncer "1.0.0"]
                 [org.webjars/bootstrap "4.0.0-alpha.2"]
                 [org.webjars/font-awesome "4.6.1"]
                 [org.webjars.bower/tether "1.1.1"]<% if not cljs %>
                 [org.webjars/jquery "2.2.2"]<% endif %>
                 [org.clojure/tools.logging "0.3.1"]
                 [compojure "1.5.0"]
                 [ring-webjars "0.1.1"]
                 [ring/ring-defaults "0.2.0"]<% if not immutant-session %>
                 [luminus/ring-ttl-session "0.3.1"]<% endif %>
                 [mount "0.1.10"]
                 [cprop "0.1.7"]
                 [org.clojure/tools.cli "0.3.3"]
                 [luminus-nrepl "0.1.4"]
                 <<http-server-dependencies>>
                 <<dependencies>>]

  :min-lein-version "<<min-lein-version>>"

  :jvm-opts ["-server" "-Dconf=.lein-env"]<% if resource-paths %>
  :source-paths <<source-paths>>
  :resource-paths <<resource-paths>><% endif %>

  :main <<project-ns>>.core<% if migrations %>
  :migratus <<migrations>><% endif %>

  :plugins [[lein-cprop "1.0.1"]<% if plugins %>
            <<plugins>><% endif %>]<% if cucumber-feature-paths %>
  :cucumber-feature-paths <<cucumber-feature-paths>><% endif %><% if sassc-config-params %>
  <<sassc-config-params>>
  <<sassc-auto-config>>
  :hooks [leiningen.sassc]<% endif %><% if uberwar-options %>
  :uberwar
  <<uberwar-options>><% endif %><% if clean-targets %>
  :clean-targets ^{:protect false}
  <<clean-targets>><% endif %><% if cljs-build %>
  :cljsbuild
  <<cljs-build>><% endif %>
  :target-path "target/%s/"
  :profiles
  {:uberjar {:omit-source true
             <% if cljs-uberjar %>
             <<cljs-uberjar>><% endif %>
             :aot :all
             :uberjar-name "<<name>>.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}
   :dev           [:project/dev :profiles/dev]
   :test          [:project/test :profiles/test]
   :project/dev  {:dependencies [[prone "1.1.1"]
                                 [ring/ring-mock "0.3.0"]
                                 [ring/ring-devel "1.4.0"]<%if war %>
                                 <<dev-http-server-dependencies>><% endif %>
                                 [pjstadig/humane-test-output "0.8.0"]<% if dev-dependencies %>
                                 <<dev-dependencies>><% endif %>]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.14.0"]<% if dev-plugins %>
                                 <<dev-plugins>><% endif %>]
                  <% if cljs-dev %>
                  <<cljs-dev>><% endif %>
                  <% if figwheel %>:figwheel
                  <<figwheel>><% endif %><% if cljs-test %>:doo <<cljs-test>><% endif %>
                  :source-paths ["env/dev/clj" "test/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user<% if figwheel %>
                                 :nrepl-middleware
                                 [cemerick.piggieback/wrap-cljs-repl]<% endif %>}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:resource-paths ["env/dev/resources" "env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}})
