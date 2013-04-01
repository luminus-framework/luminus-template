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

(defn check-lein-version [& [prefix]]
  (if (< (lein-generation) 2)
    (throw (new Exception "Leiningen version 2.x is required"))))

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
                    ['org.clojure/java.jdbc "0.2.3"]
                    dependency
                    ['korma "0.3.0-RC5"]
                    ['log4j "1.2.15"
                     :exclusions ['javax.mail/mail
                                  'javax.jms/jms
                                  'com.sun.jdmk/jmxtools
                                  'com.sun.jmx/jmxri]]))

(defmulti add-feature keyword)
(defmulti post-process (fn [feature _] (keyword feature)))

(defmethod add-feature :+bootstrap [_]
  [["resources/public/css/bootstrap-responsive.min.css"   (*render* "bootstrap/css/bootstrap-responsive.min.css")]
   ["resources/public/css/bootstrap.min.css"              (*render* "bootstrap/css/bootstrap.min.css")]
   ["resources/public/css/screen.css"                     (*render* "bootstrap/css/screen.css")]
   ["resources/public/js/bootstrap.min.js"                (*render* "bootstrap/js/bootstrap.min.js")]
   ["resources/public/img/glyphicons-halflings-white.png" (*render* "bootstrap/img/glyphicons-halflings-white.png")]
   ["resources/public/img/glyphicons-halflings.png"       (*render* "bootstrap/img/glyphicons-halflings.png")]])

(defmethod post-process :+bootstrap [_ project-file]
  ;;site base.html template already has bootstrap included
  (if-not (some #{"+site"} @features)
    (add-to-layout (sanitized-path "/views/templates/base.html")
                   ["{{context}}/css/bootstrap.min.css"
                    "{{context}}/css/bootstrap-responsive.min.css"]
                   ["//ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js"
                    "{{context}}/js/bootstrap.min.js"])))

(defmethod add-feature :+cljs [_]
  [["src-cljs/{{sanitized}}/tetris.cljs" (*render* "cljs/tetris.cljs")]
   ["src-cljs/{{sanitized}}/game.cljs" (*render* "cljs/game.cljs")]
   ["resources/public/tetris.html" (*render* "cljs/tetris.html")]])

(defmethod post-process :+cljs [_ project-file]
  (add-dependencies project-file ['jayq "2.3.0"] ['prismatic/dommy "0.0.2"])
  (add-plugins project-file ['lein-cljsbuild "0.3.0"])
  (add-to-project
   project-file
   :cljsbuild
   {:builds
    [{:source-path "src-cljs",
      :compiler {:output-to "resources/public/js/tetris.js"
                 :optimizations :advanced
                 :pretty-print false}}]}))

(defmethod add-feature :+h2 [_]
  (add-sql-files ["src/{{sanitized}}/models/schema.clj" (*render* "dbs/h2_schema.clj")]))

(defmethod post-process :+h2 [_ project-file]
  (add-sql-dependencies project-file
                        ['com.h2database/h2 "1.3.170"]))

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

(defn site-required-features []
  (remove empty?
          (concat
           [["src/{{sanitized}}/handler.clj" (*render* "site/handler.clj")]]
           (if-not (some #{"+bootstrap"} @features)
             (do
               (swap! features conj "+bootstrap")
               (add-feature :+bootstrap)))
           (if-not (some #{"+h2" "+postgres" "+mysql"} @features)
             (add-feature :+h2)))))

(defmethod add-feature :+site [_]
  (into
   [["src/{{sanitized}}/routes/home.clj"                    (*render* "home.clj")]
    ["src/{{sanitized}}/routes/auth.clj"                    (*render* "site/auth.clj")]
    ["src/{{sanitized}}/views/layout.clj"                   (*render* "site/layout.clj")]
    ["src/{{sanitized}}/views/templates/home.html"          (*render* "templates/home.html")]
    ["src/{{sanitized}}/views/templates/about.html"         (*render* "templates/about.html")]
    ["src/{{sanitized}}/views/templates/base.html"          (*render* "site/templates/base.html")]
    ["src/{{sanitized}}/views/templates/profile.html"       (*render* "site/templates/profile.html")]
    ["src/{{sanitized}}/views/templates/registration.html"  (*render* "site/templates/registration.html")]]
   (site-required-features)))

(defmethod post-process :+site [_ project-file]
  (if-not (some #{"+h2" "+postgres"} @features)
    (post-process :+h2 project-file)))

(defmethod add-feature :+site-dailycred [_]
  (into (add-feature :+site)
        [["src/{{sanitized}}/dailycred.clj"                      (*render* "dailycred/dailycred.clj")]
         ["src/{{sanitized}}/routes/auth.clj"                    (*render* "dailycred/auth.clj")]
         ["src/{{sanitized}}/views/templates/registration.html"  (*render* "dailycred/templates/registration.html")]]))

(defmethod post-process :+site-dailycred [_ project-file]
  (post-process :+site project-file))

(defmethod add-feature :default [feature]
  (throw (new Exception
              (str "unrecognized feature: " (name feature)))))

(defmethod post-process :default [_ _])

(defn include-features []
  (mapcat add-feature @features))

(defn inject-dependencies []
  (let [project-file (str *name* File/separator "project.clj")]

    (doseq [feature @features]
      (post-process feature project-file))

    (rewrite-template-tags (sanitized-path "/views/templates/"))
    (set-lein-version project-file "2.0.0")))

(defn generate-project [name feature-params data]
  (binding [*name*     name
            *render*   #((renderer "luminus") % data)]
    (reset! features
            (if (some #{"+dailycred"} feature-params)
              (->> feature-params (remove #{"+dailycred" "+site"}) (cons "+site-dailycred"))
              feature-params))

    (println "Generating a lovely new Luminus project named" (str name "..."))

    (apply (partial ->files data)
           (into
            [[".gitignore"  (*render* "gitignore")]
             ["project.clj" (*render* "project.clj")]
             ["Procfile"    (*render* "Procfile")]
             ["README.md"   (*render* "README.md")]
             ;; core namespaces
             ["src/{{sanitized}}/handler.clj" (*render* "handler.clj")]
             ["src/{{sanitized}}/repl.clj"  (*render* "repl.clj")]
             ["src/{{sanitized}}/util.clj"    (*render* "util.clj")]
             ["src/{{sanitized}}/routes/home.clj"            (*render* "home.clj")]
             ["src/{{sanitized}}/views/layout.clj"           (*render* "layout.clj")]
             ;; public resources, example URL: /css/screen.css
             ["src/{{sanitized}}/views/templates/base.html"  (*render* "templates/base.html")]
             ["src/{{sanitized}}/views/templates/home.html"  (*render* "templates/home.html")]
             ["src/{{sanitized}}/views/templates/about.html" (*render* "templates/about.html")]
             ["resources/public/css/screen.css" (*render* "screen.css")]
             ["resources/public/md/docs.md" (*render* "docs.md")]
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
  (check-lein-version)
  (let [supported-features #{"+bootstrap" "+cljs" "+site" "+h2" "+postgres" "+dailycred" "+mysql"}
        data {:name name
              :sanitized (sanitize name)
              :year (year)}
        unsupported (-> (set feature-params)
                        (clojure.set/difference supported-features)
                        (not-empty))]

    (cond
     unsupported
     (println "unrecognized options:" (format-features unsupported)
              "\nsupported options are:" (format-features supported-features))

     (.exists (new File name))
     (println "Could not create project because a directory named" name "already exists!")

     :else
     (generate-project name feature-params data))))
