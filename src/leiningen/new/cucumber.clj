(ns leiningen.new.cucumber
  (:require [leiningen.new.common :refer :all]))

(def cucumber-assets
  [["test/clj/{{sanitized}}/browser.clj" "cucumber/src/browser.clj"]
   ["test/clj/features/step_definitions/home_page_steps.clj" "cucumber/src/home_page_steps.clj"]
   ["test/clj/features/index_page.feature" "cucumber/resources/index_page.feature"]])

(defn cucumber-features [[assets options :as state]]
  (if (some #{"+cucumber"} (:features options))
    (do
      (if (some #{"+boot"} (:features options))
        (throw
         (IllegalArgumentException. "+cucumber and +boot may not be used together.")))
      [(into assets cucumber-assets)
       (-> options
           (append-options :plugins [['org.clojars.punkisdead/lein-cucumber "1.0.5"]])
           (append-options :dev-dependencies [['org.clojure/core.cache "0.6.3"]
                                              ['org.apache.httpcomponents/httpcore "4.4"]
                                              ['clj-webdriver/clj-webdriver "0.7.2"]
                                              (if (some #{"+auth" "+auth-jwe"} (:features options))
                                                ['org.seleniumhq.selenium/selenium-server "2.48.2"
                                                 :exclusions ['org.bouncycastle/bcprov-jdk15on
                                                              'org.bouncycastle/bcpkix-jdk15on]]
                                                ['org.seleniumhq.selenium/selenium-server "2.48.2"])])
           (assoc :cucumber-feature-paths (pprint-code ["test/clj/features"])))])
    state))
