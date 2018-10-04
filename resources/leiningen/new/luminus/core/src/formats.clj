(ns <<project-ns>>.middleware.formats
  (:require [cognitect.transit :as transit]
            [luminus-transit.time :as time]
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
          [:formats "application/transit+json" :decoder-opts]
          (partial merge time/time-deserialization-handlers))
        (update-in
          [:formats "application/transit+json" :encoder-opts]
          (partial merge time/time-serialization-handlers)))))