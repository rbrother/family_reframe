(ns family-reframe.view.person-list
  (:require [re-frame.core :as re-frame]
            [family-reframe.subs]
            [family-reframe.view.html :as html]))

(defn filter-fields []
  [:div
   [:div "Filter text"]
   [:div [html/input-field [:filter-text] 8]]
   (let [persons @(re-frame/subscribe [:filtered-persons])]
     [:div (count persons) " persons"])])

(defn person-row [{person-name :name :keys [id images profession birth death] :as person}]
  (let [show (html/person-visible person)]
    [:tr
     [:td id]
     [:td (if (and show (first images)) (html/image id "small.png" nil))]
     (if show
       [:td {:style {:width "9cm"}}
        (html/format-name person-name id)
        (if profession [:span ", " profession])]
       [:td html/log-in-remainder])
     [:td {:style {:width "8cm"}} (if show (html/format-life birth death))]]))

(defn person-list [ ]
  (let [persons @(re-frame/subscribe [:filtered-persons])]
    [:table.even-odd
     (into [:tbody] (map person-row persons))]))

(defn person-list-page []
  [:table
   [:tbody
    [:tr
     [:td [filter-fields]]
     [:td [person-list]]]]])