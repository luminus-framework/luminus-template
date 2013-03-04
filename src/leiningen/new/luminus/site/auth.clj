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
             [:pass1 "entered passwords do not match"])
  (not (vali/errors? :id :pass :pass1)))

(defn field [f fname flabel & [value]]
  (let [sname (name fname)
        error-item (fn [[error]] [:div.error error])]
    (list (label {:for sname} sname flabel)
          (vali/on-error fname error-item)
          [:p (f {:tabindex 1} sname value)])))

(defn register [& [id]]
  (layout/base
    (form-to [:post "/register"]
             (field text-field :id "user id" id)
             (field password-field :pass "password")
             (field password-field :pass1 "retype password")
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
