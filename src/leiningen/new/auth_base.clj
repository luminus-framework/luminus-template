(ns leiningen.new.auth-base
  (:require [leiningen.new.common :refer :all]))

(defn auth-base-features [[assets options :as state]]
  (if (some #{"+auth-base"} (:features options))
    [assets
     (-> options
         (assoc :auth true)
         (append-options :dependencies [['buddy/buddy-auth "2.2.0"]
                                        ['buddy/buddy-sign "3.3.0"]
                                        ['buddy/buddy-core "1.9.0"]
                                        ['buddy/buddy-hashers "1.7.0"]])
         (append-formatted :auth-middleware-required
                           [['buddy.auth.middleware :refer ['wrap-authentication 'wrap-authorization]]
                            ['buddy.auth.accessrules :refer ['restrict]]
                            ['buddy.auth :refer ['authenticated?]]]
                           plugin-indent))]
    state))
