(ns family-reframe.events
  (:require
   [re-frame.core :as re-frame]
   [family-reframe.db :as db]))

(re-frame/reg-event-db ::initialize-db (fn [_ _] db/default-db))

(re-frame/reg-event-db :set-value (fn [db [_ data-path value]] (assoc-in db data-path value)))

(re-frame/reg-event-db :show-person (fn [db [_ id]] (assoc db :current-person id :page :person-page)))

(re-frame/reg-event-db :select-generations (fn [db [_ n]] (assoc db :generations n)))

(re-frame/reg-event-db :show-person-list (fn [db _] (-> db (dissoc :current-person) (assoc :page :person-list))))

; Login / credentials

(re-frame/reg-event-db :login-start (fn [db _] (assoc db :page :login)))

(re-frame/reg-event-fx
  :log-in-google
  (fn [{db :db} _]
    {:db db
     :firebase/google-sign-in {:sign-in-method :popup :custom-parameters {}}}))

(re-frame/reg-event-fx
  :log-in-email
  (fn [{{{id :id password :given-password} :login :as db} :db} _]
    {:db (assoc-in db [:login :status] :waiting-for-login)
     :firebase/email-sign-in {:email id :password password}}))

(re-frame/reg-event-db
  :set-firebase-user
  (fn [ db [_ {email :email :as user}]]
    (if email
      (-> db (dissoc :login) (assoc :firebase-user user) (assoc :page :person-list))
      (-> db (dissoc :firebase-user) (assoc :page :person-list)) )))

(re-frame/reg-event-fx :log-out (fn [{db :db} _] {:db db :firebase/sign-out []}))
