(ns pick-a-pair.db
  (:require [re-frame.core :as re-frame]))

(def participant-id (atom 0))
(def project-id (atom 0))

(defn participant [name]
  {:name name
   :id (swap! participant-id inc)})

(defn create-project [name ownerid]
  {:ownerid ownerid
    :id (swap! project-id inc)
   :name name})

(def default-value
  {:participants (list (participant "participant1")
                       (participant "participant2")
                       (participant "participant3"))
   :projects (list (create-project "Reagent Man" 1))
   :input-value "enter some text friend"})
