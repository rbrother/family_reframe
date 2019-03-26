(ns family-reframe.subs
  (:require
    [clojure.string :as str]
    [re-frame.core :as re-frame]
    [family-reframe.utils :as utils]))

(re-frame/reg-sub :persons (fn [ db _ ] (:persons db) ))

(re-frame/reg-sub :filter-text (fn [ db _ ] (:filter-text db) ))

(re-frame/reg-sub :page (fn [ db _ ] (:page db) ))

(re-frame/reg-sub :current-person (fn [ db _ ] (:current-person db) ))

(re-frame/reg-sub :families (fn [ db _ ] (:families db)))

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
          raw-families (filter families-selector families)]
      (assoc (person-index id)
        :families (map (partial expand-family id) raw-families)))))

(defn person-index-entry [p]
  { :text (str/upper-case (str p)) :person p } )

(re-frame/reg-sub
  :persons-text-index
  :<- [ :persons ]
  (fn [ persons _ ] (vec (map person-index-entry persons))))

(defn person-sort-func [p]
  (let [birth-time (-> p :birth :time)]
    (if birth-time birth-time "9999")))

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

