(ns {{name}}.routes.home
  (:require [compojure.core :refer :all]
            [clojure.java.io :as io]
            [{{name}}.layout :as layout]))

(defn home-page []
  (layout/render
    "home.html" {:content (slurp "/md/docs.md")}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))
