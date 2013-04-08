(ns main
  (:require [ajax :refer [GET]] 
            [domina :as dom]
            [dommy.template :as template]))

(defn render-message [{:keys [message user]}]
  [:li [:p {:id user} message " - " user]])
  
(defn render-messages [messages]
  (.log js/console messages)
  (->> messages
      (map render-message)         
      (into [:ul])       
      template/node     
      (dom/append! (dom/by-id "messages"))))

(defn ^:export init []  
  (GET "/messages" render-messages))
