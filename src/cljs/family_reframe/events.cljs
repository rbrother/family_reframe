(ns family-reframe.events
  (:require
   [re-frame.core :as re-frame]
   [family-reframe.db :as db]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
  :set-value
  (fn [db [_ data-path value]] (assoc-in db data-path value)))

(re-frame/reg-event-db
  :show-person
  (fn [db [_ id]] (assoc db :current-person id :page :person-page)))

(re-frame/reg-event-db
  :select-generations
  (fn [db [_ n]] (assoc db :generations n)))

(re-frame/reg-event-db
  :show-person-list
  (fn [db [_ id]]
    (-> db (dissoc :current-person) (assoc :page :person-list))))

