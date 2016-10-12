(ns pick-a-pair.event-handlers
  (:require [re-frame.core :refer [reg-event-db
                                   reg-event-fx]]))

(def participant-id (atom 0))
(def project-id (atom 0))

(defn create-participant [name]
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
                   :view-type :display
                   :id (swap! participant-id inc)})
   :projects (list)
   :input-value "enter some text friend"})

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
                         (create-participant value)))))
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


