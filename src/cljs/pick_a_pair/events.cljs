(ns pick-a-pair.events
  (:require
   [pick-a-pair.db :refer [default-value]]
   [re-frame.core :refer [reg-event-db reg-event-fx inject-cofx path trim-v
                          after debug]]
   [cljs.spec :as s]))

(reg-event-db
 :add-participant
 (fn [todos [id]]
   (update-in todos [id :done] not)))

(reg-event-db
 :initialise-db
 (fn [db _]
     (merge db default-value)))
