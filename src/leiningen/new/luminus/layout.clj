(ns {{name}}.views.layout
  (:use noir.request)
  (:require [selmer.parser :as parser]))

(def template-path "{{sanitized}}/views/templates/")

(defn render [template & [params]]
  (parser/render-file (str template-path template)
                      (assoc params :servlet-context (:context *request*))))

