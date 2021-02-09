(ns <<project-ns>>.core)

(defn<% if shadow-cljs %> ^:dev/after-load<% endif %> mount-components []
  (let [content (js/document.getElementById "app")]
    (while (.hasChildNodes content)
      (.removeChild content (.-lastChild content)))
    (.appendChild content (js/document.createTextNode "Welcome to <<name>>"))))

(defn init! []
  (mount-components))
