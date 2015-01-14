(ns <<name>>.db.core
  (:require
    [yesql.core :refer [defqueries]]))

(defqueries "sql/functions.sql")

(def db
  {:subprotocol "postgresql"
   :subname "//localhost/{{sanitized}}"
   :user "db_user_name_here"
   :password "db_user_password_here"})