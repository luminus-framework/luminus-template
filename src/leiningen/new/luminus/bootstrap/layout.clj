(ns {{name}}.views.layout
  (:use [hiccup.element :only [link-to]] 
        [hiccup.page :only [html5 include-js include-css]]))

(defn header []  
  [:div.navbar.navbar-fixed-top.navbar-inverse            
     [:div.navbar-inner 
      [:div.container
       [:ul.nav
        [:li (link-to "/" "Home")]
        [:li (link-to "/about" "About")]]]]])

(defn footer []
  [:footer "-=[{{name}}]=-"])

(defn base [& content]
  (html5 
    [:head
     [:title "Welcome to {{name}}"]
     (include-css "/css/bootstrap.min.css"
                  "/css/bootstrap-responsive.min.css"
                  "/css/screen.css")     
     (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"
                 "/js/bootstrap.min.js")]     
    [:body content]))

(defn common [& content]
  (base
    (header)
    [:div#content content]
    (footer)))