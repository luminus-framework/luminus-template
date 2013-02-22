(ns {{name}}.models.db
  (:require [clojure.java.jdbc :as sql]
            [noir.io :as io]))

(def db-store "site.db")

(defn db []
  {:classname   "org.h2.Driver",
   :subprotocol "h2",
   :subname (str (io/resource-path) db-store)
   :user "sa"
   :password ""})

(def db-memo (memoize db))

(defn initialized?
  "checks to see if the database schema is present"
  []
  (.exists (new java.io.File (str (io/resource-path) db-store ".h2.db"))))

(defn create-users-table
  "creates the users table, the user has following fields
   id - "
  []
  (sql/with-connection (db-memo)
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
  (sql/with-connection (db-memo)
    (sql/insert-record :users user)))

(defn get-user [id]
  (sql/with-connection (db-memo)
    (sql/with-query-results
      res ["select * from users where id = ?" id] (first res))))
