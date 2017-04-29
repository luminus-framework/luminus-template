(ns<% if not hoplon %> ^:figwheel-no-load<% endif %> <<project-ns>>.app
  (:require [<<project-ns>>.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
