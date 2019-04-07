(ns family-reframe.views
  (:require [re-frame.core :as re-frame]
            [family-reframe.subs]
            [family-reframe.view.person-list :as person-list]
            [family-reframe.view.person-page :as person-page]
            [family-reframe.view.html :as html]))

(defn login-controls []
  (let [error @(re-frame/subscribe [:value [:error]])]
    [:div
     [:h2 "Log in"]
     [:table.data
      [:colgroup
       [:col]
       [:col]]
      [:tbody
       [:tr [:td {:col-span 2} "Single sign on" ]]
       [:tr [:td {:col-span 2 :style {:text-align "center"}}
             [:button.google-sso {:on-click #(re-frame/dispatch [:log-in-google])}
              [:span {:style {:font-size "16pt"}} "G+ "] " Log In with Google"]]]
       [:tr [:td {:col-span 2} "Log with Credentials" ]]
       [:tr [:td "Email"] [:td [html/input-field [:login :email] 15 ]]]
       [:tr [:td "Password"] [:td [html/input-field [:login :password] 15 ]]]
       [:tr [:td [:button {:on-click #(re-frame/dispatch [:log-in-email])} "Log in"]]]
       (if error [:tr [:td {:col-span 2 :style {:text-align "center"}}
                       [:div.error {:style {:width "25em"}} error]]])
       ]]]))

(defn header []
  (let [user @(re-frame/subscribe [:firebase-user])]
    [:div.header "Re-Frame Family Database - Copyright Robert J. Brotherus 2019. "
     (if user
       (let [{:keys [display-name email]} user]
         [:span "Logged In as " display-name " " [:button {:on-click #(re-frame/dispatch [:log-out])} "Log Out"]])
       html/log-in-button) ]))

(defn main-panel []
  (let [page @(re-frame/subscribe [:page])]
    [:div
     [header]
     (case page
       :login [login-controls]
       :person-page [person-page/person-page]
       :person-list [person-list/person-list-page]
       [:div "Loading..."] )]))
