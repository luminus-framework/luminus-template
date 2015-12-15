(ns leiningen.new.sassc
  (:require [leiningen.new.common :refer :all]))

(def sassc-assets
  [["resources/scss/screen.scss" "sassc/resources/scss/screen.scss"]])

(def sassc-config
  {:sassc
   {:src         "resources/scss/screen.scss"               ;; default "src/scss/main.scss"
    :output-to   "resources/public/css/screen.css"          ;; default "target/sassc/main.css"
    :style       "nested"                                   ;; "nested" or "compressed", default "nested"
    :import-path "resources/scss"}})                        ;; default "src/scss"

(def lein-sassy-config
  {:sass
   {:src "resources/sass/stylesheets"
    :dst "resources/public/css"}})

(def sass-plugins
  [['lein-sassy "1.0.7"]
   ['lein-sassc "0.10.4"]])

(defn sassc-features [[assets options :as state]]
  (if (some #{"+sassc"} (:features options))
    [(into assets sassc-assets)
     (-> options
         (append-options :plugins sass-plugins)
         (assoc :sassc-docs ((:selmer-renderer options)
                              (slurp-resource "sassc/docs/sassc_instructions.md")
                              options))
         (assoc :sassc-config-params (unwrap-map (indent root-indent sassc-config)))
         (assoc :lein-sassy-params (unwrap-map (indent root-indent lein-sassy-config))))]
    state))
