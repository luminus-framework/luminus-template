(ns <<project-ns>>.core
  (:require [<<project-ns>>.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET]]
            [cljsjs.jquery]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [hoplon.core
             :as h
             :include-macros true]
            [hoplon.jquery]
            [javelin.core
             :refer [cell]
             :refer-macros [cell= dosync]]
            [markdown.core :refer [md->html]]
            [secretary.core :as secretary])
  (:import goog.History))

(defonce selected-page (cell :home))

(defonce docs (cell nil))

(defn nav-link [uri title page expanded?]
  (h/li :class (cell= {:active (= page selected-page)
                       :nav-item true})
    (h/a :class "nav-link"
         :href uri
         :click #(do
                   (reset! expanded? false)
                   (secretary/dispatch! uri))
         title)))

(defn navbar []
  (let [expanded? (cell false)]
    (h/nav :class "navbar navbar-dark bg-primary"
      (h/button :class "navbar-toggler hidden-sm-up"
                :click #(swap! expanded? not)
                "☰")
      (h/div :class (cell= {:collapse true
                            :navbar-toggleable-xs true
                            :in expanded?})
       (h/a :class "navbar-brand" :href "/" "<<name>>")
       (h/ul {:class "nav navbar-nav"}
         (nav-link "#/" "Home" :home expanded?)
         (nav-link "#/about" "About" :about expanded?))))))

(defn about []
  (h/div :class "container"
    (h/div :class "row"
      (h/div :class "col-md-12"
             "this is the story of <<name>>... work in progress"))))

(defn home []
  (h/div :class "container"
    (h/div :html (cell= (md->html docs)))))

(h/defelem page []
  (h/div :id "app"
    (navbar)
    (cell=
     (case selected-page
       :home (home)
       :about (about)))))

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
 (reset! selected-page :home))

(secretary/defroute "/about" []
 (reset! selected-page :about))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
 (doto (History.)
   (events/listen
     HistoryEventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
   (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(reset! docs %)}))

(defn mount-components []
  (js/jQuery #(.replaceWith (js/jQuery "#app") (page))))

(defn init! []
  (load-interceptors!)
  (hook-browser-navigation!)
  (mount-components)
  (fetch-docs!))
