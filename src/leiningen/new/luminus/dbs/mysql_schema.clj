(ns {{name}}.db.schema)

(def db-spec
  {:subprotocol "mysql"
   :subname "//localhost:3306/{{sanitized}}"
   :user "db_user_name_here"
   :password "db_user_password_here"})

