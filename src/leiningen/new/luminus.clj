(ns leiningen.new.luminus
  (:require [leiningen.new.templates
             :refer [name-to-path year
                     sanitize sanitize-ns project-name]]
            [clojure.java.io :as io]
            [leiningen.core.main :refer [leiningen-version]]
            [leiningen.core.main :as main]
            [leiningen.new.common :refer :all]
            [leiningen.new.auth :refer [auth-features]]
            [leiningen.new.db :refer [db-features]]
            [leiningen.new.cljs :refer [cljs-features]]
            [leiningen.new.cucumber :refer [cucumber-features]]
            [leiningen.new.aleph :refer [aleph-features]]
            [leiningen.new.jetty :refer [jetty-features]]
            [leiningen.new.http-kit :refer [http-kit-features]]
            [leiningen.new.immutant :refer [immutant-features]]
            [leiningen.new.swagger :refer [swagger-features]]
            [leiningen.new.sassc :refer [sassc-features]]
            [leiningen.new.site :refer [site-features]]
            [leiningen.new.war :refer [war-features]]
            [leiningen.new.kibit :refer [kibit-features]]))

(defn resource [r]
  (->> r (str "leiningen/new/luminus/core/resources/") (io/resource)))

(def core-assets
  [[".gitignore" "core/gitignore"]
   ["project.clj" "core/project.clj"]
   ["profiles.clj" "core/profiles.clj"]
   ["Procfile" "core/Procfile"]
   ["README.md" "core/README.md"]
   ["env/dev/resources/config.edn" "core/env/dev/resources/config.edn"]
   ["env/prod/resources/config.edn" "core/env/prod/resources/config.edn"]
   ["env/dev/resources/log4j.properties" "core/env/dev/resources/log4j.properties"]
   ["env/prod/resources/log4j.properties" "core/env/prod/resources/log4j.properties"]

   ;; config namespaces
   ["env/dev/clj/{{sanitized}}/config.clj" "core/env/dev/clj/config.clj"]
   ["env/dev/clj/{{sanitized}}/dev_middleware.clj" "core/env/dev/clj/dev_middleware.clj"]
   ["env/prod/clj/{{sanitized}}/config.clj" "core/env/prod/clj/config.clj"]
   ;; core namespaces
   ["env/dev/clj/user.clj" "core/env/dev/clj/user.clj"]
   ["src/{{sanitized}}/core.clj" "core/src/core.clj"]
   ["src/{{sanitized}}/handler.clj" "core/src/handler.clj"]
   ["src/{{sanitized}}/routes/home.clj" "core/src/home.clj"]
   ["src/{{sanitized}}/layout.clj" "core/src/layout.clj"]
   ["src/{{sanitized}}/middleware.clj" "core/src/middleware.clj"]

   ;;HTML templates
   ["resources/templates/base.html" "core/resources/templates/base.html"]
   ["resources/templates/home.html" "core/resources/templates/home.html"]
   ["resources/templates/about.html" "core/resources/templates/about.html"]
   ["resources/templates/error.html" "core/resources/templates/error.html"]

   ;; public resources, example URL: /css/screen.css
   ["resources/public/favicon.ico"  (resource "site/favicon.ico")]
   ["resources/public/css/screen.css" "core/resources/css/screen.css"]
   ["resources/docs/docs.md" "core/resources/docs.md"]
   "resources/public/js"
   "resources/public/img"

   ;; tests
   ["test/{{sanitized}}/test/handler.clj" "core/test/handler.clj"]])

(defn format-options [options]
  (-> options
      (update-in [:dependencies] (partial indent dependency-indent))
      (update-in [:dev-dependencies] (partial indent dev-dependency-indent))
      (update-in [:plugins] (partial indent plugin-indent))))

(defn generate-project
  "Create a new Luminus project"
  [options]
  (main/info "Generating a Luminus project.")
  (let [[assets options]
        (-> [core-assets options]
            auth-features
            db-features
            cucumber-features
            site-features
            cljs-features
            swagger-features
            aleph-features
            jetty-features
            http-kit-features
            immutant-features
            sassc-features
            war-features
            kibit-features)]
    (render-assets (init-render) assets (format-options options))))

(defn format-features [features]
  (apply str (interpose ", " features)))

(defn set-default-features [options]
  (if (empty?
        (clojure.set/intersection
          (-> options :features set)
          #{"+jetty" "+aleph" "+http-kit"}))
    (update-in options [:features] conj "+immutant")
    options))

(defn parse-version [v]
  (map #(Integer/parseInt %)
       (clojure.string/split v #"\.")))

(defn version-before? [v]
  (let [[x1 y1 z1] (parse-version (leiningen-version))
        [x2 y2 z2] (parse-version v)]
    (or
      (< x1 x2)
      (and (= x1 x2) (< y1 y2))
      (and (and (= x1 x2) (= y1 y2) (< z1 z2))))))

(defn luminus
  "Create a new Luminus project"
  [name & feature-params]
  (let [min-version "2.5.2"
        supported-features #{;;databases
                             "+sqlite" "+h2" "+postgres" "+mysql" "+mongodb"
                             ;;servers
                             "+aleph" "+jetty" "+http-kit"
                             ;;misc
                             "+cljs" "+auth" "+site"
                             "+cucumber" "+sassc"
                             "+swagger" "+war"
                             "+kibit"}
        options {:name             (project-name name)
                 :selmer-renderer  render-template
                 :min-lein-version "2.0.0"
                 :project-ns       (sanitize-ns name)
                 :sanitized        (name-to-path name)
                 :year             (year)
                 :features         (set feature-params)
                 :resource-paths ["resources"]}
        unsupported (-> (set feature-params)
                        (clojure.set/difference supported-features)
                        (not-empty))]
    (cond
      (version-before? min-version)
      (main/info "Leiningen version" min-version "or higher is required, found " (leiningen-version)
                 "\nplease run: 'lein upgrade' to upgrade Leiningen")

      (re-matches #"\A\+.+" name)
      (main/info "Project name is missing.\nTry: lein new luminus PROJECT_NAME"
                 name (clojure.string/join " " (:features options)))

      unsupported
      (main/info "Unrecognized options:" (format-features unsupported)
                 "\nSupported options are:" (format-features supported-features))

      (.exists (io/file name))
      (main/info "Could not create project because a directory named" name "already exists!")

      :else
      (-> options set-default-features generate-project))))
