(ns leiningen.new.db
  (:require [leiningen.new.common :refer :all]))

(defn select-db [{:keys [features]}]
  (cond
    (some #{"+postgres"} features) :postgres
    (some #{"+mysql"} features) :mysql
    (some #{"+mongodb"} features) :mongo
    (some #{"+h2"} features) :h2))

(defn db-dependencies [options]
  [['ragtime "0.3.8"]
   ['yesql "0.5.0-rc1"]
   ({:postgres ['org.postgresql/postgresql "9.3-1102-jdbc41"]
     :mysql    ['mysql/mysql-connector-java "5.1.6"]
     :h2       ['com.h2database/h2 "1.4.182"]}
     (select-db options))])

(defn migrations [{:keys [sanitized] :as options}]
  {:migrations 'ragtime.sql.files/migrations
   :database
               ({:postgres
                 (str "jdbc:postgresql://localhost/" sanitized
                      "?user=db_user_name_here&password=db_user_password_here")
                 :mysql
                 (str "jdbc:mysql://localhost:3306/" sanitized
                      "?user=db_user_name_here&password=db_user_password_here")
                 :h2
                 (str "jdbc:h2:./site.db")}
                 (select-db options))})

(defn relational-db-files [options]
  (let [timestamp (.format
                    (java.text.SimpleDateFormat. "yyyyMMHHmmss")
                    (java.util.Date.))]
    [["src/<<sanitized>>/db/core.clj"
      (str "db/src/" ({:postgres "postgres.db.clj"
                       :mysql    "mysql.db.clj"
                       :h2       "h2.db.clj"}
                       (select-db options)))]
     ["resources/sql/functions.sql" "db/sql/functions.sql"]
     [(str "migrations/" timestamp "-add-users-table.up.sql") "db/migrations/add-users-table.up.sql"]
     [(str "migrations/" timestamp "-add-users-table.down.sql") "db/migrations/add-users-table.down.sql"]]))

(def mongo-files
  [["src/{{sanitized}}/db/core.clj" "db/src/mongodb.clj"]])

(defn add-mongo [[assets options]]
  [(into assets mongo-files)
   (assoc options
     :db-docs (slurp-resource "db/docs/mongo_instructions.html")
     :db-dependencies (indent dependency-indent [['com.novemberain/monger "2.0.1"]]))])

(defn add-relational-db [[assets options]]
  [(into assets (relational-db-files options))
   (assoc options
     :db-docs (slurp-resource "db/docs/db_instructions.html")
     :db-dependencies (indent dependency-indent (db-dependencies options))
     :db-plugins (indent plugin-indent [['ragtime/ragtime.lein "0.3.8"]])
     :migrations (indent root-indent (migrations options)))])

(defn db-features [state]
  (if-let [db (select-db (second state))]
    (if (= :mongo db)
      (add-mongo state)
      (add-relational-db state))
    state))