(ns leiningen.new.auth
  (:require [leiningen.new.common :refer :all]))

(defn auth-features [[assets options :as state]]
  (if (some #{"+auth"} (:features options))
    [assets
     (-> options
         (append-options :dependencies [['buddy "0.3.0"]])
         (append-formatted :auth-required
                           [['buddy.auth.middleware :refer ['wrap-authentication]]
                            ['buddy.auth.backends.session :refer ['session-backend]]]
                           plugin-indent)
         (assoc :auth-middleware "(wrap-authentication (session-backend))"))]
    state))
