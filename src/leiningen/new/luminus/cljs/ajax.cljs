(ns ajax.core  
  (:require [goog.net.XhrIo :as xhr]
            [goog.Uri :as uri]
            [goog.Uri.QueryData :as query-data]
            [cljs.reader :as reader]
            [goog.structs :as structs]))

(defn clj->js
  "Recursively transforms ClojureScript maps into Javascript objects,
   other ClojureScript colls into JavaScript arrays, and ClojureScript
   keywords into JavaScript strings."
  [x]
  (cond
    (string? x) x
    (keyword? x) (name x)
    (map? x) (.-strobj (reduce (fn [m [k v]]
               (assoc m (clj->js k) (clj->js v))) {} x))
    (coll? x) (apply array (map clj->js x))
    :else x))
    
(defn default-handler [format handler] 
  (fn [response]
    (if handler 
      (let [response-text (.getResponseText (.-target response))            
            result (condp = (or format :edn)
                     :json (js->clj response-text)
                     :edn (reader/read-string response-text)
                     (throw (js/Error. (str "unrecognized format: " format))))]
        (handler result)))))

(defn params-to-str [params]
  (if params
    (-> params
        clj->js
        structs/Map.
        query-data/createFromMap
        .toString)))

(defn ajax-request [rel-url method format handler params]
  (xhr/send (str js/context rel-url)
            (default-handler format handler) 
            method 
            (params-to-str params)))

(defn GET [rel-url {:keys [format handler params]}]
  (ajax-request rel-url "GET" format handler params))

(defn POST [rel-url {:keys [format handler params]}]
  (ajax-request rel-url "POST" format handler params))