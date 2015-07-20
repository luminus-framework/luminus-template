(ns <<project-ns>>.test.db.core
  (:require [clojure.test :refer :all]
            [<<project-ns>>.db.core :as db]
            [clojure.java.jdbc :as jdbc]
            [<<project-ns>>.db.migrations :as migrations]))

(use-fixtures
  :once
  (fn [f]
    (db/connect!)
    (migrations/migrate ["migrate"])
    (f)))

(deftest test-users
  ;; Make sure the user with id 1 doesn't exist.
  ;; You can also use transactions around tests to ensure that.
  (jdbc/with-db-transaction [trans-conn (:connection @db/conn)]
    (jdbc/db-set-rollback-only! trans-conn)
    (is (= 1 (db/execute
               db/create-user!
               {:id         "1"
                :first_name "Sam"
                :last_name  "Smith"
                :email      "sam.smith@example.com"
                :pass       "pass"}
               {:connection trans-conn})))
    (is (= [{:id         "1"
             :first_name "Sam"
             :last_name  "Smith"
             :email      "sam.smith@example.com"
             :pass       "pass"
             :admin      nil
             :last_login nil
             :is_active  nil}]
           (db/execute db/get-user {:id "1"} {:connection trans-conn})))))
