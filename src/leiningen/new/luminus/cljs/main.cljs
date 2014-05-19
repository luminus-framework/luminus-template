(ns {{name}}.main
  (:require [ajax.core :refer [GET POST]] 
            [domina :refer [value by-id destroy-children! append!]]
            [domina.events :refer [listen!]]
            [dommy.template :as template]))

(defn render-message [message]
  (let [user (get message "user")
        text (get message "message")]
    [:li [:p {:id user} text " - " user]]))

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
        (merge base-request-params
               {:params {:message (value (by-id "message"))
                         :user    (value (by-id "user"))}                                
                :handler render-messages})))

(defn ^:export init []
  (GET "/messages"
       {:handler render-messages})
  (listen! (by-id "send") :click add-message))

