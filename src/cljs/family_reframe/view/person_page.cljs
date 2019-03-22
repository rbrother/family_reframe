(ns family-reframe.view.person-page
  (:require [re-frame.core :as re-frame]
            [family-reframe.subs]
            [family-reframe.view.html :as html]))

(defn person-page []
  (let [current-person @(re-frame/subscribe [:current-person])
        person-desc @(re-frame/subscribe [:person-descendants current-person])]
    [:div "Person page " person-desc]))