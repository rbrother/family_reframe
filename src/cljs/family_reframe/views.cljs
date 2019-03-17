(ns family-reframe.views
  (:require [re-frame.core :as re-frame]
            [family-reframe.subs]))

(defn format-name [{ :keys [first last called orig-last]}]
  (let [show-called (and called (not= called "") (not= called first))]
    [:span first
     (if show-called (str " (" called ")")) " "
     [:span {:style {:font-weight "bold"}} last]]))


(defn person-row [{pname :name :keys [id images profession birth death]}]
  [:tr
   [:td (name id)]
   [:td (str (first images))]
   [:td (format-name pname) ", " profession]
   [:td "Birth: " birth " " (if death [:span "Death: " death]) ]])

(defn main-panel []
  (let [persons @(re-frame/subscribe [:persons])]
    [:div
     [:h1 "List of Persons"]
     [:table
      (into [:tbody] (->> persons (sort-by #(-> % :birth :time)) (map person-row)))]
     ]))
