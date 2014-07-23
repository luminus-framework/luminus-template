(ns {{name}}.static
  (:import org.apache.commons.io.FileUtils
           [java.io File BufferedReader FileReader FileWriter])
  (:require [{{name}}.routes.home :refer :all]
            [ring.mock.request :refer :all]
            [{{name}}.static-site :refer [site-definition]]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(defn- with-context
  "Specify what the ctx should be for relative urls"
  [ctx req]
  (assoc-in req [:servlet-context] ctx))

(defn- copy-resource-dir
  "For example, (copy-resource-dir \"doc\" \"css\" will copy
  resources/public/css to doc/css. Don't put a slash on end of
  dest-path"
  [dest-path resource-dir]
  (let [src-dir "resources/public/"]
    (FileUtils/copyDirectory
     (File. (str src-dir resource-dir))
     (File. (str dest-path "/" resource-dir)))
    (println "Copied " (str src-dir resource-dir) " to " (str dest-path "/" resource-dir))))

(defn- new-tmp-file [& [name]]
  (let [name (or name "new")]
    (doto (File/createTempFile name "luminus")
      (.deleteOnExit))))

(defn- copy-to-tmp-file
  "Create a temporary version of src-file that will get removed when
  process exists"
  [^File src-file]
  (let [tmp-file (new-tmp-file (.getName src-file))]
    (FileUtils/copyFile src-file tmp-file)
    tmp-file))

(defn- fix-line
  "Update urls, or do other tranformations and then return line of css"
  [servlet-context line]
  (if-let [matches (re-seq #".*url\(\"(.*)\"\)" line)]
    (let [new-url (str servlet-context (second (first matches)))]
      (println "  fixed " line)
      (println "  updated url to '" new-url "'")
      (clojure.string/replace line #"url\(\".*\"\)" (str "url(\"" new-url  "\")")))
    ;; no matches found, just return line
    line))

(defn- fix-css-file! [servlet-context ^File css-file]
  (let [tmp-src (copy-to-tmp-file css-file)
        tmp-dest (new-tmp-file)]
    (with-open [rdr (BufferedReader. (FileReader. tmp-src))
                wtr (FileWriter. tmp-dest)]
      (reduce
       (fn [{:keys [line-num errors successes]} line]
         (.write wtr (fix-line servlet-context line))
         {:line-num (inc line-num) :errors 0 :successes (inc successes)})
       {:line-num 0 :errors 0 :successes 0}
       (line-seq rdr)))
    tmp-dest))

(defn- fix-css!
       "Traverse all css files inside css-files-path and fix relative urls"
       [servlet-context css-files-path]
       (let [css-files
             (->> (File. css-files-path)
                  (file-seq)
                  (filter #(and
                            (re-find #"^.*\.css$" (.getName %))
                            (get-in site-definition [:css :exclude])
                            (not (re-find (get-in site-definition [:css :exclude]) (.getName %))))))]
            (doseq [css-file css-files]
                   (let [updated (fix-css-file! servlet-context css-file)]
                        (FileUtils/copyFile updated css-file)
                        (println "Fixed urls inside " (.getName css-file))))))

(defn- copy-resources [dest-path]
  (copy-resource-dir dest-path "css")
  (copy-resource-dir dest-path "img")
  (copy-resource-dir dest-path "js"))

(defn- save-request-to-file
  "Create html file by simulating a request to a route. ctx is the
  prefix to add to relative links. dir-path is where to create the
  file."
  [ctx dir-path file-name method req-path]
  ;; (println "Using ctx " ctx)
  ;; ensure directory exists
  (.mkdir (java.io.File. dir-path))
  ;; write the file
  (spit (str dir-path "/" file-name)
        (:body (home-routes (with-context ctx (request method req-path)))))
  (println "Saved " req-path " to " (str dir-path "/" file-name)))

(def default-site-path "site")
(def default-servlet-context "")

(defn generate-static-site
  "Generate static html files for each item defined in {{name}}.static-site/site-definition"
  [& {:keys [servlet-context dest-path]
      :or {servlet-context "" dest-path "site"}}]
  (copy-resources dest-path)
  (fix-css! servlet-context (str dest-path "/css"))
  (doseq [request (:routes site-definition)]
    (apply save-request-to-file (into [servlet-context dest-path] request))))

(def cli-options
  [["-h" "--help" "Display this help message"]
   ["-c" "--servlet-context SERVLET_CONTEXT" "Relative Servlet Context Path"
    :default ""]
   ["-d" "--dest-path DESTINATION_PATH" "Directory to create the site"
    :default "site"]])

(defn -main
  "Generate static version of this site."
  [& args]
  (let [{:keys [options summary]} (parse-opts args cli-options)
        {:keys [dest-path servlet-context help]} options]
    (if help (println summary)
        (generate-static-site :dest-path dest-path :servlet-context servlet-context))))
