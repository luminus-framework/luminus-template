(ns ajax
  (:require [goog.net.XhrIo :as xhr]
            [goog.Uri :as uri]
            [cljs.reader :as reader]))

(defn default-handler [handler] 
  (fn [response]
    (if handler 
      (-> response
          .-target
          .getResponseText
          reader/read-string
          handler))))

(defn params-to-str [params]
  (let [query-data (uri/QueryData.)] 
    (doseq [[k v] params] 
      (cond 
        (empty? v) nil
        (coll? v)
        (.setValues query-data (str k "[]") (apply array v))
        :else
        (.setValues query-data k v)))
    (.toString query-data)))
                        
(defn ajax-request [rel-url method handler params]
  (xhr/send ;prepend the servlet context to the URL
            (str js/context rel-url) 
            (default-handler handler) 
            method 
            (params-to-str params)))
            
(defn GET
  ([rel-url] (GET rel-url nil))
  ([rel-url handler & params]
    (ajax-request rel-url "GET" handler params)))

(defn POST
  ([rel-url] (POST rel-url nil))
  ([rel-url handler & params]
    (ajax-request rel-url "POST" handler params)))
