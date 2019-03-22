(ns family-reframe.view.html
  (:require [re-frame.core :as re-frame]))

(defn input-field [ data-path chars ]
  (let [current-value @(re-frame/subscribe [:value data-path])
        dispatcher #(re-frame/dispatch [:set-value data-path (-> % .-target .-value)]) ]
    [:input {:type "text" :id data-path :value current-value
             :style {:width (str (or chars 20) "em")}
             :on-change dispatcher}] ))
