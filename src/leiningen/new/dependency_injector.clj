(ns leiningen.new.dependency-injector
  (:use clojure.pprint))

(defn read-file [filename]
  (with-open [r (java.io.PushbackReader. 
                  (clojure.java.io/reader filename))]
    (binding [*read-eval* false]
      (read r))))

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
        project-map (apply hash-map more)
        updated (update-in project-map [:dependencies] into dependencies)]    
    (write-project filename f name version updated)))