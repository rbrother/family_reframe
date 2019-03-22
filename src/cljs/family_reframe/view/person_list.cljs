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

(defn format-name [ id {:keys [first last called orig-last]}]
  (let [show-called (and called (not= called "") (not= called first))]
    [:span.link { :on-click #(re-frame/dispatch [:show-person id])  } first
     (if show-called (str " (" called ")")) " "
     [:span {:style {:font-weight "bold"}} last]]))

(defn format-image [ person-id { image-name :name }]
  (if image-name
    [:img {:src (str "http://www.brotherus.net/family-data/images/" (name person-id) "/small.png")}] ))

(defn format-life [{birth-time :time birth-city :city} {death-time :time death-city :city}]
  [:div
   (if birth-time [:div "s. " birth-time " " birth-city])
   (if death-time [:div "k. " death-time " " death-city]) ])

(defn person-row [{person-name :name :keys [id images profession birth death]}]
  [:tr
   [:td id]
   [:td (format-image id (first images))]
   [:td {:style { :width "9cm"}} (format-name id person-name) (if profession [:span ", " profession])]
   [:td {:style { :width "8cm"}} (format-life birth death)]])

(defn person-list []
  (let [persons @(re-frame/subscribe [:filtered-persons])]
    [:table.even-odd
     (into [:tbody] (map person-row persons))]))

(defn person-list-page []
  [:table
   [:tbody
    [:tr
     [:td [filter-fields]]
     [:td [person-list]]]]])