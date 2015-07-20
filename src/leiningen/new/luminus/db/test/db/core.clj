(ns <<project-ns>>.test.db.core
  (:require [clojure.test :refer :all]
            [<<project-ns>>.db.core :as db]
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
  (db/execute db/delete-user! {:id "1"})
  (is (= 1 (db/execute
             db/create-user!
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
         (db/execute db/get-user {:id "1"}))))
