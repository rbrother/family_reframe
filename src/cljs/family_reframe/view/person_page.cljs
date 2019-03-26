(ns family-reframe.view.person-page
  (:require [re-frame.core :as re-frame]
            [family-reframe.subs]
            [family-reframe.view.html :as html]
            [clojure.string :as str]))

(defn format-name [ {:keys [first last called orig-last]} ]
  (let [show-called (and called (not= called "") (not= called first))]
    [:span
     first (if show-called (str " (" called ")")) " "
     last (if orig-last (str " os. " orig-last))]
    ))

(defn format-birth [{:keys [time city]}]
  [:span time " " city])

(defn format-lines [text]
  (map (fn [line] ^{:key line} [:div line]) (str/split-lines text)))

(defn format-contact [{:keys [email www phone address]}]
  (list
    (if email ^{:key email} [:tr [:th "Email"] [:td email]])
    (if phone ^{:key phone} [:tr [:th "Phone"] [:td phone]])
    (if www ^{:key www} [:tr [:th "WWW"] [:td [:a {:href www} www]]])
    (if address ^{:key address} [:tr [:th "Address"] [:td (format-lines address)]])))

(defn person-properties [ { :keys [gender birth death contact profession career miscellaneous]}]
    [:table
     [:tbody
      [:tr [:th "Gender"] [:td (str/capitalize (name gender))]]
      [:tr [:th "Birth"] [:td (format-birth birth)]]
      (if death [:tr [:th "Death"] [:td (format-birth death)]])
      (if contact (format-contact contact))
      (if profession [:tr [:th "Profession"] [:td profession]] )
      (if career [:tr [:th "Career"] [:td career]])
      (if miscellaneous [:tr [:th "Miscellaneous"] [:td miscellaneous]])
      ]])

(defn map-image [ person-id { year :year image-name :name }]
  ^{:key image-name}
    [:td.center
      [:div (html/image person-id image-name 200)]
      [:div year]])

(defn image-row [id images]
  ^{:key (:name (first images))}
    [:tr (map (partial map-image id) images)])

(defn person-pictures [ { id :id images :images}]
  (let [groups (partition-all 3 images)]
    [:table [:tbody (map (partial image-row id ) groups)]]))

; Descendant tree

(def arrow-right-url "https://firebasestorage.googleapis.com/v0/b/family-brotherus.appspot.com/o/arrow-right.png?alt=media&token=ed818b26-229b-4d74-ab39-16dcac65e8f2")
(def join-url "https://firebasestorage.googleapis.com/v0/b/family-brotherus.appspot.com/o/join.png?alt=media&token=15804a57-6857-41ed-b63c-7604d9cc6f24")
(def line-right-url "https://firebasestorage.googleapis.com/v0/b/family-brotherus.appspot.com/o/line-right.png?alt=media&token=abc20134-d0f4-4a08-b7d3-db28f5ddd012" )

(declare descendant-tree)

(defn person-box [pid spouse]
  (let [person @(re-frame/subscribe [:person pid])
        {:keys [images gender birth death profession]} person
        border-color (if (= gender :male) "#0000ff" "#ff0000")
        bg-color (if spouse "#ffccff" "#ffffcc")]
    [:div {:style {:border (str "solid 3px " border-color)
                   :background bg-color
                   :font-size "smaller"
                   :max-width "300px"}}
     [:table [:tbody [:tr
                      [:td (if (first images) (html/image pid "small.png" nil))]
                      [:td
                       (html/format-name (:name person) pid)
                       (if profession [:span ", " profession])
                       (html/format-life birth death)]]]]]))

(defn person-row [pid]
  ^{:key pid}
  [:tr
   [:td [:img {:src arrow-right-url}]]
   [:td [ person-box pid ]]
   [:td ]
   [:td ]])

(defn family-row [{:keys [spouse children]} generations]
  [:tr
   [:td ]
   [:td [ person-box spouse true  ] ]
   [:td [:img {:src line-right-url}] ]
   [:td {:style {:border-left "solid black 3px"}}
    (doall (map (partial descendant-tree generations) children))]])

(defn family-tree [ generations family]
  (list
    ^{:key (str "Join" (-> family :spouse))}
    [:tr [:td] [:td.center [:img {:src join-url}]] [:td] [:td]]
    ^{:key (-> family :spouse)}
    [family-row family generations]))

(defn descendant-tree [ generations pid ]
  (let [{:keys [id families]} @(re-frame/subscribe [:person-with-families pid])]
    ^{:key id}
    [:div {:style {:margin-top "2mm"}}
     [:table.tree
      (into [:tbody [person-row id false]]
            (if (> generations 0)
              (mapcat (partial family-tree (dec generations)) families)))]]))

; ------------- ancestor tree -------------

(defn ancestor-tree [ generations pid ]
  "xxx")

(defn generation-button [n]
  (let [selected-generations @(re-frame/subscribe [:generations])]
    ^{:key n} [:button.gen
               {:style {:background (if (= selected-generations n) "#faa" "#bbb")}
                :on-click #(re-frame/dispatch [:select-generations n])}
                (str n)]))

(defn generation-buttons [] (doall (map generation-button (range 1 4))))

(defn person-page []
  (let [current-person @(re-frame/subscribe [:current-person])
        person @(re-frame/subscribe [:person-with-families current-person])
        generations @(re-frame/subscribe [:generations])]
    [:div
     [:h2 [:span.link {:on-click #(re-frame/dispatch [:show-person-list])} "Family Database"]
      " - "
      (format-name (:name person)) " (" (name (:id person)) ")" ]
     [:table
      [:tbody
       [:tr
        [:td (person-properties person)]
        [:td (person-pictures person)]]]]
     [:h2 "Ancestor tree - Generations" (generation-buttons) ]
     [ancestor-tree generations (:id person)]
     [:h2 "Descendant tree - Generations " (generation-buttons) ]
     [descendant-tree generations (:id person)] ]))