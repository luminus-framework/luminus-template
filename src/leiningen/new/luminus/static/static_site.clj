(ns {{name}}.static-site)

(def site-definition
  {:routes
        [["index.html" :get "/index.html"]
         ["about.html" :get "/about.html"]]
   :css {:exclude #"bootstrap"}})