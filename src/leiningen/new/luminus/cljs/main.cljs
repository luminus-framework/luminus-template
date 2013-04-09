(ns {{name}}.main
  (:require [ajax.core :refer [GET POST]] 
            [domina :refer [value by-id destroy-children! append!]]
            [domina.events :refer [listen!]]
            [dommy.template :as template]))

(defn render-message [{:keys [message user]}]
  [:li [:p {:id user} message " - " user]])

(defn render-messages [messages]
  (let [messages-div (by-id "messages")]
    (destroy-children! messages-div)
    (->> messages
         (map render-message)         
         (into [:ul])       
         template/node     
         (append! messages-div))))

(defn add-message [_]
  (POST "/add-message" 
        {:params {:message (value (by-id "message"))
                  :user    (value (by-id "user"))}
         :handler render-messages}))

(defn ^:export init []
  (GET "/messages" {:handler render-messages})
  (listen! (by-id "send") :click add-message))