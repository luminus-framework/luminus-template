(ns <<project-ns>>.session
  (:require [ring-ttl-session.core :refer [ttl-memory-store]]))

(defonce ttl-mem 
  "Session storage whose keys expire in 30 minutes."
  (ttl-memory-store (* 60 30)))
