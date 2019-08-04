(ns test-templates
  (:require [clojure.test :refer :all]
            [clojure.java.shell :only [sh] :as shell])
  (:import [java.nio.file Files]
           [org.apache.commons.io FileUtils]))

(defmacro with-temp-dir [pathname-var & body]
  "Creates a temporary directory, then executes body, binding pathname-var as the pathname to the temporary directory.
  Note that the temporary directory is recursively deleted after body is executed."
  `(let [temp-dir# (Files/createTempDirectory "luminus-template" (into-array java.nio.file.attribute.FileAttribute []))
         temp-dir-file# (.toFile temp-dir#)
         ~pathname-var (.toString temp-dir#)]
     (try
       ~@body
       (finally (FileUtils/deleteDirectory temp-dir-file#)))))

(defmacro with-persistent-temp-dir [pathname-var & body]
  "Creates a temporary directory, then executes body, binding pathname-var as the pathname to the temporary directory.
  Note that the temporary directory is not deleted after body is executed."
  `(let [temp-dir# (Files/createTempDirectory "luminus-template" (into-array java.nio.file.attribute.FileAttribute []))
         temp-dir-file# (.toFile temp-dir#)
         ~pathname-var (.toString temp-dir#)]
     ~@body))

(defn sh-logging-err [& args]
  "Runs a shell command, returning exit code.
  If the exit code is 0, succeed silently.  If the exit code is non-zero, write :out to STDOUT, and :err to STDERR."
  (let [result (apply shell/sh args)
        exit-code (:exit result)]
    (if (not (= 0 exit-code))
      (do
        (println (:out result))
        (binding [*out* *err*]
          (println (:err result)))))
    exit-code))

(deftest smoke-test-templates
  "Smoke-tests each individual template option.
  For each supported template option, generates a template with that option, lints it, and compiles it."
  (doseq [template-option
          [;; Doesn't compile: Couldn't find project.clj, which is needed for compile
           ;; "+boot"

           ;; Doesn't compile: Couldn't find project.clj, which is needed for compile
           ;; "+diatomic"

           ;; Doesn't parse: {:type :reader-exception, :ex-kind :reader-error}
           ;; "+h2"

           ;; Doesn't parse: {:type :reader-exception, :ex-kind :reader-error}
           ;; "+mysql"

           ;; Doesn't parse: {:type :reader-exception, :ex-kind :reader-error}
           ;; "+postgres"

           ;; Doesn't parse: {:type :reader-exception, :ex-kind :reader-error}
           ;; "+sqlite"

           "+aleph"
           "+auth"
           "+auth-jwe"
           "+cljs"
            "+cucumber"
           "+graphql"
           "+hoplon"
           "+http-kit"
           "+immutant"
           "+jetty"
           "+kee-frame"
           "+kibit"
           "+mongodb"
           "+oauth"
           "+reagent"
           "+re-frame"
           "+reitit"
           "+sassc"
           "+service"
           "+servlet"
           "+shadow-cljs"
           "+site"
           "+swagger"
           "+war"]]
    (with-temp-dir temp-pathname
      (is (= 0 (sh-logging-err "lein" "new" "luminus" "test-project" ":to-dir" temp-pathname ":force" "t" template-option)) (str "Generate Luminus project with template option " template-option))
      (is (= 0 (sh-logging-err "lein" "compile" :dir temp-pathname)) (str "Compile Luminus project created with template option " template-option))
      (is (= 0 (sh-logging-err "lein" "eastwood" :dir temp-pathname)) (str "Lint Luminus project created with template option " template-option)))))
