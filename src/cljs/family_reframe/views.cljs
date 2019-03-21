(ns family-reframe.views
  (:require [re-frame.core :as re-frame]
            [family-reframe.subs]))

(defn input-field [ data-path chars ]
  (let [current-value @(re-frame/subscribe [:value data-path])
        dispatcher #(re-frame/dispatch [:set-value data-path (-> % .-target .-value)]) ]
    [:input {:type "text" :id data-path :value current-value
             :style {:width (str (or chars 20) "em")}
             :on-change dispatcher}] ))

(defn filter-fields []
  [:div
   [:div "Filter text"]
   [:div [input-field [:filter-text]]]])

(defn format-name [{:keys [first last called orig-last]}]
  (let [show-called (and called (not= called "") (not= called first))]
    [:span first
     (if show-called (str " (" called ")")) " "
     [:span {:style {:font-weight "bold"}} last]]))

(defn format-image [{:keys [year name]}]
  name)

(defn format-life [{birth-time :time birth-city :city} {death-time :time death-city :city}]
  [:div
   (if birth-time [:div "s. " birth-time " " birth-city])
   (if death-time [:div "k. " death-time " " death-city]) ])

(defn person-row [{person-name :name :keys [id images profession birth death]}]
  [:tr
   [:td id]
   [:td (format-image (first images))]
   [:td (format-name person-name) (if profession [:span ", " profession])]
   [:td (format-life birth death)]])


(defn person-list []
  (let [persons @(re-frame/subscribe [:filtered-persons])]
    [:table.even-odd
     (into [:tbody] (->> persons (map person-row)))]))

(defn main-panel []
  [:div
   [:h1 "List of Persons"]
   [:table
    [:tbody
     [:tr
      [:td [filter-fields]]
      [:td [person-list]]]]]])
