(ns leiningen.new.calva)

(defn calva-files [options]
  (cond 
    (some #{"+shadow-cljs"} (:features options))
    [["{{name}} Server.code-workspace" "calva/server.code-workspace"]
     ["{{name}} Client.code-workspace" "calva/client.code-workspace"]]
    
    (some #{"+cljs"} (:features options))
    [[".vscode/settings.json" "calva/figwheel-settings.json"]]
    
    :else
    [[".vscode/settings.json" "calva/server-only-settings.json"]]))

(defn calva-features [[assets options]]
  [(into assets (calva-files options)) options])