(ns {{name}}.common
  (:use [hiccup.page :only [html5 include-css]]))

(defn layout [& body]
  (html5 
    [:head
     [:title "Welcome to {{name}}"]
     (include-css "/css/bootstrap.min.css"
                  "/css/bootstrap-responsive.min.css"
                  "/css/screen.css")     
     (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"
                 "/js/bootstrap.min.js")]
    (into [:body] body)))
