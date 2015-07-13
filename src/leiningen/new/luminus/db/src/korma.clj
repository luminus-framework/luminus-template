(ns <<project-ns>>.db.core
  (:use [korma.core])
  (:require
    [korma.db :as db]
    [environ.core :refer [env]]))

(db/defdb db (env :database-url))

(defentity users)

(defn create-user [user]
  (insert users (values user)))

(defn update-user [id first-name last-name email]
  (update users
          (set-fields {:first_name first-name
                       :last_name  last-name
                       :email      email})
          (where {:id id})))

(defn get-user [id]
  (select users (where {:id id})))
