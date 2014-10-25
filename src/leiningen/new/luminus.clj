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

(defn sanitized-resource-path [& path]
  (.replaceAll
   (str *name* "/resources/" (apply str path))
   "/" (Matcher/quoteReplacement File/separator)))

(defn add-sql-files [schema-file]
  [["resources/log4j.properties" (*render* "log4j.properties")]
   ["src/{{sanitized}}/db/core.clj" (*render* "dbs/db.clj")]
   schema-file])

(defn add-mongo-files []
  [["src/{{sanitized}}/db/core.clj" (*render* "dbs/mongodb.clj")]])

(defn add-sql-dependencies [project-file dependency]
  (add-dependencies project-file
                    dependency
                    ['korma "0.4.0"]
                    ['log4j "1.2.17"
                     :exclusions ['javax.mail/mail
                                  'javax.jms/jms
                                  'com.sun.jdmk/jmxtools
                                  'com.sun.jmx/jmxri]]))

(defn add-mongo-dependencies [project-file dependency]
  (add-dependencies project-file
                    dependency
                    ['log4j "1.2.17"
                     :exclusions ['javax.mail/mail
                                  'javax.jms/jms
                                  'com.sun.jdmk/jmxtools
                                  'com.sun.jmx/jmxri]]))

(defmulti add-feature keyword)
(defmulti post-process (fn [feature _] (keyword feature)))

(defmethod add-feature :+cljs [_]
  [["src/{{sanitized}}/routes/home.clj" (*render* "cljs/home.clj")]
   ["src-cljs/{{sanitized}}/core.cljs" (*render* "cljs/core.cljs")]
   ["env/dev/cljs/{{sanitized}}/dev.cljs" (*render* "cljs/env/dev/cljs/app.cljs")]
   ["env/prod/cljs/{{sanitized}}/prod.cljs" (*render* "cljs/env/prod/cljs/app.cljs")]
   ["resources/templates/app.html" (*render* "cljs/app.html")]])

(defmethod post-process :+cljs [_ project-file]
  (replace-expr (sanitized-path "/layout.clj")
                '(assoc params
                   (keyword (s/replace template #".html" "-selected")) "active"
                   :dev (env :dev)
                   :servlet-context
                   (if-let [context (:servlet-context request)]
                     (try
                       (.getContextPath context)
                       (catch IllegalArgumentException _ context))))
                '(assoc params
                   :dev (env :dev)
                   :servlet-context
                   (if-let [context (:servlet-context request)]
                     (try
                       (.getContextPath context)
                       (catch IllegalArgumentException _ context)))))
  (add-dependencies project-file
                    ;;needed to get the latest version of ClojureScript until cljsbuild gets up to date
                    ['org.clojure/clojurescript "0.0-2371"]
                    ['reagent-forms "0.2.4"]
                    ['secretary "1.2.1"]
                    ['cljs-ajax "0.3.3"])
  (add-plugins project-file ['lein-cljsbuild "1.0.3"])
  (add-to-profile
    project-file
    :dev
    {:cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]}}}})
  (add-to-profile
    project-file
    :uberjar
    {:hooks ['leiningen.cljsbuild]
     :cljsbuild {:jar true
                 :builds {:app
                          {:source-paths ["env/prod/cljs"]
                           :compiler
                           {:optimizations :advanced
                            :pretty-print false}}}}})
  (add-to-project
   project-file
   :cljsbuild {:builds {:app {:source-paths ["src-cljs"]
                              :compiler {:output-to     "resources/public/js/app.js"
                                         :output-dir    "resources/public/js/out"
                                         :source-map    "resources/public/js/out.js.map"
                                         :externs       ["react/externs/react.js"]
                                         :optimizations :none
                                         :pretty-print  true}}}}))

(defmethod add-feature :+h2 [_]
  (add-sql-files ["src/{{sanitized}}/db/schema.clj" (*render* "dbs/h2_schema.clj")]))

(defmethod post-process :+h2 [_ project-file]
  (add-sql-dependencies project-file
                        ['com.h2database/h2 "1.4.181"]))

(defmethod add-feature :+postgres [_]
  (add-sql-files ["src/{{sanitized}}/db/schema.clj" (*render* "dbs/postgres_schema.clj")]))

(defmethod post-process :+postgres [_ project-file]
  (add-sql-dependencies project-file
                        ['org.postgresql/postgresql "9.3-1102-jdbc41"]))

(defmethod add-feature :+mysql [_]
  (add-sql-files ["src/{{sanitized}}/db/schema.clj" (*render* "dbs/mysql_schema.clj")]))

(defmethod post-process :+mysql [_ project-file]
  (add-sql-dependencies project-file
                        ['mysql/mysql-connector-java "5.1.6"]))

(defmethod add-feature :+mongodb [_]
  (add-mongo-files))

(defmethod post-process :+mongodb [_ project-file]
  (add-mongo-dependencies project-file
                          ['com.novemberain/monger "2.0.0"])
  (let [docs-filename (str *name* "/resources/public/md/docs.md")]
    (spit docs-filename (str (*render* "dbs/mongo_instructions.html") (slurp docs-filename)))))

(defmethod add-feature :+migrations [_]
  (let [timestamp (.format
                    (java.text.SimpleDateFormat. "yyyyMMHHmmss")
                    (java.util.Date.))]
    [[(str "migrations/" timestamp "-add-users-table.up.sql") (*render* "migrations/add-users-table.up.sql")]
     [(str "migrations/" timestamp "-add-users-table.down.sql") (*render* "migrations/add-users-table.down.sql")]]))

(defmethod post-process :+migrations [_ project-file]
  (add-sql-dependencies project-file
                        ['ragtime "0.3.6"])
  (add-plugins project-file ['ragtime/ragtime.lein "0.3.6"])
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
  (add-dependencies project-file ['http-kit "2.1.18"])
  (add-to-project project-file :main (symbol (str *name* ".core"))))

(defmethod add-feature :+cucumber [_]
  [["test/{{sanitized}}/browser.clj"                     (*render* "site/templates/browser.clj")]
   ["resources/log4j.properties"                         (*render* "log4j.properties")]
   ["test/features/step_definitions/home_page_steps.clj" (*render* "site/templates/home_page_steps.clj")]
   ["test/features/index_page.feature"                   (*render* "site/templates/index_page.feature")]])

(defmethod post-process :+cucumber [_ project-file]
  (add-profile-dependencies project-file :dev ['org.clojure/core.cache "0.6.3"]
                                              ['clj-webdriver/clj-webdriver "0.6.1"])
  (add-plugins project-file ['lein-cucumber "1.0.2"])
  (add-to-project project-file :cucumber-feature-paths ["test/features/"]))

(defmethod add-feature :+site [_]
  [["src/{{sanitized}}/routes/auth.clj"     (*render* "site/auth.clj")]
   ["resources/templates/menu.html"         (*render* "site/templates/menu.html")]
   ["resources/templates/base.html"         (*render* "site/templates/base.html")]
   ["resources/templates/profile.html"      (*render* "site/templates/profile.html")]
   ["resources/templates/registration.html" (*render* "site/templates/registration.html")]])

(defmethod post-process :+site [_ project-file]
  (if-not (some #{"+h2" "+postgres" "+mysql" "+mongodb"} @features)
    (post-process :+h2 project-file))
  (replace-expr (sanitized-path "/layout.clj")
                '(assoc params
                  (keyword (s/replace template #".html" "-selected")) "active"
                  :dev (env :dev)
                  :servlet-context
                  (if-let [context (:servlet-context request)]
                    (try
                      (.getContextPath context)
                      (catch IllegalArgumentException _ context))))
                '(assoc params
                  (keyword (s/replace template #".html" "-selected")) "active"
                  :dev (env :dev)
                  :servlet-context
                  (if-let [context (:servlet-context request)]
                    (try
                      (.getContextPath context)
                      (catch IllegalArgumentException _ context)))
                  :user-id (session/get :user-id)))
  (add-required (sanitized-path "/layout.clj")
                ['noir.session :as 'session])
  (add-required (sanitized-path "/handler.clj")
                [(symbol (str *name* ".routes.auth")) :refer ['auth-routes]])
  (if-not (some #{"+mongodb"} @features)
    (add-required (sanitized-path "/handler.clj")
                  [(symbol (str *name* ".db.schema")) :as 'schema]))
  (if-not (some #{"+postgres" "+mysql" "+mongodb"} @features)
    (add-to-init (sanitized-path "/handler.clj")
                 '(if-not (schema/initialized?) (schema/create-tables))))
  (add-routes (sanitized-path "/handler.clj") 'auth-routes))

(defmethod add-feature :+site-dailycred [_]
  (into (add-feature :+site)
        [["src/{{sanitized}}/dailycred.clj"       (*render* "dailycred/dailycred.clj")]
         ["src/{{sanitized}}/routes/auth.clj"     (*render* "dailycred/auth.clj")]
         ["resources/templates/registration.html" (*render* "dailycred/templates/registration.html")]]))

(defmethod post-process :+site-dailycred [_ project-file]
  (post-process :+site project-file))

(defmethod add-feature :+static [_]
  [
   ;; Add documentation page about static generator
   ["STATIC_SITE_GENERATOR.md"
    (*render* "static/STATIC_SITE_GENERATOR.md")]

   ;; Add bootstrap and jquery as local resources
   ["resources/public/css/bootstrap/3.2.0/bootstrap.min.css"
    (*render* "bootstrap/3.2.0/css/bootstrap.min.css")]
   ["resources/public/css/bootstrap/3.2.0/bootstrap-theme.min.css"
    (*render* "bootstrap/3.2.0/css/bootstrap-theme.min.css")]
   ["resources/public/js/jquery/2.0.3/jquery-2.0.3.min.js"
    (*render* "jquery/jquery-2.0.3.min.js")]
   ["resources/public/js/bootstrap/3.2.0/bootstrap.min.js"
    (*render* "bootstrap/3.2.0/js/bootstrap.min.js")]

   ;; static.clj contains logic for generating static site
   ["src/{{sanitized}}/static.clj" (*render* "static/static.clj")]
   ;;contains the routes for the static site
   ["src/{{sanitized}}/static_site.clj" (*render* "static/static_site.clj")]
   ;; home.clj updated with new routes
   ["src/{{sanitized}}/routes/home.clj" (*render* "static/home.clj")]

   ;; change base.html to serve local files
   ["resources/templates/base.html" (*render* "static/templates/base.html")]])

(defmethod post-process :+static [_ project-file]
  (add-dependencies project-file
                    ['commons-io/commons-io "2.4"]
                    ['org.clojure/tools.cli "0.3.1"])
  (add-to-project project-file :main (symbol (str *name* ".static"))))

(defmethod add-feature :default [feature]
  (throw (Exception. (str "unrecognized feature: " (name feature)))))

(defmethod post-process :default [_ _])

(defn include-features []
  (mapcat add-feature @features))

(defn inject-dependencies []
  (let [project-file (str *name* File/separator "project.clj")]

    (doseq [feature @features]
      (post-process feature project-file))

    (rewrite-template-tags (sanitized-resource-path "/templates/"))
    (set-lein-version project-file "2.0.0")))

(defn site-required-features [features]
  (if-not (some #{"+h2" "+postgres" "+mysql" "+mongodb"} features)
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
           (concat
             [[".gitignore"                                               (*render* "gitignore")]
              ["project.clj"                                              (*render* "project.clj")]
              ["Procfile"                                                 (*render* "Procfile")]
              ["README.md"                                                (*render* "README.md")]
              ;; core namespaces
              ["src/{{sanitized}}/session_manager.clj"                    (*render* "session_manager.clj")]
              ["src/{{sanitized}}/handler.clj"                            (*render* "handler.clj")]
              ["src/{{sanitized}}/middleware.clj"                         (*render* "middleware.clj")]
              ["src/{{sanitized}}/repl.clj"                               (*render* "repl.clj")]
              ["src/{{sanitized}}/util.clj"                               (*render* "util.clj")]
              ["src/{{sanitized}}/routes/home.clj"                        (*render* "home.clj")]
              ["src/{{sanitized}}/layout.clj"                             (*render* "layout.clj")]
              ;; public resources, example URL: /css/screen.css

              ["resources/public/css/screen.css"                          (*render* "screen.css")]
              ["resources/public/fonts/glyphicons-halflings-regular.eot"  (*render* "bootstrap/fonts/glyphicons-halflings-regular.eot")]
              ["resources/public/fonts/glyphicons-halflings-regular.svg"  (*render* "bootstrap/fonts/glyphicons-halflings-regular.svg")]
              ["resources/public/fonts/glyphicons-halflings-regular.ttf"  (*render* "bootstrap/fonts/glyphicons-halflings-regular.ttf")]
              ["resources/public/fonts/glyphicons-halflings-regular.woff" (*render* "bootstrap/fonts/glyphicons-halflings-regular.woff")]
              ["resources/public/md/docs.md"                              (*render* "docs.md")]
              "resources/public/js"
              "resources/public/img"
              ;; tests
              ["test/{{sanitized}}/test/handler.clj" (*render* "handler_test.clj")]]
              (when-not (some #{"+cljs"} @features)
                [["resources/templates/base.html"                            (*render* "templates/base.html")]
                 ["resources/templates/home.html"                            (*render* "templates/home.html")]
                 ["resources/templates/about.html"                           (*render* "templates/about.html")]])
              (include-features)))
    (inject-dependencies) ))

(defn format-features [features]
  (apply str (interpose ", " features)))

(defn luminus
  "Create a new Luminus project"
  [name & feature-params]
  (let [supported-features #{"+cljs" "+site" "+h2" "+postgres" "+dailycred" "+mysql" "+http-kit" "+cucumber" "+mongodb" "+static"}
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
