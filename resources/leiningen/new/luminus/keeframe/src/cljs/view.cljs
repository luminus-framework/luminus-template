(ns <<project-ns>>.view
  (:require
    [kee-frame.core :as kf]<%if expanded %>
    [markdown.core :refer [md->html]]<% endif %>
    [reagent.core :as r]
    [re-frame.core :as rf]))

(defn nav-link [title page]
  [:a<% if expanded %>.navbar-item<% endif %>
   {:href   (kf/path-for [page])
    :class (when (= page @(rf/subscribe [:nav/page])) "is-active")}
   title])

(defn navbar []<% if expanded %>
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
       [nav-link "Home" :home]
       [nav-link "About" :about]]]])<% else %>
  [:nav
   [nav-link "Home" :home]]<% endif %>)
<% if expanded %>
(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src <% if servlet %>(str js/context "/img/warning_clojure.png")<% else %>"/img/warning_clojure.png"<% endif %>}]])
<% endif %>
(defn home-page []<% if expanded %>
  [:section.section>div.container>div.content
   (when-let [docs @(rf/subscribe [:docs])]
     [:div {:dangerouslySetInnerHTML {:__html (md->html docs)}}])]<% else %>
  [:section]<% endif %>)

(defn root-component []
  [:div
   [navbar]
   [kf/switch-route (fn [route] (get-in route [:data :name]))
    :home home-page <% if expanded %>
    :about about-page <% endif %>
    nil [:div ""]]])
