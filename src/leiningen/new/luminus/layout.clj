(ns {{name}}.views.layout
  (:require [selmer.parser :as parser]
            [clojure.string :as s]
            [ring.util.response :refer [content-type response]])
  (:import compojure.response.Renderable))

(def template-path "{{sanitized}}/views/templates/")

(deftype RenderableTemplate [template params]
  Renderable
  (render [this request]
    (content-type
      (->> (assoc params
                  :selected-page   (s/replace template #".html" "")
                  :servlet-context (:context request))
        (parser/render-file (str template-path template))
        response)
      "text/html; charset=utf-8")))

(defn render [template & [params]]
  (RenderableTemplate. template params))

