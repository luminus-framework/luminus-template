(ns {{name}}.db.core
  (:require
    [yesql.core :refer [defqueries]]))

(defqueries "sql/functions.sql")

(def db
  {:subprotocol "mysql"
   :subname "//localhost:3306/{{sanitized}}"
   :user "db_user_name_here"
   :password "db_user_password_here"})