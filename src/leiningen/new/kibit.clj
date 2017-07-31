(ns leiningen.new.kibit
  (:require [leiningen.new.common :refer :all]))

(defn kibit-features [[assets options :as state]]
  (if (some #{"+kibit"} (:features options))
    (let [boot? (some #{"+boot"} (:features options))
          plugin-key (if boot?
                       :dependencies
                       :plugins)]
    (if boot?
      (println "Warning: kibit doesn't have native boot support and must be "
               "called manually on the commandline."))
    [assets
     (-> options
         (append-options plugin-key (if boot?
                                      [['lein-kibit "0.1.2" :scope "test"]]
                                      [['lein-kibit "0.1.2"]])))])
    state))
