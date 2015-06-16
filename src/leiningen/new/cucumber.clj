(ns leiningen.new.cucumber
  (:require [leiningen.new.common :refer :all]))

(def cucumber-assets
  [["test/<<sanitized>>/browser.clj" "cucumber/src/browser.clj"]
   ["test/features/step_definitions/home_page_steps.clj" "cucumber/src/home_page_steps.clj"]
   ["resources/log4j.properties" "cucumber/resources/log4j.properties"]
   ["test/features/index_page.feature" "cucumber/resources/index_page.feature"]])

(defn cucumber-features [[assets options :as state]]
  (if (some #{"+cucumber"} (:features options))
    [(into assets cucumber-assets)
     (-> options
         (append-options :plugins [['org.clojars.punkisdead/lein-cucumber "1.0.4"]])
         (append-options :dev-dependencies [['org.clojure/core.cache "0.6.3"]
                                            ['org.apache.httpcomponents/httpcore "4.4"]
                                            ['clj-webdriver/clj-webdriver "0.6.1"]])
         (assoc :cucumber-feature-paths (pprint-code ["test/features"])))]
    state))
