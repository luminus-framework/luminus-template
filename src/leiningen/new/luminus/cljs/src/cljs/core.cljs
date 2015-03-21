(ns <<project-ns>>.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary]
            [reagent.session :as session]
            [reagent-forms.core :refer [bind-fields]]
            [ajax.core :refer [POST]])
  (:require-macros [secretary.core :refer [defroute]]))

(defn navbar []
      [:div.navbar.navbar-inverse.navbar-fixed-top
       [:div.container
        [:div.navbar-header
         [:a.navbar-brand {:href "#/"} "<<name>>"]]
        [:div.navbar-collapse.collapse
         [:ul.nav.navbar-nav
          [:li {:class (when (= :home (session/get :page)) "active")}
           [:a {:on-click #(secretary/dispatch! "#/")} "Home"]]
          [:li {:class (when (= :about (session/get :page)) "active")}
           [:a {:on-click #(secretary/dispatch! "#/about")} "About"]]]]]])

(defn about-page []
  [:div "this is the story of <<name>>... work in progress"])

(defn home-page []
  [:div
   [:h2 "Welcome to ClojureScript"]])

(def pages
  {:home home-page
   :about about-page})

(defn page []
  [(pages (session/get :page))])

(defroute "/" [] (session/put! :page :home))
(defroute "/about" [] (session/put! :page :about))

(defn mount-components []
  (reagent/render-component [navbar] (.getElementById js/document "navbar"))
  (reagent/render-component [page] (.getElementById js/document "app")))

(defn init! []
  (secretary/set-config! :prefix "#")
  (session/put! :page :home)
  (mount-components))


