(ns <<project-ns>>.ajax
  (:require [ajax.core :as ajax]))

(defn default-headers [request]
  (-> request
      (update :uri #(str js/context %))
      (update
        :headers
        #(merge
          %
          {"Accept" "application/transit+json"
           "x-csrf-token" js/csrfToken}))))

(defn load-interceptors! []
  (swap! ajax/default-interceptors
         conj
         (ajax/to-interceptor {:name "default headers"
                               :request default-headers})))


