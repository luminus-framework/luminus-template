(ns <<project-ns>>.test.db.core
  (:require [clojure.test :refer :all]
            [<<project-ns>>.db.core :refer :all]
            [<<project-ns>>.db.migrations :as migrations]))

(use-fixtures
  :once
  (fn [f]
    (connect!)
    (migrations/migrate ["migrate"])
    (f)))

(deftest test-users
  ;; Make sure the user with id 1 doesn't exist.
  ;; You can also use transactions around tests to ensure that.
  (delete-user! {:id "1"} {:connection @db-spec})
  (is (= 1 (create-user! {:id         "1"
                          :first_name "Sam"
                          :last_name  "Smith"
                          :email      "sam.smith@example.com"
                          :pass       "pass"}
                         {:connection @db-spec})))
  (is (= [{:id         "1"
           :first_name "Sam"
           :last_name  "Smith"
           :email      "sam.smith@example.com"
           :pass       "pass"
           :admin      nil
           :last_login nil
           :is_active  nil}]
         (get-user {:id "1"} {:connection @db-spec}))))
