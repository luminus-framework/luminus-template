(ns leiningen.new.oauth
  (:require [leiningen.new.common :refer :all]))

(def oauth-assets
  [["src/clj/{{sanitized}}/oauth.clj" "oauth/src/oauth.clj"]
   ["src/clj/{{sanitized}}/routes/oauth.clj" "oauth/src/routes.clj"]])

(defn oauth-features [[assets options :as state]]
  (if (some #{"+oauth"} (:features options))
    [(into assets oauth-assets)
     (-> options
         (append-options :dependencies [['clj-oauth "1.5.4"]])
         (assoc :oauth-required
                (indent require-indent
                        [[(symbol (str (:project-ns options) ".routes.oauth")) :refer ['oauth-routes]]])
                :oauth-routes
                (indent dev-indent ["#'oauth-routes"])))]
    state))
