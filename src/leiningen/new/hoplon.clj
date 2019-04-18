(ns leiningen.new.hoplon
  (:require [leiningen.new.common :refer :all]))

(def hoplon-assets
  [["{{client-path}}/{{sanitized}}/core.cljs" "hoplon/src/cljs/core.cljs"]
   ["{{client-path}}/{{sanitized}}/ajax.cljs" "hoplon/src/cljs/ajax.cljs"]])

(def hoplon-dependencies
  [['hoplon "7.2.0"]
   ['cljsjs/jquery "3.2.1-0"]
   ['clj-commons/secretary "1.2.4"]
   ['cljs-ajax "0.8.0"]])

(defn hoplon-features [[assets options :as state]]
  (if (some #{"+hoplon"} (:features options))
    [(into (remove-conflicting-assets assets "core.cljs") hoplon-assets)
     (-> options
         (assoc :hoplon true)
         (append-options :dependencies hoplon-dependencies))]
    state))
