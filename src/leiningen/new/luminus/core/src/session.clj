(ns <<project-ns>>.session
  (:require [ring-ttl-session.core :refer [ttl-memory-store]]))

(defonce ttl-mem (ttl-memory-store (* 60 30)))
