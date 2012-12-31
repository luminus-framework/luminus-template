(ns {{name}}.views.layout
  (:use hiccup.form
        [hiccup.element :only [link-to]]        
        [hiccup.page :only [html5 include-js include-css]])
  (:require [noir.session :as session]))

(defn login-menu []
  (if (session/get :user)    
    (form-to [:post "/logout"] 
             (submit-button "logout"))
    
    (form-to [:post "/login"] 
             (text-field {:placeholder "user id"} "id")
             (password-field {:placeholder "password"} "pass")
             (submit-button "login"))))

(defn header []  
  [:div.navbar.navbar-fixed-top.navbar-inverse            
     [:div.navbar-inner 
      [:div.container
       [:ul.nav
        [:li (link-to "/" "Home")]
        [:li (link-to "/about" "About")]
        [:li (login-menu)]
        [:li (if-not (session/get :user) 
               (link-to "/register" "register"))]]]]])

(defn footer []
  [:footer "Copyright &copy; ..."])

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