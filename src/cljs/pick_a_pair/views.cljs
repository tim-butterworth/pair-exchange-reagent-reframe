(ns pick-a-pair.views
  (:require [pick-a-pair.util :as util]
            [pick-a-pair.event-handlers]
            [pick-a-pair.subscriptions]
            [re-frame.core :refer [dispatch
                                   subscribe]]))

(defn update-input-value [value]
  (dispatch [:set-input-value value]))

(defn atom-input [value]
  [:input {:className "almost-full-width"
           :type "text"
           :value @value
           :on-change (fn [event]
                        (update-input-value (-> event .-target .-value)))}])

(defn add-participant [new-participant]
  (dispatch [:add-participant new-participant]))

(defn text-input [input]
  [:input {:className "almost-full-width"
           :type "button"
           :value "add a person"
           :on-click (fn [event]
                       (add-participant @input))}])

(defn delete-participant [id]
  (dispatch [:delete-participant id]))

(defn toggle-participant-display [id]
  (dispatch [:toggle-participant-display id]))

(defn update-participant-name [id new-name]
  (dispatch [:update-participant-name {:id id :name new-name}]))

(defn get-participant-view [participant]
  (let [participant-view-type (:view-type participant)
        name (:name participant)
        id (:id participant)]
    [:span 
     (if (= participant-view-type :display)
       [:span {:on-click
               (fn []
                 (toggle-participant-display id))}
        name]
       [:input {:type "text"
                :value name
                :auto-focus true
                :on-change (fn [event]
                             (update-participant-name id (-> event .-target .-value)))
                :on-blur (fn []
                           (toggle-participant-display id))}])]))

(defn display-participant [participant]
  [:div {:className "participant"}
   [:div
    (get-participant-view participant)
    [:span
     {:on-click (fn []
                  (delete-participant (participant :id)))
      :className "delete-participant"}
     "x"]]
   ;[:div
                                        ;[:ul (map (fn [project] [:li (project :name)]) @projects)]
   ; ]
   ])

(defn display-participants [participants]
  [:div
   (map (fn [participant]
          [display-participant participant])
        @participants)])

(defn main-page []
  (let [state-obj (subscribe [:state])
        input-value (subscribe [:input-value])
        participants (subscribe [:participants])]
    (fn []
      [:div
       [:div {:className "container"}
        [:div
         [:div {:className "header"}
          [:h2 "Pair Exchange for Justice"]]
         [:div {:className "patricipant-controls"}
          [:div [atom-input input-value]]
          [:div [text-input input-value]]]
         [:div {:className "participants"}
          [display-participants participants]]]]
       [:div {:className "clojure-object"}
        [:pre (util/pretty-to-string @state-obj)]]])))
