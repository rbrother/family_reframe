(ns family-reframe.subs
  (:require
    [clojure.string :as str]
    [re-frame.core :as re-frame]
    [family-reframe.utils :as utils]))

; Main data

(re-frame/reg-sub :persons (fn [ db _ ] (:persons db) ))

(re-frame/reg-sub :families (fn [ db _ ] (:families db)))

; Small individual pieces of info

(re-frame/reg-sub :filter-text (fn [ db _ ] (:filter-text db) ))

(re-frame/reg-sub :page (fn [ db _ ] (:page db) ))

(re-frame/reg-sub :error (fn [ db _ ] (:error db) ))

(re-frame/reg-sub :current-person (fn [ db _ ] (:current-person db) ))

(re-frame/reg-sub :generations (fn [ db _ ] (:generations db) ))

(re-frame/reg-sub :firebase-user (fn [ db _ ] (:firebase-user db) ))

; Second-level subscriptions

(re-frame/reg-sub
  :persons-index
  :<- [:persons]
  (fn [ persons _] (utils/index-by-id persons)))

(defn expand-family [parent-id {:keys [parents children]}]
  (let [spouse-id (first (disj parents parent-id))]
    {:spouse spouse-id
     :children children}))

(re-frame/reg-sub
  :person
  :<- [:persons-index]
  (fn [person-index [_ id]] (get person-index id)))

(re-frame/reg-sub
  :person-with-families
  :<- [:persons-index]
  :<- [:families]
  (fn [ [ person-index families ] [ _ id ] ]
    (let [families-selector (fn [{parents :parents}] (get parents id))           
          raw-families (filter families-selector families)
          parent-fam-selector (fn [{children :children}] (get children id))
          parents-family (first (filter parent-fam-selector families))]
      (assoc (person-index id)
        :families (map (partial expand-family id) raw-families)
        :parents (:parents parents-family)))))

(re-frame/reg-sub
  :persons-text-index
  :<- [ :persons ]
  (fn [ persons _ ]
    (letfn [(entry [p] {:text (str/upper-case (str p)) :person p})]
      (vec (map entry persons)))))

(defn person-sort-func [{:keys [birth death]}]
  (or (:time birth) (:time death) "9999"))

(re-frame/reg-sub
  :filtered-persons
  :<- [ :persons-text-index ]
  :<- [ :filter-text ]
  (fn [ [index filter-text] _ ]
    (let [pattern (re-pattern (str/upper-case (or filter-text "")))
          filter-person (fn [ {t :text }] (re-find pattern t))]
      (->> index (filter filter-person)
           (map (fn [ { p :person } ] p))
           (sort-by person-sort-func)))))

(re-frame/reg-sub :value (fn [ db [_ data-path]] (get-in db data-path)))

