(ns leiningen.new.common
  (:require
    [leiningen.new.templates :refer [renderer ->files]]
    [clojure.pprint :refer [code-dispatch pprint with-pprint-dispatch]]))

(def dependency-indent 17)
(def dev-dependency-indent 33)
(def plugin-indent 12)
(def root-indent 2)
(def dev-indent 18)
(def uberjar-indent 13)
(def require-indent 13)

(def render (renderer "luminus"))

(defn slurp-resource [path]
  (-> (str "leiningen/new/luminus/" path)
      clojure.java.io/resource
      slurp))

(defn render-asset [options asset]
  (if (string? asset)
    asset
    (let [[target source] asset]
      [target (render source options)])))

(defn render-assets [assets options]
  (apply ->files options (map (partial render-asset options) assets)))

(defn pprint-code [code]
  (-> (pprint code)
      with-out-str
      (.replaceAll "," "")))

(defn form->str [form]
  (let [text (pprint-code form)]
    (subs text 0 (count text))))

(defn indented-code [n form]
  (let [text (form->str form)
        indents (apply str (repeat n " "))]
    (.replaceAll (str text) "\n" (str "\n" indents))))

(defn indent [n form]
  (let [indents (apply str (repeat n " "))]
    (if (map? form)
      (indented-code n form)
      (->> form
           (map str)
           (clojure.string/join (str "\n" indents))))))

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
                (.endsWith (second %) filter-str))
          assets))

(defn unsupported-jetty-java-version? [java-version]
  (as-> java-version %
        (clojure.string/split % #"\.")
        (take 2 %)
        (map #(Integer/parseInt %) %)
        (and (< (first %) 2)
             (< (second %) 8))))