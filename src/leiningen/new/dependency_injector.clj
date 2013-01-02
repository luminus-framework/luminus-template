(ns leiningen.new.dependency-injector
  (:use clojure.pprint))

(defn- read-file [filename]
  (with-open [r (java.io.PushbackReader. 
                  (clojure.java.io/reader filename))]
    (binding [*read-eval* false]
      (read r))))

(defn- to-project [f name version m]
  (->> m 
    (reduce 
      (fn [params param] (into params param)) 
      [f name version])
    (seq)))

(defn- write-project [filename f name version m]
  (binding [*print-right-margin* 80]
    (let [wrt (new java.io.StringWriter)]
      (clojure.pprint/pprint (to-project f name version m) wrt)
      (spit filename (.toString wrt)))))

(defn- update-item-list
  "filename is path to project.clj
   type is the key to update
   items are the items which will be appended to the value at the key
   the value being updated is expected to be a collection"
  [filename type items]
  (let [[projectdef name version & more] (read-file filename)
        project-map (apply hash-map more)]    
    (write-project filename projectdef name version 
                   (update-in project-map [type] 
                              #(if % (into % items) (vec items))))))

(defn add-to-project [filename k v]
  (let [[f name version & more] (read-file filename)
        project-map (apply hash-map more)]    
    (write-project filename f name version 
                   (assoc project-map k v))))

(defn add-dependencies [filename & dependencies]
  (update-item-list filename :dependencies dependencies))

(defn add-plugins [filename & plugins]
  (update-item-list filename :plugins plugins))