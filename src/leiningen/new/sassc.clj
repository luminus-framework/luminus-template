(ns leiningen.new.sassc
  (:require [leiningen.new.common :refer :all]))

(def sassc-assets
  [["resources/scss/screen.scss" "sassc/resources/scss/screen.scss"]])

(def sassc-config
  [{:src          "resources/scss/screen.scss"      ;; default "src/scss/main.scss"
    :output-to    "resources/public/css/screen.css" ;; default "target/sassc/main.css"
    :style        "nested"                          ;; "nested" or "compressed", default "nested"
    :import-path  "resources/scss"}])               ;; default "src/scss"

(defn sassc-features [[assets options :as state]]
  (if (some #{"+sassc"} (:features options))
    [(into assets sassc-assets)
     (-> options
         (append-options :plugins [['lein-sassc "0.10.4"]])
         (assoc :sassc-docs ((:selmer-renderer options)
                              (slurp-resource "sassc/docs/sassc_instructions.md")
                              options))
         (assoc :sassc-config-params (pprint-code sassc-config)))]
    state))
