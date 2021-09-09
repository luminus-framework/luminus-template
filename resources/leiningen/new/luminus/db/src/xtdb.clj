(ns <<project-ns>>.db.core
  (:require
    [xtdb.api :as xt]
    [mount.core :refer [defstate]]
    [<<project-ns>>.config :refer [env]])
  (:import (java.util UUID)))

(defstate node
  :start (doto (xt/start-node (:xtdb-config env))
           xt/sync)
  :stop (-> node .close))

(defn- put-user!
  [node user]
  (let [user-doc (assoc user :xt/id (:user/id user)
                             :<<project-ns>>/type :user)]
    (xt/await-tx
      node
      (xt/submit-tx node [[::xt/put user-doc]]))))

(defn- remove-type-and-xtdb-id
  [user-doc]
  (dissoc user-doc :<<project-ns>>/type :xt/id))

(defn create-user!
  "e.g.
    (create-user! node {:user/email \"test@example.com\"
                        :user/name \"Example user\"})"
  [node user]
  (let [user-with-id (assoc user :user/id (UUID/randomUUID))]
    (put-user! node user-with-id)
    user-with-id))

(defn update-user!
  [node user]
  (put-user! node user))

(defn find-user-by-attribute
  [node attr value]
  (-> (xt/q
        (xt/db node)
        {:find  '[(pull user [*])]
         :where [['user attr 'value]
                 ['user :<<project-ns>>/type :user]]
         :in    '[attr value]}
        attr value)
      ffirst
      remove-type-and-xtdb-id))

(defn find-user-by-id
  [node id]
  (find-user-by-attribute node :user/id id))
