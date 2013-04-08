(ns {{name}}.routes.cljsexample
  (:require [compojure.core :refer :all]
            [noir.response :as response]
            [{{name}}.views.layout :as layout]))

(def messages
  [{:message "Hello world"
    :user    "Foo"}
   {:message "Ajax is fun"
    :user    "Bar"}])

(defroutes cljs-routes
  (GET "/cljsexample" [] (layout/render "cljsexample.html"))
  (GET "/messages" [] (response/edn messages)))