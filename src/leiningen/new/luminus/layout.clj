(ns {{name}}.views.layout
  (:use hiccup.form
        [hiccup.def :only [defhtml]]
        [hiccup.element :only [link-to]]
        [hiccup.page :only [html5 include-js include-css]]))

(defn header []
  [:div.navbar.navbar-fixed-top.navbar-inverse
   [:ul.nav
    [:li (link-to "/" "Home")]
    [:li (link-to "/about" "About")]]])

(defn footer []
  [:footer "Copyright &copy; ..."])

(defhtml base [& content]
  [:head
   [:title "Welcome to {{name}}"]
   (include-css "/css/screen.css")]
  [:body content])

(defn common [& content]
  (base
    (header)
    [:div#content content]
    (footer)))
