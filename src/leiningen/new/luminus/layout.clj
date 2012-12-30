(ns {{name}}.views.layout
  (:use hiccup.form
        [hiccup.element :only [link-to]] 
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
     (include-css "/css/screen.css")]     
    [:body content]))

(defn common [& content]
  (base
    (header)
    [:div#content content]
    (footer)))