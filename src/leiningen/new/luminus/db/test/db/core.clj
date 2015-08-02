(ns <<project-ns>>.test.db.core
  (:require [<<project-ns>>.db.core :as db]
            [<<project-ns>>.db.migrations :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [environ.core :refer [env]]<% ifunequal db-type "h2" %>
            [luminus-db.core :refer [with-transaction]]<% endifunequal %>))

(use-fixtures
  :once
  (fn [f]
    (db/connect!)
    (migrations/migrate ["migrate"])
    (f)))

(deftest test-users
  (<% ifequal db-type "h2" %>jdbc/with-db-transaction [trans-conn db/conn]<% else %>with-transaction trans-conn<% endifequal %>
    (jdbc/db-set-rollback-only! trans-conn)
    (is (= 1 (db/create-user!
               {:id         "1"
                :first_name "Sam"
                :last_name  "Smith"
                :email      "sam.smith@example.com"
                :pass       "pass"})))
    (is (= [{:id         "1"
             :first_name "Sam"
             :last_name  "Smith"
             :email      "sam.smith@example.com"
             :pass       "pass"
             :admin      nil
             :last_login nil
             :is_active  nil}]
           (db/get-user {:id "1"})))))
