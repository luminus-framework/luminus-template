(set-env!
 :dependencies '[<<dependencies>>]<% if resource-paths %>
 :source-paths <<source-paths>>
 :resource-paths <<resource-paths>><% endif %>)
(require '[clojure.java.io :as io]
         '[clojure.edn :as edn]
         '[adzerk.boot-test :refer [test]])
<% if cljs %>
(require '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl]])
<% endif %><% if sassc-config-params %>
(require '[deraen.boot-sass :refer [sass]])
<% endif %>
<% if migrations %>
(require '[luminus.boot-migratus :refer [migratus]])
<% endif %>
(deftask dev
  "Enables configuration for a development setup."
  []
  (set-env!
   :source-paths #(conj % "env/dev/clj"<% if cljs %> <<dev-cljs.source-paths>><% endif %>)
   :resource-paths #(conj % "env/dev/resources")
   :dependencies #(concat % '[[prone "1.1.4"]
                              [ring/ring-mock "0.3.0"]
                              [ring/ring-devel "1.6.1"]<% if war %>
                              <<dev-http-server-dependencies>><% endif %>
                              [pjstadig/humane-test-output "0.8.2"]<% if dev-dependencies %>
                              <<dev-dependencies>><% endif %>]))
  (require 'pjstadig.humane-test-output)
  (let [pja (resolve 'pjstadig.humane-test-output/activate!)]
    (pja))
  (.. System (getProperties) (setProperty "conf" "dev-config.edn"))
  identity)

(deftask testing
  "Enables configuration for testing."
  []
  (dev)
  (set-env! :resource-paths #(conj % "env/test/resources"))<% if cljs %>
  (merge-env! :source-paths <<dev-cljs.test.source-paths>>)<% endif %>
  (.. System (getProperties) (setProperty "conf" "test-config.edn"))
  identity)

(deftask prod
  "Enables configuration for production building."
  []
  (merge-env! :source-paths #{"env/prod/clj"<% if cljs %> "env/prod/cljs"<% endif %>}
              :resource-paths #{"env/prod/resources"})
  identity)

(deftask start-server
  "Runs the project without building class files.

  This does not pause execution. Combine with a wait task or use the \"run\"
  task."
  []
  (require '<<project-ns>>.core)
  (let [-main (resolve '<<project-ns>>.core/-main)]
    (with-pass-thru _
      (apply -main *args*))))

(deftask run
  "Starts the server and causes it to wait."
  []
  (comp
   (apply start-server *args*)
   (wait)))
<% if cljs %>
(require '[clojure.java.io :as io])
(require '[crisptrutski.boot-cljs-test :refer [test-cljs]])
(deftask figwheel
  "Runs figwheel and enables reloading."
  []
  (dev)
  (require '[powerlaces.boot-figreload :refer [reload]])
  (let [reload (resolve 'powerlaces.boot-figreload/reload)]
    (comp
     (start-server)
     (watch)
     (cljs-repl)
     (reload :client-opts {:debug true})
     (speak)
     (cljs))))

(deftask run-cljs-tests
  "Runs the doo tests for ClojureScript."
  []
  (comp
   (testing)
   (test-cljs :cljs-opts <<dev-cljs.test.compiler>>)))
<% endif %>
(deftask uberjar
  "Builds an uberjar of this project that can be run with java -jar"
  []
  (comp
   (prod)
   (aot :namespace #{'<<project-ns>>.core})<% if cljs %>
   (cljs :optimizations :advanced)<% endif %>
   (uber)
   (jar :file "<<name>>.jar" :main '<<project-ns>>.core)
   (sift :include #{#"<<name>>.jar"})
   (target)))
<% if war %>
(require '[boot.immutant :refer [gird]])
(deftask uberwar
  "Creates a war file ready to deploy to wildfly."
  []
  (comp
   (uber :as-jars true)
   (aot :all true)
   (gird :init-fn '<<project-ns>>.handler/init)
   (war)
   (target)))

(deftask dev-war
  "Creates a war file for development and testing."
  []
  (comp
   (dev)
   (gird :dev true :init-fn '<<project-ns>>.handler/init)
   (war)
   (target)))
<% endif %>
