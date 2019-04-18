(ns leiningen.new.lein
  (:require [leiningen.new.common :refer :all]))

(defn lein-features [[assets options :as state]]
  (if (some #{"+lein"} (:features options))
    [(conj assets ["project.clj" "core/project.clj"])
     options]
    state))

