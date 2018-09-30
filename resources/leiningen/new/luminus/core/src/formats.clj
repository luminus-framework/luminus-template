(ns <<project-ns>>.middleware.formats
  (:require [cognitect.transit :as transit]
            [muuntaja.core :as m])
  (:import [com.fasterxml.jackson.datatype.jdk8 Jdk8Module]
           [java.time LocalDateTime]))

(def instance
  (m/create
    (-> m/default-options
        (assoc-in
          [:formats "application/json" :opts :modules]
          [(Jdk8Module.)])
        (update-in
          [:formats "application/transit+json"]
          merge
          {:encoder-opts
           {:handlers
            {java.time.LocalDateTime
             (transit/write-handler
               (constantly "LocalDateTime")
               #(.format % java.time.format.DateTimeFormatter/ISO_LOCAL_DATE_TIME))
             java.time.ZonedDateTime
             (transit/write-handler
               (constantly "ZonedDateTime")
               #(.format % java.time.format.DateTimeFormatter/ISO_OFFSET_DATE_TIME))}}}))))