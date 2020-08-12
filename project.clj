(defproject luminus/lein-template "3.84"
  :description "a template for creating Luminus applications"
  :url "https://github.com/yogthos/luminus-template"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :min-lein-version "2.5.3"
  :eval-in-leiningen true
  :dependencies [[leinjacker "0.4.2"]
                 [selmer "1.11.7"]
                 [cljfmt "0.6.8"]
                 [lein-cljfmt "0.6.8"]]
  :plugins [[lein-cljfmt "0.6.8"]
            [commons-io/commons-io "2.6"]])

