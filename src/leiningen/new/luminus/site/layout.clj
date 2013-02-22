(ns {{name}}.views.layout
  (:use hiccup.form
        [hiccup.def :only [defhtml]]
        [hiccup.element :only [link-to]]
        [hiccup.page :only [html5 include-js include-css]])
  (:require [noir.session :as session]))

(defn login-menu []
  (if (session/get :user)
    (form-to {:class "navbar-form"} [:post "/logout"]
             (submit-button {:class "btn"} "Logout"))

    (form-to {:class "navbar-form"} [:post "/login"]
             (text-field {:class "span2"
                          :style "margin-right: 5px"
                          :placeholder "user id"} "id")
             (password-field {:class "span2"
                              :style "margin-right: 5px"
                              :placeholder "password"} "pass")
             (submit-button {:class "btn"} "Login"))))

(defn header []
  [:div.navbar.navbar-fixed-top.navbar-inverse
   [:div.navbar-inner
    [:div.container
     [:ul.nav
      [:li (link-to "/" "Home")]
      [:li (link-to "/about" "About")]]
     [:ul.nav.pull-right
      [:li (login-menu)]
      [:li (if-not (session/get :user)
             (link-to "/register" "Register"))]]]]])

(defn footer []
  [:footer "Copyright &copy; ..."])

(defhtml base [& content]
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
