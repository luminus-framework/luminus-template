(ns {{name}}.models.db
  (:require [clojure.java.jdbc :as sql]))

(def db 
  {:subprotocol "postgresql"
   :subname "//localhost/{{sanitized}}"
   :user "admin"
   :password "admin"})

(defn initialized? [] 
  (throw (new Exception "TODO: initialize the database schema!")))

(defn create-users-table []
  (sql/with-connection db
    (sql/create-table
      :users
      [:id "varchar(20) PRIMARY KEY"]
      [:first_name "varchar(30)"]
      [:last_name "varchar(30)"]
      [:email "varchar(30)"]
      [:admin :boolean]
      [:last_login :time]
      [:is_active :boolean]
      [:pass "varchar(100)"])))

(defn create-tables
  "creates the database tables used by the application"
  []
  (create-users-table))

(defn create-user
  "creates a user row with id and pass columns"
  [{:keys [id] :as user}]
  (sql/with-connection db
    (sql/insert-record :users user)))

(defn get-user [id]
  (sql/with-connection db
    (sql/with-query-results 
      res ["select * from users where id = ?" id] (first res))))