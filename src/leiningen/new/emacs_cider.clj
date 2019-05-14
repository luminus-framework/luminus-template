(ns leiningen.new.emacs-cider
  (:require [leiningen.new.common :refer :all])
  )

(defn has-select? [{:keys [features]}]
  (some #{"+emacs-cider"} features))

(defn emacs-cider-features [state]
  (when (has-select? (second state))
    [(first state) (assoc (second state) :emacs-cider true)])
  )
