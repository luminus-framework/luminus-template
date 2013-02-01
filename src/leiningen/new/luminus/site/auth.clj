(ns {{name}}.routes.auth
  (:use hiccup.form compojure.core)
  (:require [{{name}}.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [{{name}}.models.db :as db]))

(defn valid? [id pass pass1]
  (vali/rule (vali/has-value? id)
             [:id "user ID is required"])
  (vali/rule (vali/min-length? pass 5)
             [:pass "password must be at least 5 characters"])
  (vali/rule (= pass pass1)
             [:pass "entered passwords do not match"])
  (not (vali/errors? :id :pass :pass1)))

(defn error-item [[error]]
  [:div.error error])

(defn register [& [id]]
  (layout/base
    (form-to [:post "/register"]
             (label {:for "id"} "user-id" "user id")
             (vali/on-error :id error-item)
             [:p (text-field {:tabindex 1} "id" id)]

             (label {:for "pass"} "pass" "password")
             (vali/on-error :pass error-item)
             [:p (password-field {:tabindex 2} "pass")]

             (label {:for "pass1"} "pass1" "retype password")
             (vali/on-error :pass1 error-item)
             [:p (password-field {:tabindex 3} "pass1")]

             (submit-button {:class "btn" :tabindex 4} "create account"))))

(defn handle-registration [id pass pass1]
  (if (valid? id pass pass1)
    (try
      (do
        (db/create-user {:id id :pass (crypt/encrypt pass)})
        (session/put! :user id)
        (resp/redirect "/"))
      (catch Exception ex
        (vali/rule false [:id (.getMessage ex)])
        (register)))
    (register id)))

(defn handle-login [id pass]
  (let [user (db/get-user id)]
    (if (and user (crypt/compare pass (:pass user)))
      (session/put! :user id)
      )
    (resp/redirect "/")))

(defn logout []
  (session/clear!)
  (resp/redirect "/"))

(defroutes auth-routes
  (GET "/register" []
       (register))

  (POST "/register" [id pass pass1]
        (handle-registration id pass pass1))

  (POST "/login" [id pass]
        (handle-login id pass))

  (POST "/logout" []
        (logout)))
