(ns {{name}}.views.layout
  (:require [selmer.parser :as parser]
            [noir.session :as session]
            [ring.util.response :refer [response]]
            compojure.response)
  (:import compojure.response.Renderable))

(def template-path "{{sanitized}}/views/templates/")

(deftype RenderableTemplate [template params]
  Renderable
  (render [this request]
    (->> (assoc params
                :servlet-context (:context request)
                :user-id (session/get :user-id))
         (parser/render-file (str template-path template))
         response)))

(defn render [template & [params]]
  (RenderableTemplate. template params))