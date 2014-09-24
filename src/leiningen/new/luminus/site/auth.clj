(ns {{name}}.routes.auth
  (:use compojure.core)
  (:require [{{name}}.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [{{name}}.db.core :as db])
  (:import javax.xml.bind.DatatypeConverter))

(defn valid? [id pass pass1]
  (vali/rule (vali/has-value? id)
             [:id "user ID is required"])
  (vali/rule (not (db/get-user id))
             [:id "duplicated user ID"])
  (vali/rule (vali/min-length? pass 5)
             [:pass "password must be at least 5 characters"])
  (vali/rule (= pass pass1)
             [:pass1 "entered passwords do not match"])
  (not (vali/errors? :id :pass :pass1)))

(defn register [& [id]]
  (layout/render
    "registration.html"
    {:id id
     :id-error (vali/on-error :id first)
     :pass-error (vali/on-error :pass first)
     :pass1-error (vali/on-error :pass1 first)}))

(defn handle-registration [id pass pass1]
  (if (valid? id pass pass1)
    (try
      (do
        (db/create-user {:id id :pass (crypt/encrypt pass)})
        (session/put! :user-id id)
        (resp/redirect "/"))
      (catch Exception ex
        (vali/rule false [:id (.getMessage ex)])
        (register)))
    (register id)))

(defn profile []
  (layout/render
    "profile.html"
    {:user (db/get-user (session/get :user-id))}))

(defn update-profile [{:keys [first-name last-name email]}]
  (db/update-user (session/get :user-id) first-name last-name email)
  (profile))

(defn parse-creds [auth]
  (when-let [basic-creds (second (re-matches #"\QBasic\E\s+(.*)" auth))]
    (->> (String. (DatatypeConverter/parseBase64Binary basic-creds) "UTF-8")
         (re-matches #"(.*):(.*)")
         rest)))

(defn handle-login [auth]
  (when auth
    (let [[user pass] (parse-creds auth)
          account (db/get-user user)]
      (if (and account (crypt/compare pass (:pass account)))
        (do (session/put! :user-id user)
            (resp/empty))
        (resp/status 401 (resp/empty))))))



(defn logout []
  (session/clear!)
  (resp/redirect "/"))

(defroutes auth-routes
  (GET "/register" []
       (register))

  (POST "/register" [id pass pass1]
        (handle-registration id pass pass1))

  (GET "/profile" [] (profile))
  
  (POST "/update-profile" {params :params} (update-profile params))
  
  (GET "/login" req
        (handle-login (get-in req [:headers "authorization"])))

  (GET "/logout" []
        (logout)))
