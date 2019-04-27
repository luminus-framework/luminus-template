(ns<% if not hoplon %><% if shadow-cljs %> ^:dev/once<% else %>^:figwheel-no-load<% endif %><% endif %> <<project-ns>>.app
  (:require
    [<<project-ns>>.core :as core]
    [cljs.spec.alpha :as s]
    [expound.alpha :as expound]
    [devtools.core :as devtools]))

(extend-protocol IPrintWithWriter
  js/Symbol
  (-pr-writer [sym writer _]
    (-write writer (str "\"" (.toString sym) "\""))))

(set! s/*explain-out* expound/printer)

(enable-console-print!)

(devtools/install!)

<% if kee-frame %>(core/init! true)<%else%>(core/init!)<% endif %>
