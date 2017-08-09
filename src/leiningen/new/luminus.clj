(ns leiningen.new.luminus
  (:require [leiningen.new.templates
             :refer [name-to-path year
                     sanitize sanitize-ns project-name]]
            [clojure.java.io :as io]
            [leiningen.core.main :refer [leiningen-version]]
            [leiningen.core.main :as main]
            [leiningen.new.common :refer :all]
            [leiningen.new.lein :refer [lein-features]]
            [leiningen.new.boot :refer [boot-features]]
            [leiningen.new.auth :refer [auth-features]]
            [leiningen.new.auth-base :refer [auth-base-features]]
            [leiningen.new.auth-jwe :refer [auth-jwe-features]]
            [leiningen.new.cider :refer [cider-features]]
            [leiningen.new.db :refer [db-features]]
            [leiningen.new.cljs :refer [cljs-features]]
            [leiningen.new.hoplon :refer [hoplon-features]]
            [leiningen.new.reagent :refer [reagent-features]]
            [leiningen.new.re-frame :refer [re-frame-features]]
            [leiningen.new.cucumber :refer [cucumber-features]]
            [leiningen.new.aleph :refer [aleph-features]]
            [leiningen.new.jetty :refer [jetty-features]]
            [leiningen.new.http-kit :refer [http-kit-features]]
            [leiningen.new.immutant :refer [immutant-features]]
            [leiningen.new.swagger :refer [swagger-features]]
            [leiningen.new.sassc :refer [sassc-features]]
            [leiningen.new.site :refer [site-features]]
            [leiningen.new.war :refer [war-features]]
            [leiningen.new.kibit :refer [kibit-features]]
            [leiningen.new.logback :refer [logback-features]]
            [leiningen.new.service :refer [service-features]]
            [leiningen.new.oauth :refer [oauth-features]]))

(defn resource [r]
  (->> r (str "leiningen/new/luminus/core/resources/") (io/resource)))

(def core-assets
  [[".gitignore" "core/gitignore"]
   ["profiles.clj" "core/profiles.clj"]
   ["Procfile" "core/Procfile"]
   ["Dockerfile" "core/Dockerfile"]
   ["Capstanfile" "core/Capstanfile"]
   ["README.md" "core/README.md"]
   ["env/prod/resources/config.edn" "core/env/prod/resources/config.edn"]
   ["env/dev/resources/config.edn" "core/env/dev/resources/config.edn"]
   ["env/test/resources/config.edn" "core/env/dev/resources/config.edn"]


   ;; config namespaces
   ["env/dev/clj/{{sanitized}}/env.clj" "core/env/dev/clj/env.clj"]
   ["env/dev/clj/{{sanitized}}/dev_middleware.clj" "core/env/dev/clj/dev_middleware.clj"]
   ["env/prod/clj/{{sanitized}}/env.clj" "core/env/prod/clj/env.clj"]

   ;; core namespaces
   ["env/dev/clj/user.clj" "core/env/dev/clj/user.clj"]
   ["src/clj/{{sanitized}}/core.clj" "core/src/core.clj"]
   ["src/clj/{{sanitized}}/config.clj" "core/src/config.clj"]
   ["src/clj/{{sanitized}}/handler.clj" "core/src/handler.clj"]
   ["src/clj/{{sanitized}}/routes/home.clj" "core/src/home.clj"]
   ["src/clj/{{sanitized}}/layout.clj" "core/src/layout.clj"]
   ["src/clj/{{sanitized}}/middleware.clj" "core/src/middleware.clj"]

   ;;HTML templates
   ["resources/templates/base.html" "core/resources/templates/base.html"]
   ["resources/templates/home.html" "core/resources/templates/home.html"]
   ["resources/templates/about.html" "core/resources/templates/about.html"]
   ["resources/templates/error.html" "core/resources/templates/error.html"]

   ;; public resources, example URL: /css/screen.css
   ["resources/public/css/screen.css" "core/resources/css/screen.css"]
   ["resources/docs/docs.md" "core/resources/docs.md"]
   "resources/public/js"

   ;; tests
   ["test/clj/{{sanitized}}/test/handler.clj" "core/test/handler.clj"]])

(def binary-assets
  [["resources/public/favicon.ico" "core/resources/favicon.ico"]
   ["resources/public/img/warning_clojure.png" "core/resources/img/warning_clojure.png"]])

(defn sort-deps [deps]
  (sort-by (fn [dep] (str dep)) deps))

(defn format-options [{:keys [http-server-dependencies features] :as options}]
  (let [boot? (some #{"+boot"} features)
        dev-indent (if-not boot?
                     dev-dependency-indent
                     boot-dev-dependency-indent)]
    (-> options
        (dissoc :http-server-dependencies)
        (update-in [:dependencies] #(->> %
                                         (into http-server-dependencies)
                                         (sort-deps)
                                         (indent dependency-indent)))
        (update-in [:http-server-dependencies] (partial indent dependency-indent))
        (update-in [:dev-http-server-dependencies] (partial indent dev-indent))
        (update-in [:dev-dependencies] #(->> % (sort-deps) (indent dev-indent)))
        (update-in [:plugins] (partial indent plugin-indent))
        (update-in [:dev-plugins] (partial indent dev-dependency-indent)))))

(def core-dependencies
  [['org.clojure/clojure "1.8.0"]
   ['selmer "1.11.0"]
   ['clj-time "0.14.0"]
   ['markdown-clj "0.9.99"]
   ['metosin/muuntaja "0.3.2"]
   ['metosin/ring-http-response "0.9.0"]
   ['funcool/struct "1.0.0"]
   ['org.webjars/bootstrap "4.0.0-alpha.5"]
   ['org.webjars/font-awesome "4.7.0"]
   ['org.webjars.bower/tether "1.4.0"]
   ['org.webjars/jquery "3.2.1"]
   ['org.clojure/tools.logging "0.4.0"]
   ['compojure "1.6.0"]
   ['ring/ring-core "1.6.2"]
   ['ring-webjars "0.2.0"]
   ['ring/ring-defaults "0.3.1"]
   ['luminus/ring-ttl-session "0.3.2"]
   ['mount "0.1.11"]
   ['cprop "0.1.11"]
   ['org.clojure/tools.cli "0.3.5"]
   ['luminus-nrepl "0.1.4"]])

(defn generate-project
  "Create a new Luminus project"
  [options]
  (main/info "Generating a Luminus project.")
  (let [[assets options]
        (-> [core-assets options]
            lein-features
            boot-features
            service-features
            auth-base-features
            auth-features
            auth-jwe-features
            cider-features
            db-features
            cucumber-features
            site-features
            cljs-features
            hoplon-features
            reagent-features
            re-frame-features
            swagger-features
            aleph-features
            jetty-features
            http-kit-features
            immutant-features
            sassc-features
            kibit-features
            logback-features
            oauth-features
            war-features)]
    (render-assets assets binary-assets (format-options options))))

(defn format-features [features]
  (apply str (interpose ", " features)))

(defn set-feature [options feature features]
  (if (empty?
        (clojure.set/intersection
          (-> options :features set)
          features))
    (update-in options [:features] conj feature)
    options))

(defn set-default-features [options]
  (-> options
      (set-feature "+immutant" #{"+jetty" "+aleph" "+http-kit"})
      (set-feature "+logback" #{})
      (set-feature "+lein" #{"+boot"})))

(defn set-feature-dependency [options feature dependencies]
  (let [features (-> options :features set)]
    (if (features feature)
      (update-in options [:features] into dependencies)
      options)))

(defn set-dependent-features [options]
  (-> options
      (set-feature-dependency "+auth" #{"+auth-base"})
      (set-feature-dependency "+auth-jwe" #{"+auth-base"})
      (set-feature-dependency "+hoplon" #{"+cljs"})
      (set-feature-dependency "+reagent" #{"+cljs"})
      (set-feature-dependency "+re-frame" #{"+cljs" "+reagent"})))

(defn parse-version [v]
  (map #(Integer/parseInt %)
       (clojure.string/split v #"\.")))

(defn version-before? [v]
  (let [[x1 y1 z1] (parse-version (leiningen-version))
        [x2 y2 z2] (parse-version v)]
    (or
      (< x1 x2)
      (and (= x1 x2) (< y1 y2))
      (and (= x1 x2) (= y1 y2) (< z1 z2)))))

(defn luminus
  "Create a new Luminus project"
  [name & feature-params]
  (let [min-version "2.5.2"
        supported-features #{;;databases
                             "+sqlite" "+h2" "+postgres" "+mysql" "+mongodb" "+datomic"
                             ;;servers
                             "+aleph" "+jetty" "+http-kit"
                             ;;misc
                             "+cljs" "+hoplon" "+reagent" "+re-frame" "+auth" "+auth-jwe" "+site"
                             "+cucumber" "+sassc" "+cider" "+oauth"
                             "+swagger" "+war"
                             "+kibit" "+service"
                             "+boot"}
        options {:name              (project-name name)
                 :dependencies      core-dependencies
                 :selmer-renderer   render-template
                 :min-lein-version  "2.0.0"
                 :project-ns        (sanitize-ns name)
                 :sanitized         (name-to-path name)
                 :year              (year)
                 :features          (set feature-params)
                 :source-paths      ["src/clj"]
                 :resource-paths    ["resources"]}
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
      (-> options set-default-features set-dependent-features generate-project))))
