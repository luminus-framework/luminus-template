(ns <<project-ns>>.nrepl
  (:require
    [nrepl.server :as nrepl]
    [clojure.java.io :as io]
    [clojure.tools.logging :as log]))

(defn ->var
  [thing]
  (if-not (symbol? thing)
    thing
    (do (assert (and (symbol? thing) (namespace thing))
                (format "expected namespaced symbol (%s)" thing))
        (require (symbol (namespace thing)))
        (resolve thing))))

(defn ->mw-list
  [thing]
  (when thing
    (let [thing* (->var thing)
          thing  (if (var? thing*) @thing* thing*)]
      (if-not (sequential? thing)
        [thing*]
        (mapcat ->mw-list thing)))))

(defn start
  "Start a network repl for debugging on specified port followed by
  an optional parameters map. The :bind, :transport-fn, :handler,
  :ack-port and :greeting-fn will be forwarded to
  clojure.tools.nrepl.server/start-server as they are."
  [{:keys [port bind middleware transport-fn ack-port greeting-fn]}]
  (try
    (log/info "starting nREPL server on port" port)
    (log/info "nrepl.add-middleware: " middleware)
    (nrepl/start-server :port port
                        :bind bind
                        :transport-fn transport-fn
                        ;; :handler handler
                        :handler (apply nrepl/default-handler (->mw-list middleware))
                        :ack-port ack-port
                        :greeting-fn greeting-fn)
    (doto (io/file ".nrepl-port") .deleteOnExit (spit port))

    (catch Throwable t
      (log/error t "failed to start nREPL")
      (throw t))))

(defn stop [server]
  (nrepl/stop-server server)
  (log/info "nREPL server stopped"))
