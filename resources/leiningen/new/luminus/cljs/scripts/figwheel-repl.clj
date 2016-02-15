(use 'figwheel-sidecar.repl-api)
(start-figwheel! {:all-builds (figwheel-sidecar.repl/get-project-cljs-builds)})
(cljs-repl)