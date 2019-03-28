(ns family-reframe.view.html
  (:require [re-frame.core :as re-frame]))

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
        image-ref (.child storage-ref image-path)
        set-src-func (fn [url] (set-image-src image-path url))]
    (-> image-ref (.getDownloadURL) (.then set-src-func))
    ; Set only id of the image now... the callback above will set the src later when URL retrieved
    [:img (merge {:id image-path} (if width {:width width} {}))]))

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
