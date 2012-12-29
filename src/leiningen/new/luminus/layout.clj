(ns {{name}}.views.layout
  (:use [hiccup.element :only [link-to]]
        [hiccup.page :only [html5 include-css]]))

(defn header []
  [:div.navbar.navbar-fixed-top            
   [:ul.nav
    [:li (link-to "/" "Home")]
    [:li (link-to "/about" "About")]]])

(defn footer []
  [:footer "-=[{{name}}]=-"])

(defn common [& content]
  (html5 
    [:head
     [:title "Welcome to {{name}}"]
     (include-css "/css/screen.css")]
    [:body
     (header)
     [:div#content content]
     (footer)]))