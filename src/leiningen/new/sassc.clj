(ns leiningen.new.sassc
  (:require [leiningen.new.common :refer :all]))

;;On boot this can be run with the "sass" task.

(def sassc-assets
  [["resources/scss/screen.scss" "sassc/resources/scss/screen.scss"]])

(def sassc-config
  {:sassc
   [{:src         "resources/scss/screen.scss"
     :output-to   "resources/public/css/screen.css"
     :style       "nested"
     :import-path "resources/scss"}]})

(def sassc-auto-config
  {:auto {"sassc" {:file-pattern  #"\.(scss|sass)$"
                   :paths ["resources/scss"]}}})

(def sass-plugins
  [['lein-sassc "0.10.4"]
  ['lein-auto "0.1.2"]])

(def boot-sass-plugins
  '[[deraen/boot-sass "0.3.1" :scope "test"]])

(defn sassc-features [[assets options :as state]]
  (if (some #{"+sassc"} (:features options))
    (let [boot? (some #{"+boot"} (:features options))
          plugin-key (if boot? :dependencies :plugins)]
      [(into assets sassc-assets)
       (-> options
           (append-options plugin-key (if boot?
                                        boot-sass-plugins
                                        sass-plugins))
           (assoc :sassc true)
           (assoc :sassc-docs ((:selmer-renderer options)
                               (slurp-resource "sassc/docs/sassc_instructions.md")
                               options))
           (assoc :sassc-config-params (unwrap-map (indent root-indent sassc-config)))
           (assoc :sassc-auto-config (unwrap-map (indent root-indent sassc-auto-config))))])
    state))
