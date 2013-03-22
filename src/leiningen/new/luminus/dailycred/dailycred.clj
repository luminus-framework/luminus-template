(ns {{name}}.dailycred
  (use [clojure.java.io :only [reader]])
  (require [clojure.data.json :as json]))

(def client-id "Your DailyCred application Client Id goes here!")

(def base-url "https://www.dailycred.com/user/api")

(defn sign-in [login pass] 
  (let [response (slurp (format "%s/signin.json?login=%s&pass=%s&client_id=%s"
                                  base-url login pass client-id))]
    (json/read-str response :key-fn #(keyword %))))

(defn sign-up [email login pass] 
  (let [response (slurp (format "%s/signup.json?email=%s&username=%s&pass=%s&client_id=%s"
                                  base-url email login pass client-id))]
      (json/read-str response :key-fn #(keyword %))))