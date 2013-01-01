(ns leiningen.new.dependency-injector
  (:use clojure.pprint))

(defn read-file [filename]
  (with-open [r (java.io.PushbackReader. 
                  (clojure.java.io/reader filename))]
    (binding [*read-eval* false]
      (read r))))

(defn project-to-map [project]
  (->> project    
    (partition 2)
    (map vec)
    (into {})))

(defn to-project [f name version m]
  (->> m 
    (reduce 
      (fn [params param] (into params param)) 
      [f name version])
    (seq)))

(defn write-project [filename f name version m]
  (let [wrt (new java.io.StringWriter)]
    (clojure.pprint/pprint (to-project f name version m) wrt)
    (spit filename (.toString wrt))))

(defn add-dependencies [filename  & dependencies]
  (let [[f name version & more] (read-file filename)
        project-map (project-to-map more)
        updated (update-in project-map [:dependencies] into dependencies)]    
    (write-project filename f name version updated)))