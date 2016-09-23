(ns pick-a-pair.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent]
            [pick-a-pair.util :as util]
            [re-frame.core :refer [reg-event-db
                                   reg-event-fx
                                   path
                                   reg-sub
                                   dispatch
                                   dispatch-sync
                                   subscribe]]))

(def participant-id (atom 0))
(def project-id (atom 0))

(defn participant [name]
  {:name name
   :id (swap! participant-id inc)})

(defn create-project [name ownerid]
  {:ownerid ownerid
   :id (swap! project-id inc)
   :name name})

(def initial-state
  {:participants (list (participant "participant1")
                       (participant "participant2")
                       (participant "participant3"))
   :projects (list (create-project "Reagent Man" 1))
   :input-value "enter some text friend"})


;; -- Event Handlers ----------------------------------------------------------


(reg-event-db
 :initialize
 (fn [db _]
   (merge db initial-state)))

(reg-event-db
 :set-input-value
 (fn [db [_ value]]
   (assoc db :input-value value)))

(reg-event-db
 :clear-input
 (fn [db _]
   (assoc db :input-value "")))

(reg-event-fx
 :add-participant
 (fn [world [_ value]]
   {:db (let [db (:db world)
              participants (db :participants)]
          (if (= value "")
            db
            (assoc db :participants
                   (conj participants
                         (participant value)))))
    :dispatch [:clear-input 1]}))

(reg-event-db
 :delete-participant
 (fn [db [_ id]]
   (assoc db :participants
          (filter
           (fn [participant]
             (not (= id (participant :id))))
           (db :participants)))))

;; -- Subscription Handlers ---------------------------------------------------

(reg-sub :state
         (fn [db _] db))

(reg-sub :input-value
         (fn [db _]
           (:input-value db)))

(reg-sub :participants
         (fn [db _]
           (:participants db)))

;; -- View Components ---------------------------------------------------------

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

(defn display-participant [participant]
  [:div {:className "participant"}
   [:div
    [:span
     (:name participant)]
    [:span
     {
      :on-click (fn []
                  (delete-participant (participant :id)))
      :className "delete-participant"}
     "x"]]
   [:div
                                        ;[:ul (map (fn [project] [:li (project :name)]) @projects)]
    ]])

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
;; -- Entry Point -------------------------------------------------------------


(defn mount-root []
  (dispatch-sync [:initialize])
  (reagent/render [main-page]
                  (js/document.getElementById "app")))

(defn init! []
  (mount-root))
