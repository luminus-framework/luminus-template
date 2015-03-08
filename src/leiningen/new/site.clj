(ns leiningen.new.site
  (:require [leiningen.new.common :refer :all]))

(def site-assets
  [])

(defn required-feature [features feature default]
  (if (some feature features)
    features
    (conj features default)))

(defn required-features [options]
  (update-in options
             [:features]
             #(-> % (required-feature #{"+cljs"} "+cljs")
                    (required-feature #{"+h2" "+mysql" "+postgres"} "+h2"))))

(defn site-features [[assets options :as state]]
  (if (some #{"+site"} (:features options))
    [(into (remove-conflicting-assets assets ".html") site-assets)
     (-> options
         required-features)]
    state))