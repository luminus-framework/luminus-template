(ns {{name}}.views.layout
  (:use noir.request)
  (:require [selmer.parser :as parser]
            [noir.session :as session]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(def template-path "{{sanitized}}/views/templates/")

(defn render [template & [params]]
  (parser/render-file (str template-path template)
                      (assoc params
                             :antiforgery (anti-forgery-field)
                             :servlet-context (:context *request*)
                             :user-id (session/get :user-id))))
