(ns <<project-ns>>.core
  (:require [ajax.core :refer [GET POST]]
            [kee-frame.core :as kf]
            [markdown.core :refer [md->html]]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [<<project-ns>>.ajax :refer [load-interceptors!]]
            [<<project-ns>>.effects :as effects]
            [<<project-ns>>.routing :as routing])
  (:import goog.History))

(defn nav-link [uri title page]
  [:li.nav-item
   {:class (when (= page @(rf/subscribe [:nav/page])) "active")}
   [:a.nav-link {:href uri} title]])

(defn navbar []
  [:nav.navbar.navbar-dark.bg-primary.navbar-expand-md
   {:role "navigation"}
   [:button.navbar-toggler.hidden-sm-up
    {:type "button"
     :data-toggle "collapse"
     :data-target "#collapsing-navbar"}
    [:span.navbar-toggler-icon]]
   [:a.navbar-brand {:href "#/"} "<<name>>"]
   [:div#collapsing-navbar.collapse.navbar-collapse
    [:ul.nav.navbar-nav.mr-auto
     [nav-link (kf/path-for [:home]) "Home" :home]
     [nav-link (kf/path-for [:about]) "About" :about]]]])

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src <% if servlet %>(str js/context "/img/warning_clojure.png")<% else %>"/img/warning_clojure.png"<% endif %>}]]]])

(rf/reg-event-fx
  ::load-about-page
  (constantly nil))

(kf/reg-controller
  ::about-controller
  {:params (constantly true)
   :start  [::load-about-page]})

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(defn home-page []
  [:div.container
   [:div.row>div.col-sm-12
    [:h2.alert.alert-info "Tip: try pressing CTRL+H to open re-frame tracing menu"]]
   (when-let [docs @(rf/subscribe [:docs])]
     [:div.row>div.col-sm-12
      [:div {:dangerouslySetInnerHTML
             {:__html (md->html docs)}}]])])

(kf/reg-chain
  ::load-home-page
  (fn [_ _]
    {:http {:method      :get
            :url         "/docs"
            :error-event [:common/set-error]}})
  (fn [{:keys [db]} [_ docs]]
    {:db (assoc db :docs docs)}))


(kf/reg-controller
  ::home-controller
  {:params (constantly true)
   :start  [::load-home-page]})

(defn root-component []
  [:div
   [navbar]
   [kf/switch-route (fn [route] (get-in route [:data :name]))
    :home home-page
    :about about-page
    nil [:div ""]]])

;; -------------------------
;; Initialize app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'root-component] (.getElementById js/document "app")))

(defn init! []
  (kf/start! {:debug?         true
              :router         (routing/->ReititRouter routing/router)
              :chain-links    effects/chain-links
              :initial-db     {}
              :root-component [root-component]})
  (load-interceptors!)
  (routing/hook-browser-navigation!))
