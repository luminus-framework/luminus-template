(ns <<project-ns>>.db.core-test
  (:require
    [<<project-ns>>.db.core :as db]
    [xtdb.api :as xt]
    [clojure.test :refer :all]
    [mount.core :as mount]))

(defn- with-components
  [components f]
  (apply mount/start components)
  (f)
  (apply mount/stop components))

(use-fixtures
  :each
  #(with-components [#'<<project-ns>>.config/env
                     #'<<project-ns>>.db.core/node] %))

;; Test data
(def user-1 {:user/email "aaa@example.com"})
(def user-2 {:user/email "bbb@example.com"})

(deftest create-user
  (let [{:keys [user/id]} (db/create-user! db/node user-1)]
    (is (uuid? id))
    (is (= (assoc user-1 :user/id id
                         :xt/id id
                         :<<project-ns>>/type :user)
           (xt/entity (xt/db db/node) id)))))

(deftest update-user
  (let [{:keys [user/id]} (db/create-user! db/node user-1)]
    (db/update-user! db/node (assoc user-1 :user/id id
                                           :user/email "updated@test.com"))
    (is (= "updated@test.com"
           (-> (xt/entity (xt/db db/node) id)
               :user/email)))))

(deftest find-user-by-id
  (let [{:keys [user/id]} (db/create-user! db/node user-1)]
    (is (= user-1
           (-> (db/find-user-by-id db/node id)
               (dissoc :user/id))))))

(deftest find-user-by-attr
  (db/create-user! db/node user-1)
  (db/create-user! db/node user-2)
  (is (= user-1
         (-> (db/find-user-by-attribute db/node :user/email (:user/email user-1))
             (dissoc :user/id)))))
