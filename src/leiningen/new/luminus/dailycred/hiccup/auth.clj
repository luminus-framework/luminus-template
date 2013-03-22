(ns {{name}}.routes.auth
  (:use hiccup.form compojure.core)
  (:require [{{name}}.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [{{name}}.dailycred :as dailycred]))

(defn valid? [email username pass pass1]
  (vali/rule (vali/has-value? username)
             [:email "email is required"])  
  (vali/rule (vali/has-value? username)
             [:username "username is required"])
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

(defn register [& [email username]]
  (layout/common
    (form-to [:post "/register"]
             (field text-field :email "email" email)
             (field text-field :username "username" username)
             (field password-field :pass "password")
             (field password-field :pass1 "retype password")
             (submit-button {:class "btn" :tabindex 4} "create account"))))

(defn handle-registration [email username pass pass1]
  (if (valid? email username pass pass1)
    (try
      (let [response (dailycred/sign-up email username pass)]
        (if (= (:worked response) true)
          (do 
            (session/put! :user (-> response :user :id))
            (resp/redirect "/"))
          (do 
            (vali/rule false [:email (-> response :errors first :message)])
            (register email username))))
      (catch Exception ex
        (vali/rule false [:email (.getMessage ex)])
        (register email username)))
    (register email username)))

(defn handle-login [login pass]
  (let [response (dailycred/sign-in login pass)]
    (when (= (:worked response) true)      
      (session/put! :user (-> response :user :id)))
    (resp/redirect "/")))

(defn logout []
  (session/clear!)
  (resp/redirect "/"))

(defroutes auth-routes
  (GET "/register" []
       (register))

  (POST "/register" [email username pass pass1]
        (handle-registration email username pass pass1))

  (POST "/login" [id pass]
        (handle-login id pass))

  (POST "/logout" []
        (logout)))