(ns {{name}}.models.db
  (:require [clojure.java.jdbc :as sql]
            [noir.io :as io])
  (:import java.sql.DriverManager 
           java.io.File))

(defn db [] 
  {:classname   "org.sqlite.JDBC",
   :subprotocol "sqlite",
   :subname     (str (io/resource-path) "db.sq3")})

(def db-memo (memoize db))

(defn initialized?
  "checks to see if the database schema is present"
  []
  (.exists (new File (str (io/resource-path) "db.sq3"))))

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