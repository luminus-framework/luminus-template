(ns {{name}}.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [clojure.java.jdbc :as sql]))

(def db-spec
  {:subprotocol "postgresql"
   :subname "//localhost/{{sanitized}}"
   :user "admin"
   :password "admin"})

(def db db-spec)

(defn initialized? []
  (throw (new Exception "TODO: initialize the database schema!")))

(defn create-users-table []
  (sql/with-connection db-spec
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

(defentity users)

(defn create-user
  "creates a user row with id and pass columns"
  [user]
  (insert users
          (values user)))

(defn get-user [id]
  (first (select users
                 (where {:id id})
                 (limit 1))))
