(ns leiningen.new.db
  (:require [leiningen.new.common :refer :all]))

(defn select-db [{:keys [features]}]
  (cond
    (some #{"+postgres"} features) :postgres
    (some #{"+mysql"} features) :mysql
    (some #{"+mongodb"} features) :mongo
    (some #{"+datomic"} features) :datomic
    (some #{"+h2"} features) :h2
    (some #{"+sqlite"} features) :sqlite))

(defn db-dependencies [options]
  [['luminus-migrations "0.3.9"]
   ['conman "0.6.4"]
   ({:postgres ['org.postgresql/postgresql "42.0.0"]
     :mysql    ['mysql/mysql-connector-java "6.0.5"]
     :h2       ['com.h2database/h2 "1.4.193"]
     :sqlite   ['org.xerial/sqlite-jdbc "3.16.1"]}
     (select-db options))])

(defn db-url [{:keys [sanitized] :as options} suffix]
  ({:postgres (str "postgresql://localhost/" sanitized "_" suffix
                   "?user=db_user_name_here&password=db_user_password_here")
    :mysql    (str "mysql://localhost:3306/" sanitized "_" suffix
                   "?user=db_user_name_here&password=db_user_password_here")
    :h2       (str "jdbc:h2:./" sanitized "_" suffix ".db")
    :sqlite   (str "jdbc:sqlite:" sanitized "_" suffix ".db")
    :mongo    (str "mongodb://127.0.0.1/" sanitized "_" suffix)
    :datomic  (str "datomic:free://localhost:4334/" sanitized "_" suffix)}
    (select-db options)))

(defn relational-db-files [options]
  (let [timestamp (.format
                    (java.text.SimpleDateFormat. "yyyyMMddHHmmss")
                    (java.util.Date.))]
    [["src/clj/{{sanitized}}/db/core.clj" "db/src/sql.db.clj"]
     ["resources/sql/queries.sql" "db/sql/queries.sql"]
     ["test/clj/{{sanitized}}/test/db/core.clj" "db/test/db/core.clj"]
     [(str "resources/migrations/" timestamp "-add-users-table.up.sql") "db/migrations/add-users-table.up.sql"]
     [(str "resources/migrations/" timestamp "-add-users-table.down.sql") "db/migrations/add-users-table.down.sql"]]))

(defn db-profiles [options]
  {:database-profile-dev  (str :database-url " \"" (db-url options "dev") "\"")
   :database-profile-test (str :database-url " \"" (db-url options "test") "\"")})

(def mongo-files
  [["src/clj/{{sanitized}}/db/core.clj" "db/src/mongodb.clj"]])

(def datomic-files
  [["src/clj/{{sanitized}}/db/core.clj" "db/src/datomic.clj"]])

(defn add-mongo [[assets options]]
  [(into assets mongo-files)
   (-> options
       (append-options :dependencies [['com.novemberain/monger "3.1.0" :exclusions ['com.google.guava/guava]]
                                      ['com.google.guava/guava "20.0"]])
       (assoc
         :db-connection true
         :db-docs ((:selmer-renderer options) (slurp-resource "db/docs/mongo_instructions.md") options))
       (merge (db-profiles options)))])

(defn add-datomic [[assets options]]
  [(into assets datomic-files)
   (-> options
       (append-options :dependencies [['com.datomic/datomic-free "0.9.5561"
                                       :exclusions ['org.slf4j/log4j-over-slf4j
                                                    'org.slf4j/slf4j-nop]]
                                      ['com.google.guava/guava "21.0"]])
       (assoc
         :db-connection true
         :db-docs ((:selmer-renderer options) (slurp-resource "db/docs/datomic_instructions.md") options))
       (merge (db-profiles options)))])

(defn add-relational-db [db [assets options]]
  [(into assets (relational-db-files options))
   (let [embedded-db? (some #{(name db)} ["h2" "sqlite"])]
     (-> options
         (append-options :dependencies (db-dependencies options))
         (append-options :plugins [['migratus-lein "0.4.9"]])
         (assoc
           :relational-db true
           :db-connection (not embedded-db?)
           :db-type (name db)
           :embedded-db embedded-db?
           :migrations "{:store :database :db ~(get (System/getenv) \"DATABASE_URL\")}"
           :db-docs ((:selmer-renderer options)
                      (slurp-resource (if (= :h2 db)
                                        "db/docs/h2_instructions.md"
                                        "db/docs/db_instructions.md"))
                      options))
         (merge (db-profiles options))))])

(defn db-features [state]
  (if-let [db (select-db (second state))]
    (cond
      (= :mongo db)   (add-mongo state)
      (= :datomic db) (add-datomic state)
      :else           (add-relational-db db state))
    state))
