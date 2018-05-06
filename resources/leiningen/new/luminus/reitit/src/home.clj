(ns <<project-ns>>.routes.home
  (:require [<<project-ns>>.layout :as layout]
            [clojure.java.io :as io]
            [myapp.middleware :as middleware]))

(defn home-page [_]
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page [_]
  (layout/render "about.html"))

(defn home-routes []
  [["/" {:get {:handler    home-page
               :middleware [middleware/wrap-csrf
                            middleware/wrap-formats]}}]
   ["/about" {:get about-page}]])
