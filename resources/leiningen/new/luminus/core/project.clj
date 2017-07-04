(defproject <<name>> "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [<<dependencies>>]

  :min-lein-version "<<min-lein-version>>"

  :jvm-opts ["-server" "-Dconf=.lein-env"]<% if resource-paths %>
  :source-paths <<source-paths>>
  :test-paths ["test/clj"]
  :resource-paths <<resource-paths>><% endif %>
  :target-path "target/%s/"
  :main ^:skip-aot <<project-ns>>.core<% if migrations %>
  :migratus <<migrations>><% endif %>

  :plugins [[lein-cprop "1.0.3"]<% if plugins %>
            <<plugins>><% endif %>]<% if cucumber-feature-paths %>
  :cucumber-feature-paths <<cucumber-feature-paths>><% endif %><% if sassc-config-params %>
  <<sassc-config-params>>
  <<sassc-auto-config>>
  :hooks [leiningen.sassc]<% endif %><% if uberwar-options %>
  :uberwar
  <<uberwar-options>><% endif %><% if clean-targets %>
  :clean-targets ^{:protect false}
  <<clean-targets>><% endif %><% if cljs %>
  :figwheel
  <<figwheel>><% endif %>

  :profiles
  {:uberjar {:omit-source true<% if cljs %>
             <<cljs-uberjar-prep>>
             :cljsbuild
             <<uberjar-cljsbuild>>
             <% endif %>
             :aot :all
             :uberjar-name "<<name>>.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:dependencies [[prone "1.1.4"]
                                 [ring/ring-mock "0.3.1"]
                                 [ring/ring-devel "1.6.1"]<%if war %>
                                 <<dev-http-server-dependencies>><% endif %>
                                 [pjstadig/humane-test-output "0.8.2"]<% if dev-dependencies %>
                                 <<dev-dependencies>><% endif %>]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.19.0"]<% if dev-plugins %>
                                 <<dev-plugins>><% endif %>]<% if cljs %>
                  :cljsbuild
                  <<dev-cljsbuild>>
                  <% endif %>
                  <% if cljs-test %>
                  :doo <<cljs-test>><% endif %>
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:resource-paths ["env/test/resources"]<% if cljs %>
                  :cljsbuild
                  <<test-cljsbuild>>
                  <% endif %>}
   :profiles/dev {}
   :profiles/test {}})
