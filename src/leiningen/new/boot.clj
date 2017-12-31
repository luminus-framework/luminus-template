(ns leiningen.new.boot
  (:require [leiningen.new.common :refer :all]))

(def boot-dependencies '[[adzerk/boot-test "1.2.0" :scope "test"]
                         [luminus/boot-cprop "1.0.0" :scope "test"]])

(defn boot-features [[assets options :as state]]
  (if (some #{"+boot"} (:features options))
    [(conj assets ["build.boot" "core/build.boot"])
     (let [new-opts (-> options
                        (assoc :boot true)
                        (update :source-paths set)
                        (update :resource-paths set)
                        (append-options :dependencies boot-dependencies))]
       (if (and (some #{"+war"} (:features options))
                (some #{"+immutant"} (:features options)))
         ;; There is a conflict with dependencies with ring webjars when
         ;; org.webjars/webjars-locator-jboss-vfs is added, but only under
         ;; boot. This removes the conflict
         (let [web-jar-dep (first (filter #(= (first %) 'ring-webjars)
                                          (:dependencies options)))
               new-web-jar-dep (conj web-jar-dep :exclusions
                                     ['org.webjars/webjars-locator-core])]
           (assoc new-opts :dependencies
                  (replace {web-jar-dep new-web-jar-dep}
                           (:dependencies options))))
         new-opts))]
    state))

