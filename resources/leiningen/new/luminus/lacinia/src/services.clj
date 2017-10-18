(ns <<project-ns>>.routes.services
  (:require [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema :refer [compile]]
            [com.walmartlabs.lacinia :as lacinia :refer [execute]]
            [clojure.data.json :as json]
            [clojure.edn :as edn] 
            [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]<% if auth %>
            [compojure.api.meta :refer [restructure-param]]
            [buddy.auth.accessrules :refer [restrict]]
            [buddy.auth :refer [authenticated?]]<% endif %>))

<% if auth %>
(defn access-error [_ _]
  (unauthorized {:error "unauthorized"}))

(defn wrap-restricted [handler rule]
  (restrict handler {:handler  rule
                     :on-error access-error}))

(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [wrap-restricted rule]))

(defmethod restructure-param :current-user
  [_ binding acc]
  (update-in acc [:letks] into [binding `(:identity ~'+compojure-api-request+)]))
<% endif %>

(defn get-hero [context args value]
  (let [data  [{:id 1000
               :name "Luke"
               :home_planet "Tatooine"
               :appears_in ["NEWHOPE" "EMPIRE" "JEDI"]}
              {:id 2000
               :name "Lando Calrissian"
               :home_planet "Socorro"
               :appears_in ["EMPIRE" "JEDI"]}]]
           (first data)))

(def compiled-schema
  (-> "resources/schema.edn"
      slurp
      edn/read-string
      (attach-resolvers {:get-hero get-hero 
                         :get-droid (constantly {})})
      schema/compile))

(defn format-params [query]
   (let [parsed (json/read-str query)] ;;-> placeholder - need to ensure query meets graphql syntax
     (str "query { hero(id: \"1000\") { name appears_in }}")))

(defn execute-request [query]
    (let [formatted (format-params query)
          vars nil
          context nil]
    (-> (lacinia/execute compiled-schema formatted vars context)
        (json/write-str))))

(defapi service-routes
 <% if auth %>
  (GET "/authenticated" []
       :auth-rules authenticated?
       :current-user user
       (ok {:user user}))<% endif %>
                        
  (POST "/api" [query]
      (ok (execute-request query))))