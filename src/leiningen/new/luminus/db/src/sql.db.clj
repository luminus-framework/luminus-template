(ns <<project-ns>>.db.core<% ifequal db-type "h2" %>
  (:require
    [clojure.java.jdbc :as jdbc]
    [yesql.core :refer [defqueries]]
    [taoensso.timbre :as timbre]
    [environ.core :refer [env]])
  (:import java.sql.BatchUpdateException)<% endifequal %><% ifequal db-type "postgres" %>
  (:require
    [cheshire.core :refer [generate-string parse-string]]
    [taoensso.timbre :as timbre]
    [clojure.java.jdbc :as jdbc]
    [yesql.core :as yesql]
    [clj-dbcp.core :as dbcp]
    [to-jdbc-uri.core :refer [to-jdbc-uri]]
    [environ.core :refer [env]])
  (:import org.postgresql.util.PGobject
           org.postgresql.jdbc4.Jdbc4Array
           clojure.lang.IPersistentMap
           clojure.lang.IPersistentVector
           [java.sql BatchUpdateException
            Date
            Timestamp
            PreparedStatement])<% endifequal %><% ifequal db-type "mysql" %>
  (:require
    [taoensso.timbre :as timbre]
    [clojure.java.jdbc :as jdbc]
    [yesql.core :as yesql]
    [clj-dbcp.core :as dbcp]
    [to-jdbc-uri.core :refer [to-jdbc-uri]]
    [environ.core :refer [env]])
  (:import [java.sql BatchUpdateException
            PreparedStatement])<% endifequal %>)<% ifequal db-type "h2"%>

(def conn
  {:classname   "org.h2.Driver"
   :connection-uri (:database-url env)
   :make-pool?     true
   :naming         {:keys   clojure.string/lower-case
                    :fields clojure.string/upper-case}})

(defqueries "sql/queries.sql" {:connection conn})

(defn connect! [])

(defn disconnect! [])<% else %>

(defonce ^:dynamic conn (atom nil))

(defn init!
  "initialize wrapper queries for Yesql connectionless queries
   the wrappers will use the current connection defined in the conn atom
   unless one is explicitly passed in"
  [& filenames]
  (let [base-namespace *ns*
        queries-ns (-> *ns* ns-name name (str ".connectionless-queries") symbol)]
    (create-ns queries-ns)
    (in-ns queries-ns)
    (require '[yesql.core :as yesql])
    (doseq [filename filenames]
      (let [yesql-queries (yesql/defqueries filename)]
        (doall
         (for [yesql-query yesql-queries]
           (intern base-namespace
                   (with-meta (:name (meta yesql-query)) (meta yesql-queries))
                   (fn
                     ([] (yesql-query {} {:connection @conn}))
                     ([args] (yesql-query args {:connection @conn}))
                     ([args conn] (yesql-query args {:connection conn}))))))))
    (in-ns (ns-name base-namespace))))

(defmacro with-transaction
  "runs the body in a transaction where t-conn is the name of the transaction connection
   the body will be evaluated within a binding where conn is set to the transactional
   connection"
  [t-conn & body]
  `(jdbc/with-db-transaction [~t-conn @<<project-ns>>.db.core/conn]
     (binding [<<project-ns>>.db.core/conn (atom ~t-conn)]
       ~@body)))

(init! "sql/queries.sql")<% ifequal db-type "postgres" %>

(def pool-spec
  {:adapter    :postgresql
   :init-size  1
   :min-idle   1
   :max-idle   4
   :max-active 32})<% endifequal %><% ifequal db-type "mysql" %>

(def pool-spec
  {:adapter    :mysql
   :init-size  1
   :min-idle   1
   :max-idle   4
   :max-active 32})<% endifequal %>

(defn connect! []
  (try
    (reset!
     conn
     {:datasource
      (dbcp/make-datasource
       (assoc
        pool-spec
        :jdbc-url (to-jdbc-uri (env :database-url))))})
    (catch Throwable t
      (throw (Exception. "Error occured while connecting to the database!" t)))))

(defn disconnect! [conn]
  (when-let [ds (:datasource @conn)]
    (when-not (.isClosed ds)
      (.close ds)
      (reset! conn nil))))<% endifequal %><% ifequal db-type "mysql" %>

(defn to-date [sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Date
  (result-set-read-column [v _ _] (to-date v))

  java.sql.Timestamp
  (result-set-read-column [v _ _] (to-date v)))

(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt idx]
    (.setTimestamp stmt idx (java.sql.Timestamp. (.getTime v)))))
<% endifequal %><% ifequal db-type "postgres" %>

(defn to-date [sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(extend-protocol jdbc/IResultSetReadColumn
  Date
  (result-set-read-column [v _ _] (to-date v))

  Timestamp
  (result-set-read-column [v _ _] (to-date v))

  Jdbc4Array
  (result-set-read-column [v _ _] (vec (.getArray v)))

  PGobject
  (result-set-read-column [pgobj _metadata _index]
    (let [type  (.getType pgobj)
          value (.getValue pgobj)]
      (case type
        "json" (parse-string value true)
        "jsonb" (parse-string value true)
        "citext" (str value)
        value))))

(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt idx]
    (.setTimestamp stmt idx (Timestamp. (.getTime v)))))

(defn to-pg-json [value]
  (doto (PGobject.)
    (.setType "jsonb")
    (.setValue (generate-string value))))

(extend-protocol jdbc/ISQLValue
  IPersistentMap
  (sql-value [value] (to-pg-json value))
  IPersistentVector
  (sql-value [value] (to-pg-json value)))<% endifequal %>
