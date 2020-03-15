(ns leiningen.new.calva)

(defn calva-files [options]
  (cond 
    (some #{"+shadow-cljs"} (:features options))
    [[".vscode/settings.json" "calva/shadow-settings.json"]]
    
    (some #{"+cljs"} (:features options))
    [[".vscode/settings.json" "calva/figwheel-settings.json"]]
    
    :else
    [[".vscode/settings.json" "calva/server-only-settings.json"]]))

(defn calva-features [[assets options]]
  [(into assets (calva-files options)) options])