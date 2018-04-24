(ns<% if not hoplon %> ^:figwheel-no-load<% endif %> <<project-ns>>.app
  (:require [<<project-ns>>.core :as core]
            [cljs.spec.alpha :as s]
            [expound.alpha :as expound]
            [devtools.core :as devtools]))

(set! s/*explain-out* expound/printer)

(enable-console-print!)

(devtools/install!)

(core/init!)
