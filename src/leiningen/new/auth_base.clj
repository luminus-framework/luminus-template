(ns leiningen.new.auth-base
  (:require [leiningen.new.common :refer :all]))

(defn auth-base-features [[assets options :as state]]
  (if (some #{"+auth-base"} (:features options))
    [assets
     (-> options
         (assoc :auth true)
         (append-options :dependencies [['buddy/buddy-auth "3.0.323"]
                                        ['buddy/buddy-sign "3.4.333"]
                                        ['buddy/buddy-core "1.10.413"]
                                        ['buddy/buddy-hashers "1.8.158"]])
         (append-formatted :auth-middleware-required
                           [['buddy.auth.middleware :refer ['wrap-authentication 'wrap-authorization]]
                            ['buddy.auth.accessrules :refer ['restrict]]
                            ['buddy.auth :refer ['authenticated?]]]
                           plugin-indent))]
    state))
