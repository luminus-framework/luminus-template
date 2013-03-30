(ns leiningen.new.template-parser
  (:require
    [clojure.java.io :as io]
    [clojure.xml :as xml])
  (:import java.io.StringReader
           javax.xml.transform.OutputKeys
           javax.xml.transform.TransformerFactory
           [javax.xml.transform.stream StreamSource StreamResult]))

(defn write-template [filename xml]
  (.transform
    (doto
      (.newTransformer
        (TransformerFactory/newInstance))
      (.setOutputProperty OutputKeys/DOCTYPE_PUBLIC "")
      (.setOutputProperty OutputKeys/OMIT_XML_DECLARATION "yes")
      (.setOutputProperty OutputKeys/ENCODING "UTF-8")
      (.setOutputProperty OutputKeys/INDENT "yes")
      (.setOutputProperty "{http://xml.apache.org/xslt}indent-amount" "4")
      (.setOutputProperty OutputKeys/METHOD "html"))
    (StreamSource. (StringReader. (.replaceAll xml "\n" "")))
    (StreamResult. (io/writer filename))))

(defn parse-template [filename]  
  (-> filename
      (io/input-stream)
      (xml/parse)))

(defn css-tag [item]
  {:tag :link :attrs {:href item :rel "stylesheet" :type "text/css"} :content nil})

(defn js-tag [item]
  {:tag :script :attrs {:src item :type "text/javascript"} :content nil})

(defn add-css [content tags]  
  (let [[a b](split-with #(not= :link (:tag %)) content)] 
    (concat a (map css-tag tags) b)))

(defn add-to-layout [filename css js]
  (let [template (parse-template filename)]
    (write-template
      filename
      (with-out-str
        (xml/emit
          (clojure.walk/prewalk
            (fn [item]
              (if (= :head (:tag item))                
                (update-in item [:content]
                           #(-> % (add-css css) (concat (map js-tag js))))
                item))
            template))))))

(defn replace-tags [filename]
  (spit filename
        (-> filename
          (slurp)
          (.replaceAll "\\{%" "\n{%")
          (.replaceAll "%\\}" "%}\n")
          (.replaceAll "#%" "{{")
          (.replaceAll "%#" "}}"))))

(defn rewrite-template-tags [path]
  (doseq [file (->> path
                    clojure.java.io/file
                    file-seq
                    (map #(.getName %))
                    (filter #(.endsWith % ".html")))]
    (replace-tags (str path file))))

