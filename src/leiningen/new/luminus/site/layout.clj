(ns {{name}}.views.layout
  (:use noir.request)
  (:require [selmer.parser :as parser]
            [noir.session :as session]))

(def template-path "{{sanitized}}/views/templates/")

(defn render [template & [params]]
  (parser/render-file (str template-path template)
                      (assoc (or params {})
                        :context (:context *request*)
                        :user-id (session/get :user-id))))
