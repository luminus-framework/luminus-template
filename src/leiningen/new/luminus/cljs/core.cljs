(ns {{name}}.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary]
            [reagent-forms.core :refer [bind-fields]]
            [ajax.core :refer [POST]])
  (:require-macros [secretary.core :refer [defroute]]))

(def state (atom {:saved? false}))

(defn row [label & body]
  [:div.row
   [:div.col-md-2 [:span label]]
   [:div.col-md-3 body]])

(defn text-input [id label]
  (row label [:input.form-control {:field :text :id id}]))

(defn selection-list [label id & items]
  (row label
    [:div.btn-group {:field :multi-select :id id}
      (for [[k label] items]
        [:button.btn.btn-default {:key k} label])]))

(def form
  [:div
   [:div.page-header [:h1 "Reagent Form"]]
   (text-input :first-name "First name")
   (text-input :last-name "Last name")
   (selection-list "Favorite drinks" :favorite-drinks
                   [:coffee "Coffee"]
                   [:beer "Beer"]
                   [:crab-juice "Crab juice"])])

(defn save-doc [doc]
  (fn []
    (POST (str js/context "/save")
          {:params {:doc @doc}
           :handler (fn [_] (swap! state assoc :saved? true))})))

(defn about []
  [:div "this is the story of {{name}}... work in progress"])

(defn home []
  (let [doc (atom {})]
    (fn []
      [:div
       [bind-fields form doc
        (fn [_ _ _] (swap! state assoc :saved? false) nil)]
       (if (:saved? @state)
         [:p "Saved"]
         [:button {:type "submit"
                   :class "btn btn-default"
                   :onClick (save-doc doc)}
          "Submit"])])))

(defn navbar []
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand {:href "#/"} "{{name}}"]]
    [:div.navbar-collapse.collapse
     [:ul.nav.navbar-nav
      [:li {:class (when (= home (:page @state)) "active")}
       [:a {:on-click #(secretary/dispatch! "#/")} "Home"]]
      [:li {:class (when (= about (:page @state)) "active")}
       [:a {:on-click #(secretary/dispatch! "#/about")} "About"]]]]]])

(defn page []
  [(:page @state)])

(secretary/set-config! :prefix "#")

(defroute "/" []
          (.log js/console "hi!")
          (swap! state assoc :page home))
(defroute "/about" [] (swap! state assoc :page about))

(defn init! []
  (swap! state assoc :page home)
  (reagent/render-component [navbar] (.getElementById js/document "navbar"))
  (reagent/render-component [page] (.getElementById js/document "app")))


