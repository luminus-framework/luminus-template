(ns {{name}}.component)

(defn as-html 
  [hiccup-data]
  (hiccup.core/html hiccup-data))

(defn
  ^{:private true}
  input
  "Generates Hiccup input stub"
  [input-type &
   {:keys
    [value
     attributes
     content]}]
  (let [element [:input 
                 (merge 
                  (if-not attributes {} attributes)
                  {:type input-type}
                  (when value {:value value}))]]
     (if-not (or (empty? content)
                 (nil? content))
       (conj element content)
       element)))

(defn button
  ([] (input "button" 
             :content "Button"))
  ([content] (button content {}))
  ([content attributes]
     (input "button" 
            :content content
            :attributes attributes)))

(defn submit-button
  ([] (input "submit"
             :value "Submit"))
  ([text] (submit-button text {}))
  ([text attributes]
     (input "submit"
            :value text
            :attributes attributes)))
