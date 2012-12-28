(ns {{name}}.common
  (:use hiccup.form
        [hiccup.element :only [link-to]] 
        [hiccup.page :only [html5 include-js include-css]])
  (:require [noir.session :as session]))
   
(defn header []
  [:header
   (if (session/get :user)    
     (form-to [:post "/logout"] 
              (submit-button "logout"))
     
     (form-to [:post "/login"] 
              (text-field {:placeholder "user id"} "id")
              (password-field {:placeholder "password"} "pass")
              (submit-button "login")
              [:div#register (link-to "/register" "register")]))])

(defn footer []
  [:footer "-=[{{name}}]=-"])

(defn base-layout [& content]
  (html5
    [:head
     [:title "Welcome to picture-gallery"]
     (include-css "/css/bootstrap.min.css"
                  "/css/bootstrap-responsive.min.css"
                  "/css/screen.css")     
     (include-js "http://code.jquery.com/jquery-1.7.2.min.js"
                 "/js/bootstrap.min.js")]
    [:body
     content]))

(defn layout [& content]
  (base-layout    
    (header)      
    [:div#content content]
    (footer)))