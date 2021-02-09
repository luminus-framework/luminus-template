(ns leiningen.new.auth-jwe
  (:require [leiningen.new.common :refer :all]))

(defn auth-jwe-features [[assets options :as state]]
  (if (some #{"+auth-jwe"} (:features options))
    [assets
     (-> options         
         (append-formatted :auth-jwe
                           [['buddy.auth.backends.token :refer ['jwe-backend]]
                            ['buddy.sign.jwt :refer ['encrypt]]
                            ['buddy.core.nonce :refer ['random-bytes]]]
                           plugin-indent))]
    state))
