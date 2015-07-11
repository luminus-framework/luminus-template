(ns leiningen.new.jetty
  (:require [leiningen.new.common :refer :all]
            [leiningen.core.main :as main]))

(defn jetty-features [[assets options :as state]]
  (if (some #{"+jetty"} (:features options))
    (do
      (let [java-version (System/getProperty "java.version")]
        (when (and (= "jetty" (:server options))
                   (unsupported-jetty-java-version? java-version))
          (main/info (str "Warning: Jetty requires JDK 8+, found: " java-version))))
      [assets
       (-> options
           (assoc :server "jetty")
           (append-options :dependencies [['cc.qbits/jet "0.6.6"]]))])
    state))
