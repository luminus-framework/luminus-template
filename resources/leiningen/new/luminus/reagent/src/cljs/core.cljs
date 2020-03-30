(ns <<project-ns>>.core
  (:require
    [reagent.core :as r]
    [reagent.dom :as rdom]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]<% if expanded %>
    [markdown.core :refer [md->html]]<% endif %>
    [<<project-ns>>.ajax :as ajax]
    [ajax.core :refer [GET POST]]
    [reitit.core :as reitit]
    [clojure.string :as string])
  (:import goog.History))

(defonce session (r/atom {:page :home}))

(defn nav-link [uri title page]
  [:a<% if expanded %>.navbar-item<% endif %>
   {:href   uri
    :class (when (= page (:page @session)) "is-active")}
   title])

(defn navbar [] <% if expanded %>
  (r/with-let [expanded? (r/atom false)]
    [:nav.navbar.is-info>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href "/" :style {:font-weight :bold}} "<<name>>"]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click #(swap! expanded? not)
        :class (when @expanded? :is-active)}
       [:span][:span][:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when @expanded? :is-active)}
      [:div.navbar-start
       [nav-link "#/" "Home" :home]
       [nav-link "#/about" "About" :about]]]])<% else %>
  [:nav
   [nav-link "#/" "Home" :home]] <% endif %>)
<% if expanded %>
(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])
<% endif %>

(defn home-page []<% if expanded %>
  [:section.section>div.container>div.content
   (when-let [docs (:docs @session)]
     [:div {:dangerouslySetInnerHTML {:__html (md->html docs)}}])]<% else %>
  [:section]<% endif %>)

(def pages
  {:home #'home-page<% if expanded %>
   :about #'about-page<% endif %>})

(defn page []
  [(pages (:page @session))])

;; -------------------------
;; Routes

(def router
  (reitit/router
    [["/" :home]<% if expanded %>
     ["/about" :about]<% endif %>]))

(defn match-route [uri]
  (->> (or (not-empty (string/replace uri #"^.*#" "")) "/")
       (reitit/match-by-path router)
       :data
       :name))
;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (swap! session assoc :page (match-route (.-token event)))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
<% if expanded %>(defn fetch-docs! []
  (GET "/docs" {:handler #(swap! session assoc :docs %)}))
<% endif %>
(defn<% if shadow-cljs %> ^:dev/after-load<% endif %> mount-components []
  (rdom/render [#'navbar] (.getElementById js/document "navbar"))
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)<% if expanded %>
  (fetch-docs!)<% endif %>
  (hook-browser-navigation!)
  (mount-components))
