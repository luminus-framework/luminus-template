(ns leiningen.new.figwheel
  (:require
    [leiningen.new.cljs :refer [dev-cljsbuild uberjar-cljsbuild test-cljsbuild]]
    [leiningen.new.common :refer :all]
    [clojure.string :refer [join]]))

(def doo-version "0.1.11")
(def figwheel-version "0.5.20")

(def figwheel
  {:http-server-root "public"
   :server-logfile "log/figwheel-logfile.log"
   :nrepl-port       7002
   :css-dirs         ["resources/public/css"]
   :nrepl-middleware `[cider.piggieback/wrap-cljs-repl]})

(def figwheel-assets
  [["{{client-test-path}}/{{sanitized}}/doo_runner.cljs" "cljs/test/cljs/doo_runner.cljs"]
   ["env/dev/clj/{{sanitized}}/figwheel.clj" "cljs/env/dev/clj/figwheel.clj"]])

(def figwheel-dev-plugins
  [['lein-doo doo-version]
   ['lein-figwheel figwheel-version]])

(def figwheel-plugins
  [['lein-cljsbuild "1.1.7"]])

(def figwheel-dev-dependencies
  [['figwheel-sidecar figwheel-version]])

(defn figwheel-features [[assets options :as state]]
  (if (some #{"+figwheel"} (:features options))
    [(into assets figwheel-assets)
     (-> options
         (assoc :figwheel true
                :figwheel-uberjar-prep ":prep-tasks [\"compile\" [\"cljsbuild\" \"once\" \"min\"]]")
         (merge
           {:figwheel (indent root-indent (figwheel options))
            :dev-cljsbuild (indent dev-indent (dev-cljsbuild options))
            :test-cljsbuild (indent dev-indent (test-cljsbuild options))
            :uberjar-cljsbuild (indent uberjar-indent (uberjar-cljsbuild options))})
         (append-options :plugins figwheel-plugins)
         (append-options :dev-plugins figwheel-dev-plugins)
         (append-options :dev-dependencies figwheel-dev-dependencies))]
    state))
