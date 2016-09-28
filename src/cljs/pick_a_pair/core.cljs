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
   :view-type :display
   :id (swap! participant-id inc)})

(defn create-project [name ownerid]
  {:ownerid ownerid
   :id (swap! project-id inc)
   :name name})

(def initial-state
  {:participants (list
                  {:name "name"
                   :view-type :edit
                   :id (swap! participant-id inc)})
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
    :dispatch [:clear-input]}))

(reg-event-db
 :delete-participant
 (fn [db [_ id]]
   (assoc db :participants
          (filter
           (fn [participant]
             (not (= id (participant :id))))
           (db :participants)))))

(def reverse-type
  {:display :edit
   :edit :display})

(defn maybe-update-participant [participant id update-fn]
  (let [view-type (participant :view-type)]
   (if (= id (participant :id))
    (update-fn participant)
    participant)))

(reg-event-db
 :toggle-participant-display
 (fn [db [_ id]]
   (assoc db :participants
          (map
           (fn [participant]
             (maybe-update-participant
              participant
              id
              (fn [participant]
                (assoc participant :view-type (reverse-type (participant :view-type))))))
           (db :participants)))))

(reg-event-db
 :update-participant-name
 (fn [db [_ data]]
   (assoc db :participants
          (map
           (fn [participant]
             (maybe-update-participant
              participant
              (data :id)
              (fn [participant]
                (assoc participant :name (data :name)))))
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
