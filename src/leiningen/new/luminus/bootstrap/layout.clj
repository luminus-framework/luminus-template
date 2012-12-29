(ns {{name}}.layout
  (:use [hiccup.page :only [html5 include-css]]))

(defn base [& content]
  (html5 
    [:head
     [:title "Welcome to {{name}}"]
     (include-css "/css/bootstrap.min.css"
                  "/css/bootstrap-responsive.min.css"
                  "/css/screen.css")     
     (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"
                 "/js/bootstrap.min.js")]
    [:body content])

(defn common [content]
  (base [:body [:div.navbar
                [:a.brand {:href "/"} "Garbwell"]
                   [:ul.nav
                    [:li [:a {:href "/"} "Home"]]
                    [:li [:a {:href "/about/"} "About"]]]]
         [:div.content [:h1 "blah"] content]]
        ))
