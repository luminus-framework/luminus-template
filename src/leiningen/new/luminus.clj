(ns leiningen.new.luminus
  (:use [leiningen.new.dependency-injector]
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
  (add-to-layout (.replaceAll
                  (str *name* "/src/" (sanitize *name*) "/views/layout.clj")
                         "/" (Matcher/quoteReplacement File/separator))
                 ["/css/bootstrap.min.css"
                  "/css/bootstrap-responsive.min.css"]
                 ["//ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js"
                  "/js/bootstrap.min.js"]))


(defmethod add-feature :+cljs [_]
  [["src-cljs/{{sanitized}}/tetris.cljs" (*render* "cljs/tetris.cljs")]
   ["src-cljs/{{sanitized}}/game.cljs" (*render* "cljs/game.cljs")]
   ["resources/public/tetris.html" (*render* "cljs/tetris.html")]])

(defmethod post-process :+cljs [_ project-file]
  (add-dependencies project-file ['jayq "2.0.0"] ['crate "0.2.3"])
  (add-plugins project-file ['lein-cljsbuild "0.2.10"])
  (add-to-project
    project-file
    :cljsbuild
    {:builds
     [{:source-path "src-cljs",
       :compiler {:output-to "resources/public/js/tetris.js"
                  :optimizations :advanced
                  :pretty-print false}}]}))

(defmethod add-feature :+h2 [_]
  [["src/{{sanitized}}/models/db.clj" (*render* "dbs/db.clj")]
   ["src/{{sanitized}}/models/schema.clj" (*render* "dbs/h2_schema.clj")]])

(defmethod post-process :+h2 [_ project-file]
  (add-dependencies project-file
                    ['org.clojure/java.jdbc "0.2.3"]
                    ['com.h2database/h2 "1.3.170"]))

(defmethod add-feature :+postgres [_]
  [["src/{{sanitized}}/models/db.clj" (*render* "dbs/db.clj")]
   ["src/{{sanitized}}/models/schema.clj" (*render* "dbs/postgres_schema.clj")]])

(defmethod post-process :+postgres [_ project-file]
  (add-dependencies project-file
                    ['org.clojure/java.jdbc "0.2.3"]
                    ['postgresql/postgresql "9.1-901.jdbc4"]))


(defmethod add-feature :+site [_]
  (remove empty?
          (concat
            [["src/{{sanitized}}/views/layout.clj" (*render* "site/layout.clj")]
             ["src/{{sanitized}}/routes/auth.clj"  (*render* "site/auth.clj")]
             ["src/{{sanitized}}/handler.clj"      (*render* "site/handler.clj")]]
            (if-not (some #{"+bootstrap"} @features)
              (do
                (swap! features conj "+bootstrap")
                (add-feature :+bootstrap)))
            (if-not (some #{"+h2" "+postgres"} @features)
              (do
                (swap! features conj "+h2")
                (add-feature :+h2))))))

(defmethod post-process :+site [_ _])


(defmethod add-feature :default [feature]
 (throw (new Exception (str "unrecognized feature: " (name feature)))))

(defmethod post-process :default [_ _])



(defn inject-dependencies []
  (let [project-file (str *name* File/separator "project.clj")]
    (doseq [feature @features]
      (post-process feature project-file))
    (set-lein-version project-file "2.0.0")))

(defn include-features []
  (mapcat add-feature @features))

(defn luminus
  "Create a new Luminus project"
  [name & feature-params]
  (check-lein-version)
  (let [data {:name name
              :sanitized (sanitize name)
              :year (year)}]

    (binding [*name*     name
              *render*   #((renderer "luminus") % data)]
      (reset! features feature-params)
      (println "Generating a lovely new Luminus project named" (str name "..."))
      (apply (partial ->files data)
             (into
               [[".gitignore"  (*render* "gitignore")]
                ["project.clj" (*render* "project.clj")]
                ["Procfile"    (*render* "Procfile")]
                ["README.md"   (*render* "README.md")]
                ;;logging properties for Korma
                ["src/log4j.xml" (*render* "dbs/log4j.xml")]
                ;; core namespaces
                ["src/{{sanitized}}/handler.clj" (*render* "handler.clj")]
                ["src/{{sanitized}}/repl.clj"  (*render* "repl.clj")]
                ["src/{{sanitized}}/util.clj"    (*render* "util.clj")]
                ;; application routes
                ["src/{{sanitized}}/routes/home.clj"  (*render* "home.clj")]
                ;; views
                ["src/{{sanitized}}/views/layout.clj"  (*render* "layout.clj")]
                ;; public resources, example URL: /css/screen.css
                ["resources/public/css/screen.css" (*render* "screen.css")]
                ["resources/public/md/docs.md" (*render* "docs.md")]
                "resources/public/js"
                "resources/public/img"
                ;; tests
                ["test/{{sanitized}}/test/handler.clj" (*render* "handler_test.clj")]]
               (include-features)))
      (inject-dependencies))))
