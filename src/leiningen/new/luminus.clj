(ns leiningen.new.luminus
  (:use [leiningen.new.templates :only [renderer sanitize year ->files]]
        [leinjacker.utils :only [lein-generation]]))

(declare ^{:dynamic true} *render*)
(declare ^{:dynamic true} *features*)

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

(defmethod add-feature :+sqlite [_]
  [["project.clj" (*render* (project-file (str "sqlite" java.io.File/separator)))]   
   ["src/{{sanitized}}/models/db.clj"    (*render* "sqlite/db.clj")]])

(defmethod add-feature :+site [_]
  (remove empty?
          (concat
            [["src/{{sanitized}}/views/layout.clj" (*render* "site/layout.clj")]
             ["src/{{sanitized}}/routes/auth.clj"  (*render* "site/auth.clj")]
             ["src/{{sanitized}}/handler.clj"      (*render* "site/handler.clj")]]
            (if-not (some #{"+bootstrap"} *features*) (add-feature :+bootstrap))
            (if-not (some #{"+sqlite"} *features*)    (add-feature :+sqlite)))))

(defmethod add-feature :default [feature]
 (throw (new Exception (str "unrecognized feature: " feature))))

(defn include-features [features]
  (mapcat add-feature features))

(defn luminus
  "Create a new Luminus project"
  [name & features]
  (let [data {:name name
              :sanitized (sanitize name)
              :year (year)}]
    
    (binding [*render*   #((renderer "luminus") % data)
              *features* features]
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
                "resources/public/md"
                "resources/public/js"
                "resources/public/img"
                ;; tests
                ["test/{{sanitized}}/test/handler.clj" (*render* "handler_test.clj")]]
               (include-features features))))))
