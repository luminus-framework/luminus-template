(ns {{name}}.routes.auth
  (:use compojure.core)
  (:require [{{name}}.layout :as layout]
            [crypto.password.bcrypt :as password]
            [ring.util.response :refer [redirect status]]
            [bouncer
             [core :as b]
             [validators :as v]]
            [{{name}}.db.core :as db])
  (:import javax.xml.bind.DatatypeConverter))

(defn validate? [params]
  (first
    (b/validate
      params
      :id [[v/required]
           [db/get-user :message "duplicate user id"]]
      :pass [[v/required]
             [#(> (count %) 4) :message "password must be at least 5 characters"]]
      :pass1 [[v/required]
              [v/member #{(:pass params)} :message "entered passwords do not match"]])))

(defn register [& [id errors]]
  (layout/render
    "registration.html"
    {:id id
     :id-error (:id errors)
     :pass-error (:pass errors)
     :pass1-error (:pass1 errors)}))

(defn handle-registration [id pass pass1 {:keys [session]}]
  (if-let [errors (validate id pass pass1)]
    (register id errors)
    (try
      (do
        (db/create-user {:id id :pass (password/encrypt pass)})
        (session/put! :user-id id)
        (-> (redirect "/")
            (assoc :session (assoc session :user-id id))))
      (catch Exception ex
        (timbre/info "registration error" ex)
        (register id)))))

(defn profile [user-id]
  (layout/render
    "profile.html"
    {:user (db/get-user user-id)}))

(defn update-profile [{{:keys [first-name last-name email]} :status
                       {:keys [user-id]} :session}]
  (db/update-user user-id first-name last-name email)
  (profile user-id))

(defn parse-creds [auth]
  (when-let [basic-creds (second (re-matches #"\QBasic\E\s+(.*)" auth))]
    (->> (String. (DatatypeConverter/parseBase64Binary basic-creds) "UTF-8")
         (re-matches #"(.*):(.*)")
         rest)))

(defn handle-login [{:keys [headers session]}]
  (when-let [auth (get headers "authorization")]
    (let [[user pass] (parse-creds auth)
          account (db/get-user user)]
      (if (and account (password/check pass (:pass account)))
        (-> (response user)
            (assoc :session (assoc session :user-id user)))
        (status (response nil) 401)))))



(defn logout []
  (session/clear!)
  (resp/redirect "/"))

(defroutes auth-routes
  (GET "/register" []
       (register))

  (POST "/register" [id pass pass1 :as req]
        (handle-registration id pass pass1 req))

  (GET "/profile" [] (profile))

  (POST "/update-profile" req (update-profile req))

  (GET "/login" req
        (handle-login req))

  (GET "/logout" []
        (logout)))
