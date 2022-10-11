(ns <<project-ns>>.routes.home
  (:require
   [<<project-ns>>.layout :as layout]<% if relational-db %>
   [<<project-ns>>.db.core :as db]<% endif %>
   [clojure.java.io :as io]
   [<<project-ns>>.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]<% if async %>
   [promesa.core :as p]<% endif %>))
<% if async %>
(defn expensive [_]
  (p/delay 5000 {:status 200
                  :headers {"content-type" "text/plain"}
                  :body "Imagine this was a long running request.

But is uses promesa/CompletableFuture under the covers, so it does not tie
up a thread - it is async."}))
<% endif %>

<% if cljs  %>
(defn home-page [request]
  (layout/render request "home.html"))

(defn home-routes []
  [<% if war %>"/<<name>>"<% else %>""<% endif %>
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats<% if async %>
                 ;; wrap-as-async must be last
                 middleware/wrap-as-async<% endif %>]}
   ["/" {:get home-page}]<% if graphql %>
   ["/graphiql" {:get (fn [request] (layout/render request "graphiql.html"))}]<% endif %><% if async %>
   ["/expensive" {:get expensive}]<% endif %><% if expanded %>
   ["/docs" {:get (fn [_]
                    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                        (response/header "Content-Type" "text/plain; charset=utf-8")))}]<% endif %>])
<% else %>
(defn home-page [request]
  (layout/render request "home.html"<% if expanded %> {:docs (-> "docs/docs.md" io/resource slurp)}<% endif %>))
<% if expanded %>
(defn about-page [request]
  (layout/render request "about.html"))
<% endif %>
(defn home-routes []
  [<% if war %> "/<<name>>" <% else %> "" <% endif %>
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats
                 <% if async %>;; wrap-as-async must be last
                 middleware/wrap-as-async<% endif %>]}
   ["/" {:get home-page}]<% if async %>
   ["/expensive" {:get expensive}]<% endif %><% if graphql %>
   ["/graphiql" {:get (fn [request]
                        (layout/render request "graphiql.html"))}]<% endif %><% if expanded %>
   ["/about" {:get about-page}]<% endif %>])
<% endif %>
