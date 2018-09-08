(ns <<project-ns>>.middleware.formats
  (:require [cognitect.transit :as transit]
            [muuntaja.core :as m])
  (:import
    [com.fasterxml.jackson.datatype.joda JodaModule]
    [org.joda.time ReadableInstant]))

(def joda-time-writer
  (transit/write-handler
    (constantly "m")
    (fn [v] (-> ^ReadableInstant v .getMillis))
    (fn [v] (-> ^ReadableInstant v .getMillis .toString))))

(def instance
  (m/create
    (-> m/default-options
        (assoc-in
          [:formats "application/json" :opts :modules]
          [(JodaModule.)])
        (assoc-in
          [:formats "application/transit+json" :encode-opts]
          {:handlers {org.joda.time.DateTime joda-time-writer}}))))