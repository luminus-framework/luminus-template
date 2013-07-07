(ns leiningen.new.dependency-injector
  (:use clojure.pprint)
  (:require [clojure.java.io :as io]))

(defn- read-file [filename]
  (with-open [r (java.io.PushbackReader.
                  (io/reader filename))]
    (binding [*read-eval* false]
      (loop [exprs []]
        (if-let [expr (read r nil nil)]
          (recur (conj exprs expr))
          exprs)))))

(defn- to-project [f name version m]
  (->> m
    (reduce
      (fn [params param] (into params param))
      [f name version])
    (seq)))

(defn- pprint-code [code]
  (.replaceAll (with-out-str (with-pprint-dispatch code-dispatch (pprint code))) "\\\\n" "\n"))

(defn- write-project [filename f name version m]
  (spit filename (pprint-code (to-project f name version m))))

(defn- update-item-list
  "filename is path to project.clj
   type is the key to update
   items are the items which will be appended to the value at the key
   the value being updated is expected to be a collection"
  [filename type update-fn]
  (let [[projectdef name version & more] (first (read-file filename))
        project-map (apply hash-map more)]
    (write-project filename projectdef name version
                   (update-in project-map [type] update-fn))))

(defn add-to-project [filename k v]
  (let [[f name version & more] (first (read-file filename))
        project-map (apply hash-map more)]
    (write-project filename f name version
                   (assoc project-map k v))))

(defn remove-dependencies [filename & dependencies]
  (update-item-list filename :dependencies 
                    #(if % 
                       (reduce
                         (fn [deps dep]
                           (if (some #{dep} dependencies)
                             deps (conj deps dep)))
                         []
                         %))))

(defn add-dependencies [filename & dependencies]
  (update-item-list filename :dependencies 
                    #(if % (into % dependencies) (vec dependencies))))

(defn add-plugins [filename & plugins]
  (update-item-list filename :plugins 
                    #(if % (into % plugins) (vec plugins))))

(defn add-to-ns [filename handler-fn & libs]
  (let [file (read-file filename)]
    (with-open [wrt (io/writer filename)]
      (doseq [expr file]
        (.write wrt
          (pprint-code (handler-fn expr)))
        (.write wrt "\n")))))

(defn add-required [filename & requires]
  (add-to-ns
    filename
    (fn [expr]
      (if (= 'ns (first expr))
        (clojure.walk/prewalk
          (fn [x]
            (if (and (coll? x) (= (first x) :require))
              (concat x requires) x))
          expr)
        expr))))

(defn add-routes [filename & routes]
  (add-to-ns
    filename
    (fn [expr]      
      (if (and (= 'def (first expr)) (= 'app (second expr)))
        (clojure.walk/prewalk
          (fn [x]            
            (if (and (vector? x) (some #(= % 'app-routes) x))
              (into (vec routes) x) x))
          expr)
        expr))))

(defn append-exps [filename & exps]
  (let [file (slurp filename)]
    (with-open [wrt (io/writer filename)]
      (.write wrt file)
      (doseq [exp exps]
        (.write wrt "\n")
        (.write wrt (pprint-code exp)))
      (.write wrt "\n"))))

(defn set-lein-version [filename version]
  (spit filename
        (let [project-str (.trim (slurp filename))
              length      (dec (count project-str))]
          (str (.substring project-str 0 length)
               "\n  :min-lein-version \"" version "\")"))))
