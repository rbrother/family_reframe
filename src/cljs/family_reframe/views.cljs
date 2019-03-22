(ns family-reframe.views
  (:require [re-frame.core :as re-frame]
            [family-reframe.subs]
            [family-reframe.view.person-list :as person-list]
            [family-reframe.view.person-page :as person-page]))

(defn header []
  [:div.header "Re-Frame Family Database - Copyright Robert J. Brotherus 2019. Logged in." ])


(defn main-panel []
  (let [page @(re-frame/subscribe [:page])]
    (println "Page: " page)
    [:div
     [header]
     (case page
       :person-page [person-page/person-page]
       :person-list [person-list/person-list-page]
       [:div "Loading..."] )]))
