(defproject <<name>> "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [<<dependencies>>]

  :min-lein-version "<<min-lein-version>>"
  <% if resource-paths %>
  :source-paths <<source-paths>>
  :test-paths ["test/clj"]
  :resource-paths <<resource-paths>><% endif %>
  :target-path "target/%s/"
  :main ^:skip-aot <<project-ns>>.core

  :plugins [<% if plugins %><<plugins>><% endif %>]<% if cucumber-feature-paths %>
  :cucumber-feature-paths <<cucumber-feature-paths>><% endif %><% if sassc-config-params %>
  <<sassc-config-params>>
  <<sassc-auto-config>>
  :hooks [leiningen.sassc]<% endif %><% if uberwar-options %>
  :uberwar
  <<uberwar-options>><% endif %><% if clean-targets %>
  :clean-targets ^{:protect false}
  <<clean-targets>><% endif %><% if cljs %>
  <% if shadow-cljs %>:shadow-cljs
  <<shadow-cljs-config>>
  :npm-deps [<<npm-deps>>]<% else %>:figwheel
  <<figwheel>><% endif %><% endif %>

  :profiles
  {:uberjar {:omit-source true<% if cljs %>
             <<cljs-uberjar-prep>>
             <% if not shadow-cljs %>:cljsbuild<% endif %><<uberjar-cljsbuild>><% endif %>
             :aot :all
             :uberjar-name "<<name>>.jar"
             :source-paths ["env/prod/clj"<% if shadow-cljs %> "env/prod/cljs"<% endif %>]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"<% for opt in opts %> <<opt>><% endfor %>]
                  :dependencies [<<dev-dependencies>>]
                  :plugins      [<<dev-plugins>>]<% if cljs %>
                  <% if not shadow-cljs %>:cljsbuild<% endif %><<dev-cljsbuild>><% endif %>
                  <% if cljs-test %>
                  :doo <<cljs-test>><% endif %>
                  :source-paths ["env/dev/clj"<% if shadow-cljs %> "env/dev/cljs"<% endif %>]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"<% for opt in opts %> <<opt>><% endfor %>]
                  :resource-paths ["env/test/resources"]<% if cljs %>
                  <% if not shadow-cljs %>:cljsbuild <% endif %>
                  <<test-cljsbuild>>
                  <% endif %>}
   :profiles/dev {}
   :profiles/test {}})
