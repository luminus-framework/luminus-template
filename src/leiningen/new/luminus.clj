(ns leiningen.new.luminus
  (:use [leiningen.new.dependency-injector]
        [leiningen.new.templates :only [renderer sanitize year ->files]]
        [leinjacker.utils :only [lein-generation]]))

(declare ^{:dynamic true} *name*)
(declare ^{:dynamic true} *render*)
(def features (atom nil))

(defn project-file [& [prefix]]
  (if (= (lein-generation) 2)
    (str prefix "project_lein2.clj")
    (str prefix "project_lein2.clj")))

(defmulti add-feature keyword)

(defmethod add-feature :+bootstrap [_] 
  [["resources/public/css/bootstrap-responsive.min.css"   (*render* "bootstrap/css/bootstrap-responsive.min.css")]
   ["resources/public/css/bootstrap.min.css"              (*render* "bootstrap/css/bootstrap.min.css")]
   ["resources/public/css/screen.css"                     (*render* "bootstrap/css/screen.css")]
   ["resources/public/js/bootstrap.min.js"                (*render* "bootstrap/js/bootstrap.min.js")]
   ["resources/public/img/glyphicons-halflings-white.png" (*render* "bootstrap/img/glyphicons-halflings-white.png")]
   ["resources/public/img/glyphicons-halflings.png"       (*render* "bootstrap/img/glyphicons-halflings.png")]])

(defmethod add-feature :+cljs [_]
  [["src-cljs/{{sanitized}}/tetris.cljs" (*render* "cljs/tetris.cljs")]
   ["src-cljs/{{sanitized}}/game.cljs" (*render* "cljs/game.cljs")]
   ["resources/public/tetris.html" (*render* "cljs/tetris.html")]])

(defmethod add-feature :+h2 [_]  
  [["src/{{sanitized}}/models/db.clj" (*render* "dbs/h2_db.clj")]])

(defmethod add-feature :+sqlite [_]  
  [["src/{{sanitized}}/models/db.clj" (*render* "dbs/sqlite_db.clj")]])

(defmethod add-feature :+postgres [_]    
  [["src/{{sanitized}}/models/db.clj" (*render* "dbs/postgres_db.clj")]])

(defmethod add-feature :+site [_]
  (remove empty?
          (concat
            [["src/{{sanitized}}/views/layout.clj" (*render* "site/layout.clj")]
             ["src/{{sanitized}}/routes/auth.clj"  (*render* "site/auth.clj")]
             ["src/{{sanitized}}/handler.clj"      (*render* "site/handler.clj")]]
            (if-not (some #{"+bootstrap"} @features) (add-feature :+bootstrap))
            (if-not (some #{"+sqlite" "+h2" "+postgres"} @features) 
              (do
                (swap! features conj "+sqlite")
                (add-feature :+sqlite))))))

(defn inject-dependencies []
  (let [project-file (str *name* java.io.File/separator "project.clj")] 
    (cond 
      (some #{"+cljs"} @features)
      (do
        (add-dependencies project-file 
                          ['jayq "2.0.0"]
                          ['crate "0.2.3"])
        (add-plugins project-file ['lein-cljsbuild "0.2.10"])
        (add-to-project 
          project-file
          :cljsbuild
          {:builds
           [{:source-path "src-cljs",      
             :compiler {:output-to "resources/public/js/tetris.js"
                        :optimizations :advanced
                        :pretty-print false}}]}))
      
      (some #{"+sqlite"} @features)
      (add-dependencies project-file  
        ['org.clojure/java.jdbc "0.2.3"]
        ['org.xerial/sqlite-jdbc "3.7.2"])
      
      (some #{"+h2"} @features)
      (add-dependencies project-file 
                        ['org.clojure/java.jdbc "0.2.3"]
                        ['com.h2database/h2 "1.3.170"])
      
      (some #{"+postgres"} @features)
      (add-dependencies project-file 
                        ['org.clojure/java.jdbc "0.2.3"]
                        ['postgresql/postgresql "9.1-901.jdbc4"]))))

(defmethod add-feature :default [feature]
 (throw (new Exception (str "unrecognized feature: " feature))))

(defn include-features []
  (mapcat add-feature @features))

(defn luminus
  "Create a new Luminus project"
  [name & feature-params]
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
                ["project.clj" (*render* (project-file))]
                ["README.md"   (*render* "README.md")]
                ["Procfile"    (*render* "Procfile")]
                ;; core namespaces
                ["src/{{sanitized}}/handler.clj" (*render* "handler.clj")]
                ["src/{{sanitized}}/server.clj"  (*render* "server.clj")]             
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
