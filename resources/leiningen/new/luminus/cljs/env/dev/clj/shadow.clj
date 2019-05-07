(ns leiningen.new.luminus.cljs.env.dev.clj.shadow
  (:require [shadow.cljs.devtools.api :as shadow]))

(defn cljs
  ([]
   (cljs :app))
  ([env]
    (shadow/repl env)))
