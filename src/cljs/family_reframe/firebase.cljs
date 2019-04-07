(ns family-reframe.firebase
  (:require [clojure.walk :as walk]
            [com.degel.re-frame-firebase :as firebase]
            [re-frame.core :as re-frame]))

(defonce firebase-app-info
         {:apiKey "AIzaSyDvaMRFcRuvJuDsTopc8fNtvTBfTvJbFms"
          :authDomain "family-brotherus.firebaseapp.com"
          :databaseURL "https://family-brotherus.firebaseio.com"
          :projectId "family-brotherus"
          :storageBucket "family-brotherus.appspot.com" })

(defonce firebase-initialized (atom false))

(defn init []
  (if (not @firebase-initialized)
    (do
      (reset! firebase-initialized true)
      (firebase/init :firebase-app-info firebase-app-info
                     ; See: https://firebase.google.com/docs/reference/js/firebase.firestore.Settings
                     :firestore-settings {:timestampsInSnapshots true}
                     :get-user-sub [:firebase-user]
                     :set-user-event [:set-firebase-user]
                     :default-error-handler [:firebase-error]))))

(defn message->doc [ {data :data id :id ref :ref} ]
  (walk/keywordize-keys (assoc data :id id :ref ref)))

(defn message->docs [ { docs :docs } ] (map message->doc docs))

(re-frame/reg-fx
  :firebase-upload
  (fn [ { file :file id :id data-path :data-path } ]
    (let [storage-ref (-> js/firebase .storage .ref)
          image-ref (.child storage-ref id)]
      (-> image-ref
          (.put file)
          (.then #(-> image-ref
                      (.getDownloadURL)
                      (.then (fn [url] (re-frame/dispatch [:upload-complete data-path url]) ))))))))