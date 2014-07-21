(ns {{name}}.routes.home
  (:require [{{name}}.layout :as layout]
            [compojure.core :refer :all]
            [noir.response :refer [edn]]
            [clojure.pprint :refer [pprint]]))

(defn save-document [doc]
      (pprint doc)
      {:status "ok"})

(defroutes home-routes
  (GET "/" [] (layout/render "app.html"))
  (POST "/save" {:keys [body-params]}
    (edn (save-document body-params))))
