(ns leiningen.new.common
  (:require
    [selmer.parser :as selmer]
    [leiningen.new.templates :refer [renderer raw-resourcer ->files]]
    [clojure.pprint :refer [code-dispatch pprint with-pprint-dispatch]]
    [clojure.string :as string]
    [clojure.java.io :as io]))

(def dependency-indent 17)
(def dev-dependency-indent 33)
(def boot-dev-dependency-indent 30)
(def plugin-indent 12)
(def root-indent 2)
(def dev-indent 18)
(def uberjar-indent 13)
(def require-indent 13)
(def template-name "luminus")

(defn render-template [template options]
  (selmer/render
    (str "<% safe %>" template "<% endsafe %>")
    options
    {:tag-open \< :tag-close \> :filter-open \< :filter-close \>}))

(defn init-render []
  (renderer template-name render-template))

(defn slurp-resource [path]
  (-> (str "leiningen/new/luminus/" path)
      io/resource
      slurp))

(selmer/add-tag!
  :include
  (fn [args context-map]
    (-> (slurp-resource (first args))
        (render-template context-map)
        (string/replace #"^\n+" "")
        (string/replace #"\n+$" ""))))

(defn render-asset [render options asset]
  (if (string? asset)
    asset
    (let [[target source] asset]
      [target (render source options)])))

(defn render-assets [assets binary-assets options]
  (let [render (init-render)
        raw    (raw-resourcer template-name)]
    (apply ->files options
           (into
             (map (partial render-asset render options) assets)
             (map (fn [[target source]] [target (raw source)]) binary-assets)))))

(defn pprint-code [code]
  (-> (pprint code)
      with-out-str
      (.replaceAll "," "")))

(defn form->str [form]
  (let [text (pprint-code form)]
    (subs text 0 (count text))))

(defn indented-code [n form]
  (let [text    (form->str form)
        indents (apply str (repeat n " "))]
    (.replaceAll (str text) "\n" (str "\n" indents))))

(defn indent [n form]
  (when form
    (let [indents (apply str (repeat n " "))]
      (if (map? form)
        (indented-code n form)
        (->> form
             (map str)
             (clojure.string/join (str "\n" indents)))))))

(defn unwrap-map [text]
  (let [sb (StringBuilder. text)]
    (.setCharAt sb (.indexOf text "{") \space)
    (.setCharAt sb (.lastIndexOf text "}") \space)
    (.toString sb)))

(defn append-options [options k v]
  (update-in options [k] (fnil into []) v))

(defn append-formatted [options k v indent-width]
  (assoc options k (indent indent-width v)))

(defn remove-conflicting-assets [assets filter-str]
  (remove #(and (coll? %)
                (and (string? (second %))
                     (.endsWith (second %) filter-str)))
          assets))

(defn unsupported-jetty-java-version? [java-version]
  (as-> java-version %
        (clojure.string/split % #"\.")
        (take 2 %)
        (map #(Integer/parseInt %) %)
        (and (< (first %) 2)
             (< (second %) 8))))
