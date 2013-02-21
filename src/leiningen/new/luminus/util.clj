(ns {{name}}.util
  (:require [noir.io :as io]
            [markdown.core :as md]))

(defn safe-escape
  "Partial html character conversion for safety.
  -> &amp; &quot; &lt; &gt;"
  [^java.lang.String html]
  (let [r [["\"" "&quot;"]
           ["<" "&lt;"]
           [">" "&gt;"]
           ["&" "&amp;"]]]
    (loop [c (count r)
           html html]
      (if-not (= c 0)
        (let [[x y] (nth r (dec c))]
          (recur
           (dec c)
           (-> html (.replace x y))))
        html))))

(defn required-number
  "Returns 0 instead of nil if x is not a number."
  [x]
  (if-not (number? x) 0 x))

(defn required-string
  "Returns blank (not nil) string if x is not a string
   if convert? apply str to x"
  ([x] (required-string x false))
  ([x ^java.lang.Boolean convert?]
  (if convert?
    (.toString x)
    (if-not (string? x) " " x))))
  
(defmulti str-limit
  (fn [x y]))

(defmethod str-limit
  java.lang.String
  [x ^java.lang.Long max]
  (when-not (empty? x)
    (if (> (count x)  
           max)
      (subs x 0 max)
      x)))

(defmethod str-limit
  java.lang.Long
  [max ^java.lang.String x]
  (when-not (empty? x)
    (if (> (count x)  
           max)
      (subs x 0 max)
      x)))

(defn format-time
  "formats the time using SimpleDateFormat, the default format is
   \"dd MMM, yyyy\" and a custom one can be passed in as the second argument"
  ([time] (format-time time "dd MMM, yyyy"))
  ([time fmt]
    (.format (new java.text.SimpleDateFormat fmt) time)))

(defn md->html
  "reads a markdown file from public/md and returns an HTML string"
  [filename]
  (->>
    (io/slurp-resource filename)
    (md/md-to-html-string)))
