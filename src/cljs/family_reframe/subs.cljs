(ns family-reframe.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
  :persons
  (fn [ {persons :persons} ] persons ))