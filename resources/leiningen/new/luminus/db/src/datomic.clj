(ns <<project-ns>>.db.core
  (:require [datomic.api :as d]
            [io.rkn.conformity :as c]
            [mount.core :refer [defstate]]
            [<<project-ns>>.config :refer [env]]))

(defstate conn
  :start (do (-> env :database-url d/create-database) (-> env :database-url d/connect))
  :stop (-> conn .release))

(def norms-map (c/read-resource "migrations/schema.edn"))

(defn install-schema
  "This function expected to be called at system start up.

  Datomic schema migraitons or db preinstalled data can be put into 'migrations/schema.edn'
  Every txes will be executed exactly once no matter how many times system restart."
  [conn]
  (c/ensure-conforms conn norms-map (keys norms-map)))

(defn show-schema
  "Show currenly installed schema"
  [conn]
  (let [system-ns #{"db" "db.type" "db.install" "db.part"
                    "db.lang" "fressian" "db.unique" "db.excise"
                    "db.cardinality" "db.fn" "db.sys" "db.bootstrap"
                    "db.alter"}]
    (d/q '[:find ?ident
           :in $ ?system-ns
           :where
           [?e :db/ident ?ident]
           [(namespace ?ident) ?ns]
           [((comp not contains?) ?system-ns ?ns)]]
         (d/db conn) system-ns)))

(defn show-transaction
  "Show all the transaction data
   e.g.
    (-> conn show-transaction count)
    => the number of transaction"
  [conn]
  (seq (d/tx-range (d/log conn) nil nil)))

(defn add-user
  "e.g.
    (add-user conn {:id \"aaa\"
                    :screen-name \"AAA\"
                    :status :user.status/active
                    :email \"aaa@example.com\" })"
  [conn {:keys [id screen-name status email]}]
  @(d/transact conn [{:user/id         id
                      :user/name       screen-name
                      :user/status     status
                      :user/email      email}]))

(defn find-one-by
  "Given db value and an (attr/val), return the user as EntityMap (datomic.query.EntityMap)
   If there is no result, return nil.

   e.g.
    (d/touch (find-one-by (d/db conn) :user/email \"user@example.com\"))
    => show all fields
    (:user/first-name (find-one-by (d/db conn) :user/email \"user@example.com\"))
    => show first-name field"
  [db attr val]
  (d/entity db
            ;;find Specifications using ':find ?a .' will return single scalar
            (d/q '[:find ?e .
                   :in $ ?attr ?val
                   :where [?e ?attr ?val]]
                 db attr val)))


(defn find-user [db id]
  (d/touch (find-one-by db :user/id id)))
