(defproject luminus/lein-template "4.09"
  :description "a template for creating Luminus applications"
  :url "https://github.com/yogthos/luminus-template"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :min-lein-version "2.5.3"
  :eval-in-leiningen true
  :dependencies [[leinjacker "0.4.3"]
                 [selmer "1.12.31"]]
  :plugins [[lein-cljfmt "0.6.4"]
            [commons-io/commons-io "2.6"]])

