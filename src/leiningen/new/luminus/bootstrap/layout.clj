(ns {{name}}.views.layout
  (:use [hiccup.element :only [link-to]] 
        [hiccup.page :only [html5 include-css include-js]]))

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
      [:a.brand {:href "/"} "{{name}}"]
      [:div.navbar.navbar-fixed-top            
       [:ul.nav
        [:li (link-to "/" "Home")]
        [:li (link-to "/about" "About")]]]
      [:div#content content]))
