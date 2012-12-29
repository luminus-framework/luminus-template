(ns {{name}}.server
  (:use {{name}}.handler
        ring.adapter.jetty
        [ring.middleware file-info file]
        bultitude.core)
  (:gen-class))
; gen-class propagates, you don't need it outside of server.clj
; (:gen-class) must be in the ns in order for lein uberjar to work.
; Enables you to byte-compile everything into a single jar/war

(defn wrap-reload-if-available [handler]
  (if (some #{'ring.middleware.reload} (namespaces-on-classpath))
    (do       
      (use 'ring.middleware.reload)
      ((resolve 'wrap-reload) handler))
    handler))

(defn get-handler []
  ; #'app expands to (var app) so that when we reload our code, 
  ; the server is forced to re-resolve the symbol in the var
  ; rather than having its own copy. When the root binding
  ; changes, the server picks it up without having to restart.
  (-> #'app
    ; Makes static assets in $PROJECT_DIR/resources/public/ available.
    (wrap-file "resources")
    ; Content-Type, Content-Length, and Last Modified headers for files in body
    (wrap-file-info)
    (wrap-reload-if-available)))

(defn -main [& [port]]  
  (init)
  (let [port    (if port (Integer/parseInt port) 8080)]    
    (run-jetty (get-handler) {:join? false :port port})
    (println "Server started on port [" port "].")
    (println (str "You can view the site at http://localhost:" port))))
