(ns {{name}}.views.layout
  (:use noir.request)
  (:require [clabango.parser :as parser]
            [noir.session :as session]))

(def template-path "{{name}}/views/templates/")

(defn render [template & [params]]
  (parser/render-file (str template-path template) 
                      (assoc (or params {})
                        :context (:context *request*)
                        :user    (session/get :user))))
