(ns leiningen.new.auth-jwe
  (:require [leiningen.new.common :refer :all]))

(defn auth-jwe-features [[assets options :as state]]
  (if (some #{"+auth-jwe"} (:features options))
    [assets
     (-> options
         (assoc :auth true)
         (append-options :dependencies [['buddy "1.0.0"]])
         (append-formatted :auth-middleware-required
                           [['buddy.auth.middleware :refer ['wrap-authentication 'wrap-authorization]]
                            ['buddy.auth.backends.token :refer ['jwe-backend]]
                            ['buddy.sign.jwt :refer ['encrypt]]
                            ['buddy.core.nonce :refer ['random-bytes]]  
                            ['buddy.auth.accessrules :refer ['restrict]]
                            ['buddy.auth :refer ['authenticated? 'throw-unauthorized]]
                            ['clj-time.core :refer ['plus 'now 'minutes]]]
                           plugin-indent))]
    state))
