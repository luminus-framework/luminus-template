(ns leiningen.new.auth-jwe
  (:require [leiningen.new.common :refer :all]))

(defn auth-jwe-features [[assets options :as state]]
  (if (some #{"+auth-jwe"} (:features options))
    [assets
     (-> options         
         (append-options :dependencies [['tick "0.4.21-alpha"]])
         (append-formatted :auth-jwe
                           [['buddy.auth.backends.token :refer ['jwe-backend]]
                            ['buddy.sign.jwt :refer ['encrypt]]
                            ['buddy.core.nonce :refer ['random-bytes]]
                            ['tick.alpha.api :as 'tick]]
                           plugin-indent))]
    state))
