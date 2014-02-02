(ns leiningen.new.luminus
  (:use leiningen.new.dependency-injector
        leiningen.new.template-parser
        [leiningen.new.templates :only [renderer sanitize year ->files]]
        [leinjacker.utils :only [lein-generation]])
  (:import java.io.File
           java.util.regex.Matcher))

(declare ^{:dynamic true} *name*)
(declare ^{:dynamic true} *render*)
(def features (atom nil))

(defn sanitized-path [& path]
  (.replaceAll
   (str *name* "/src/" (sanitize *name*) (apply str path))
   "/" (Matcher/quoteReplacement File/separator)))

(defn add-sql-files [schema-file]
  [["src/log4j.xml" (*render* "dbs/log4j.xml")]
   ["src/{{sanitized}}/models/db.clj" (*render* "dbs/db.clj")]
   schema-file])

(defn add-sql-dependencies [project-file dependency]
  (add-dependencies project-file
                    dependency
                    ['korma "0.3.0-RC6"]
                    ['log4j "1.2.17"
                     :exclusions ['javax.mail/mail
                                  'javax.jms/jms
                                  'com.sun.jdmk/jmxtools
                                  'com.sun.jmx/jmxri]]))

(defmulti add-feature keyword)
(defmulti post-process (fn [feature _] (keyword feature)))

(defmethod add-feature :+cljs [_]
  [["src/{{sanitized}}/routes/cljsexample.clj"  (*render* "cljs/cljsexample.clj")]
   ["src-cljs/main.cljs"  (*render* "cljs/main.cljs")]
   ["src/{{sanitized}}/views/templates/cljsexample.html" (*render* "cljs/cljsexample.html")]])

(defmethod post-process :+cljs [_ project-file]
  (add-required (sanitized-path "/handler.clj")
                [(symbol (str *name* ".routes.cljsexample")) :refer ['cljs-routes]])
  (add-routes (sanitized-path "/handler.clj") 'cljs-routes)
  (add-dependencies project-file
                    ;;needed to get the latest version of ClojureScript until cljsbuild gets up to date
                    ['org.clojure/clojurescript "0.0-2138"]
                    ['domina "1.0.2"]
                    ['prismatic/dommy "0.1.2"]
                    ['cljs-ajax "0.2.3"])
  (add-plugins project-file ['lein-cljsbuild "0.3.3"])
  (add-to-project
   project-file
   :cljsbuild
   #_{:builds {:dev {:source-paths ["src-cljs"]
                   :compiler {:output-to "resources/public/js/site.js"
                              :optimizations :whitespace
                              :pretty-print true}}
             :prod {:source-paths ["src-cljs"]
                    :compiler {:output-to "resources/public/js/site.js"
                               :optimizations :advanced}}}}
   {:builds
    [{:source-paths ["src-cljs"]
      :compiler {:output-to "resources/public/js/site.js"
                 :optimizations :advanced
                 :pretty-print false}}]}))

(defmethod add-feature :+h2 [_]
  (add-sql-files ["src/{{sanitized}}/models/schema.clj" (*render* "dbs/h2_schema.clj")]))

(defmethod post-process :+h2 [_ project-file]
  (add-sql-dependencies project-file
                        ['com.h2database/h2 "1.3.175"]))

(defmethod add-feature :+postgres [_]
  (add-sql-files ["src/{{sanitized}}/models/schema.clj" (*render* "dbs/postgres_schema.clj")]))

(defmethod post-process :+postgres [_ project-file]
  (add-sql-dependencies project-file
                        ['postgresql/postgresql "9.1-901.jdbc4"]))

(defmethod add-feature :+mysql [_]
  (add-sql-files ["src/{{sanitized}}/models/schema.clj" (*render* "dbs/mysql_schema.clj")]))

(defmethod post-process :+mysql [_ project-file]
  (add-sql-dependencies project-file
                        ['mysql/mysql-connector-java "5.1.6"]))

(defmethod add-feature :+migrations [_]
  (let [timestamp (.format
                    (java.text.SimpleDateFormat. "ssmmHHMMyyyy")
                    (java.util.Date.))]
    [[(str "migrations/" timestamp "-add-users-table.up.sql") (*render* "migrations/add-users-table.up.sql")]
     [(str "migrations/" timestamp "-add-users-table.down.sql") (*render* "migrations/add-users-table.down.sql")]]))

(defmethod post-process :+migrations [_ project-file]
  (add-sql-dependencies project-file
                        ['ragtime "0.3.4"])
  (add-plugins project-file ['ragtime/ragtime.lein "0.3.4"])
  (add-to-project
   project-file
   :ragtime {:migrations 'ragtime.sql.files/migrations
             :database
             (let [postgres? (some #{"+postgres"} @features)]
               (str "jdbc:" (if postgres? "postgresql" "mysql")
                  "://localhost" (if postgres? "/" ":3306/") (sanitize *name*)
                  "?user=db_user_name_here&password=db_user_password_here"))})
  (let [docs-filename (str *name* "/resources/public/md/docs.md")]
    (spit docs-filename (str (*render* "dbs/db_instructions.html") (slurp docs-filename)))))

(defmethod add-feature :+http-kit [_]
  [["src//{{sanitized}}/core.clj"  (*render* "core.clj")]])

(defmethod post-process :+http-kit [_ project-file]
  (add-dependencies project-file ['http-kit "2.1.13"])
  (add-to-project project-file :main (symbol (str *name* ".core"))))

(defmethod add-feature :+site [_]
  [["src/{{sanitized}}/routes/auth.clj"                    (*render* "site/auth.clj")]
   ["src/{{sanitized}}/views/templates/menu.html"          (*render* "site/templates/menu.html")]
   ["src/{{sanitized}}/views/templates/base.html"          (*render* "site/templates/base.html")]
   ["src/{{sanitized}}/views/templates/profile.html"       (*render* "site/templates/profile.html")]
   ["src/{{sanitized}}/views/templates/registration.html"  (*render* "site/templates/registration.html")]])


(defmethod post-process :+site [_ project-file]
  (if-not (some #{"+h2" "+postgres" "+mysql"} @features)
    (post-process :+h2 project-file))
  (replace-expr (sanitized-path "/views/layout.clj")
                '(assoc params
                  (keyword (s/replace template #".html" "-selected")) "active"
                  :servlet-context (:context request))
                '(assoc params
                  (keyword (s/replace template #".html" "-selected")) "active"
                  :servlet-context (:context request)
                  :user-id (session/get :user-id)))
  (add-required (sanitized-path "/views/layout.clj")
                ['noir.session :as 'session])
  (add-required (sanitized-path "/handler.clj")
                [(symbol (str *name* ".routes.auth")) :refer ['auth-routes]]
                [(symbol (str *name* ".models.schema")) :as 'schema])
  (if-not (some #{"+postgres" "+mysql"} @features)
    (add-to-init (sanitized-path "/handler.clj")
                 '(if-not (schema/initialized?) (schema/create-tables))))
  (add-routes (sanitized-path "/handler.clj") 'auth-routes))

(defmethod add-feature :+site-dailycred [_]
  (into (add-feature :+site)
        [["src/{{sanitized}}/dailycred.clj"                      (*render* "dailycred/dailycred.clj")]
         ["src/{{sanitized}}/routes/auth.clj"                    (*render* "dailycred/auth.clj")]
         ["src/{{sanitized}}/views/templates/registration.html"  (*render* "dailycred/templates/registration.html")]]))

(defmethod post-process :+site-dailycred [_ project-file]
  (post-process :+site project-file))

(defmethod add-feature :default [feature]
  (throw (Exception. (str "unrecognized feature: " (name feature)))))

(defmethod post-process :default [_ _])

(defn include-features []
  (mapcat add-feature @features))

(defn inject-dependencies []
  (let [project-file (str *name* File/separator "project.clj")]

    (doseq [feature @features]
      (post-process feature project-file))

    (rewrite-template-tags (sanitized-path "/views/templates/"))
    (set-lein-version project-file "2.0.0")))

(defn site-required-features [features]
  (if-not (some #{"+h2" "+postgres" "+mysql"} features)
    (conj features :+h2) features))

(defn db-required-features [features]
  (if (some #{"+postgres" "+mysql"} features)
    (conj features :+migrations) features))

(defn site-params [feature-params]
  (if (some #{"+site"} feature-params)
    (site-required-features feature-params)
    feature-params))

(defn dailycred-params [feature-params]
  (if (some #{"+dailycred"} feature-params)
    (->> feature-params (remove #{"+dailycred" "+site"}) (cons "+site-dailycred") site-required-features)
    feature-params))

(defn generate-project [name feature-params data]
  (binding [*name*   name
            *render* #((renderer "luminus") % data)]
    (reset! features (-> feature-params dailycred-params site-params db-required-features))

    (println "Generating a lovely new Luminus project named" (str name "..."))

    (apply (partial ->files data)
           (into
            [[".gitignore"  (*render* "gitignore")]
             ["project.clj" (*render* "project.clj")]
             ["Procfile"    (*render* "Procfile")]
             ["README.md"   (*render* "README.md")]
             ;; core namespaces
             ["src/{{sanitized}}/handler.clj"                            (*render* "handler.clj")]
             ["src/{{sanitized}}/middleware.clj"                         (*render* "middleware.clj")]
             ["src/{{sanitized}}/repl.clj"                               (*render* "repl.clj")]
             ["src/{{sanitized}}/util.clj"                               (*render* "util.clj")]
             ["src/{{sanitized}}/routes/home.clj"                        (*render* "home.clj")]
             ["src/{{sanitized}}/views/layout.clj"                       (*render* "layout.clj")]
             ;; public resources, example URL: /css/screen.css
             ["src/{{sanitized}}/views/templates/base.html"              (*render* "templates/base.html")]
             ["src/{{sanitized}}/views/templates/home.html"              (*render* "templates/home.html")]
             ["src/{{sanitized}}/views/templates/about.html"             (*render* "templates/about.html")]
             ["resources/public/css/screen.css"                          (*render* "screen.css")]
             ["resources/public/css/bootstrap-theme.min.css"             (*render* "bootstrap/css/bootstrap-theme.min.css")]
             ["resources/public/css/bootstrap.min.css"                   (*render* "bootstrap/css/bootstrap.min.css")]
             ["resources/public/js/bootstrap.min.js"                     (*render* "bootstrap/js/bootstrap.min.js")]
             ["resources/public/fonts/glyphicons-halflings-regular.eot"  (*render* "bootstrap/fonts/glyphicons-halflings-regular.eot")]
             ["resources/public/fonts/glyphicons-halflings-regular.svg"  (*render* "bootstrap/fonts/glyphicons-halflings-regular.svg")]
             ["resources/public/fonts/glyphicons-halflings-regular.ttf"  (*render* "bootstrap/fonts/glyphicons-halflings-regular.ttf")]
             ["resources/public/fonts/glyphicons-halflings-regular.woff" (*render* "bootstrap/fonts/glyphicons-halflings-regular.woff")]
             ["resources/public/md/docs.md"                              (*render* "docs.md")]
             "resources/public/js"
             "resources/public/img"
             ;; tests
             ["test/{{sanitized}}/test/handler.clj" (*render* "handler_test.clj")]]
            (include-features)))
    (inject-dependencies) ))

(defn format-features [features]
  (apply str (interpose ", " features)))

(defn luminus
  "Create a new Luminus project"
  [name & feature-params]
  (let [supported-features #{"+cljs" "+site" "+h2" "+postgres" "+dailycred" "+mysql" "+http-kit"}
        data {:name name
              :sanitized (sanitize name)
              :year (year)}
        unsupported (-> (set feature-params)
                        (clojure.set/difference supported-features)
                        (not-empty))
        feature-params (set feature-params)]

    (cond
     (< (lein-generation) 2)
     (println "Leiningen version 2.x is required.")

     (re-matches #"\A\+.+" name)
     (println "Project name is missing.\nTry: lein new luminus PROJECT_NAME"
              name (clojure.string/join " " feature-params))

     unsupported
     (println "Unrecognized options:" (format-features unsupported)
              "\nSupported options are:" (format-features supported-features))

     (.exists (new File name))
     (println "Could not create project because a directory named" name "already exists!")

     :else
     (generate-project name feature-params data))))
