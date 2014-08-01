(ns {{name}}.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [POST]]))

(def state (atom {:doc {} :saved? false}))

(defn set-value! [id value]
  (swap! state assoc :saved? false)
  (swap! state assoc-in [:doc id] value))

(defn get-value [id]
  (get-in @state [:doc id]))

(defn row [label & body]
  [:div.row
   [:div.col-md-2 [:span label]]
   [:div.col-md-3 body]])

(defn text-input [id label]
  [row label
   [:input {:type "text"
            :class "form-control"
            :value (get-value id)
            :onChange #(set-value! id (-> % .-target .-value))}]])

(defn list-item [id k v states]
  (letfn [(handle-click! []
                         (swap! (get states k) not)
                         (set-value! id (->> states (filter (fn [[_ v]] @v)) keys)))]
    [:li {:class (str "list-group-item" (if @(get states k) " active"))
          :onClick handle-click!}
     v]))

(defn selection-list [id label & items]
  (let [states (->> items
                    (map (fn [[k]] [k (atom false)]))
                    (into {}))]
    (fn []
      [row label
       [:ul.list-group
        (for [[k v] items]
          [list-item id k v states])]])))

(defn save-doc []
  (POST (str js/context "/save")
        {:params (:doc @state)
         :handler (fn [_] (swap! state assoc :saved? true))}))

(defn status []
  (when-not (:saved? @state)
    [:p "document has unsaved changes"]))

(defn home []
  [:div
   [:div.page-header [:h1 "Reagent Form"]]

   [status]
   [text-input :first-name "First name"]
   [text-input :last-name "Last name"]
   [selection-list :favorite-drinks "Favorite drinks"
    [:coffee "Coffee"] [:beer "Beer"] [:crab-juice "Crab juice"]]

   (if (:saved? @state)
     [:p "Saved"]
     [:button {:type "submit"
               :class "btn btn-default"
               :onClick save-doc}
      "Submit"])])

;;start the app
(reagent/render-component [home] (.getElementById js/document "app"))
