(ns <<project-ns>>.test.db.core
  (:require [<<project-ns>>.db.core :as db]
            [<<project-ns>>.db.migrations :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]<% ifunequal db-type "h2" %>
            [conman.core :refer [with-transaction]]<% endifunequal %>
            [environ.core :refer [env]]))

(use-fixtures
  :once
  (fn [f]<% ifunequal db-type "h2" %>
    (db/connect!)<% endifunequal %>
    (migrations/migrate ["migrate"])
    (f)))

(deftest test-users
  (<% ifequal db-type "h2" %>jdbc/with-db-transaction<% else %>with-transaction<% endifequal %> [t-conn db/conn]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= 1 (db/create-user!
               {:id         "1"
                :first_name "Sam"
                :last_name  "Smith"
                :email      "sam.smith@example.com"
                :pass       "pass"}<% ifequal db-type "h2" %> {:connection t-conn}<% endifequal %>)))
    (is (= [{:id         "1"
             :first_name "Sam"
             :last_name  "Smith"
             :email      "sam.smith@example.com"
             :pass       "pass"
             :admin      nil
             :last_login nil
             :is_active  nil}]
           (db/get-user {:id "1"}<% ifequal db-type "h2" %> {:connection t-conn}<% endifequal %>)))))
