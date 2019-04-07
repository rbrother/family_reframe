(ns family-reframe.view.html
  (:require [re-frame.core :as re-frame]))

(defn horiz-stack [ & items]
  [:table [:tbody 
    (into [:tr] (map (fn [i] [:td i]) items))    ]])

(defn input-field [ data-path chars ]
  (let [current-value @(re-frame/subscribe [:value data-path])
        dispatcher #(re-frame/dispatch [:set-value data-path (-> % .-target .-value)]) ]
    [:input {:type "text" :id data-path :value current-value
             :style {:width (str (or chars 20) "em")}
             :on-change dispatcher}] ))

(defn set-image-src [image-id url]
  (let [image (-> js/document (.getElementById image-id))]
    (if image (set! (.-src image) url))))

(defn image [ person-id image-name width]
  (let [storage-ref (-> js/firebase .storage .ref)
        image-path (str "persons/" (clojure.string/upper-case (name person-id)) "-" image-name)
        image-id (str (random-uuid))
        image-ref (.child storage-ref image-path)
        set-src-func (fn [url] (set-image-src image-id url))]
    (-> image-ref (.getDownloadURL) (.then set-src-func))
    ; Set only id of the image now... the callback above will set the src later when URL retrieved
    [:img (merge {:id image-id} (if width {:width width} {}))]))

(defn format-name [ {:keys [first last called orig-last]} id ]
  (let [show-called (and called (not= called "") (not= called first))]
    [:span.link {:on-click #(re-frame/dispatch [:show-person id])}
     first
     (if show-called (str " (" called ")")) " "
     [:span {:style {:font-weight "bold"}} last]
     (if (and orig-last (not= orig-last last)) (str " os. " orig-last))]))

(defn format-life [{birth-time :time birth-city :city} {death-time :time death-city :city}]
  [:div
   (if birth-time [:div "s. " birth-time " " birth-city])
   (if death-time [:div "k. " death-time " " death-city]) ])

(def log-in-button [:button {:on-click #(re-frame/dispatch [:login-start])} "Log In"])

(def log-in-remainder [:span log-in-button " to show info of alive persons"])

(defn person-visible [ {:keys [birth death]}]
  (let [user @(re-frame/subscribe [:firebase-user])
        birth-time (:time birth)
        birth-year (if birth-time (int (subs birth-time 0 4)))
        assume-dead (and birth-year (< birth-year 1920))]
    (or user (:time death) assume-dead)))