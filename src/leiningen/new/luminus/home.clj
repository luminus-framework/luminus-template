(ns {{name}}.home
  (:require [{{name}}.views.layout :as layout])

(defn home [] 
  (layout/common
    [:h1 "Hello World!"]
    "This site sure could use some content..."))
