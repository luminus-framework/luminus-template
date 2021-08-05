(ns <<project-ns>>.routes.home
  (:require
    [ctmx.core :as ctmx]
    [ctmx.render :as render]
    [hiccup.page :refer [html5]]))

(defn html-response [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(defn html5-response
  ([body]
   (html-response
    (html5
     [:head
      [:meta {:name "viewport"
              :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]]
     [:body (render/walk-attrs body)]
     [:script {:src "https://unpkg.com/htmx.org@1.5.0"}]))))

(ctmx/defcomponent ^:endpoint root [req ^:int num-clicks]
  [:div.m-3 {:hx-post "root"
             :hx-swap "outerHTML"
             :hx-vals {:num-clicks (inc num-clicks)}}
   "You have clicked me " num-clicks " times."])

(defn home-routes []
  (ctmx/make-routes
   "/"
   (fn [req]
     (html5-response
      (root req 0)))))
