(ns leiningen.new.emacs-cider
  (:require [leiningen.new.common :refer :all])
  )

(defn has-select? [{:keys [features]}]
  (some #{"+emacs-cider"} features))

(defn emacs-cider-features [state]
  (if (has-select? (second state))
    [(first state)
     (-> (second state)
         (assoc :emacs-cider true)
         (append-options :dev-dependencies [['refactor-nrepl "2.4.0"]
                                        ['cider/cider-nrepl "0.21.1"]
                                        ])
         )]
    state)
  )
