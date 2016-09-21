(ns pick-a-pair.views
  (:require [reagent.core :as reagent :refer [atom cursor]]
            [pick-a-pair.util :as util]
            [re-frame.core :refer [subscribe dispatch]]))

(def participant-id (atom 0))
(def project-id (atom 0))

(defn participant [name]
  {:name name
   :id (swap! participant-id inc)})

(defn create-project [name ownerid]
  {:ownerid ownerid
    :id (swap! project-id inc)
    :name name})

(defonce app-state (atom {:participant (list (participant "participant1")
                                             (participant "participant2")
                                             (participant "participant3"))
                          :projects (list (create-project "Reagent Man" 1))
                          :input-value "enter some text friend"}))

(defn update-input-value [value]
  (let [input-value (cursor app-state [:input-value])]
    (reset! input-value value)))

(defn add-participant []
  (let [participants (cursor app-state [:participant])
        input (cursor app-state [:input-value])]
    (if (not
         (= @input ""))
      (do
        (swap! participants
               (fn [participant-list]
                 (conj participant-list (participant @input))))
        (reset! input "")))))

(defn delete-participant [participant-id]
  (let [participants (cursor app-state [:participant])]
    (swap! participants
           (fn [participants]
             (filter
              (fn [participant]
                (not
                 (= participant-id (:id participant))))
              participants)))))

(defn atom-input [value]
  [:input {:className "almost-full-width"
           :type "text"
           :value @value
           :on-change (fn [event]
                        (update-input-value
                         (-> event .-target .-value)))}])

(defn counting-component [input participants]
  [:input {:className "almost-full-width"
           :type "button" :value "add a person"
           :on-click (fn [event]
                       (add-participant))}])

(defn display-state [state-obj]
  [:div
   {:className "clojure-object"}
   "state-obj:"
   [:pre (str (util/pretty-to-string state-obj))]])

(defn display-participant [participant projects]
  [:div {:className "participant"}
   [:div
     [:span
      (:name participant)]
     [:span
      {:on-click (fn [] (delete-participant (participant :id)))
       :className "delete-participant"}
      "x"]]
      [:div
        [:ul
         (map (fn [project] [:li (project :name)]) @projects)]]])

(defn main-page []
  (let [participants (subscribe [:participants])
        input (subscribe [:input-value])
        projects (subscribe [:projects])
        state-obj (subscribe [:showing])]
    (fn []
      [:div
       [:div {:className "container"}
        [:div
         [:div {:className "header"}
          [:h2 "Pair Exchange for Justice"]]
         [:div {:className "patricipant-controls"}
          [:div [atom-input input]]
          ;[:div [counting-component input participants]]
          ]
         [:div {:className "participants"}
                                        ;[:div
                                        ; (map (fn [participant]
                                        ;        [display-participant participant projects])
                                        ;      @participants-cursor)]
          ]
         ]]
       [:div (.log js/console @input)]])))
