(ns family-reframe.subs
  (:require
   [re-frame.core :as re-frame]
   [clojure.string :as str]))

(re-frame/reg-sub
  :persons
  (fn [ db _ ] (:persons db) ))

(re-frame/reg-sub
  :filter-text
  (fn [ db _ ] (:filter-text db) ))

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
