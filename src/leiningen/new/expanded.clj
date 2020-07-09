(ns leiningen.new.expanded
  (:require [leiningen.new.common :refer :all]))

(def expanded-dependencies
  [['markdown-clj "1.10.5"]
   ['funcool/struct "1.4.0"]
   ['org.webjars.npm/bulma "0.9.0"]
   ['org.webjars.npm/material-icons "0.3.1"]
   ['ring-webjars "0.2.0"]
   ['org.webjars/webjars-locator "0.40"]])

(def expanded-assets
  [["{{resource-path}}/public/css/screen.css" "expanded/resources/css/screen.css"]
   ["{{resource-path}}/html/base.html" "expanded/resources/html/base.html"]
   ["{{resource-path}}/html/about.html" "expanded/resources/html/about.html"]
   ["{{resource-path}}/html/home.html" "expanded/resources/html/home.html"]
   ["{{resource-path}}/html/error.html" "expanded/resources/html/error.html"]
   ["{{resource-path}}/docs/docs.md" "expanded/resources/docs.md"]])

(defn expanded-features [[assets options :as state]]
  (if (some #{"+expanded"} (:features options))
    [(into (remove-conflicting-assets assets "screen.css" "base.html" "home.html" "error.html" "docs.md") expanded-assets)
     (-> options
         (assoc :expanded true)
         (append-options :dependencies expanded-dependencies))]
    state))
