(ns pick-a-pair.subscriptions
    (:require [reagent.core :as reagent]
              [re-frame.core :refer [reg-sub]]))

(reg-sub :state
         (fn [db _] db))

(reg-sub :input-value
         (fn [db _]
           (:input-value db)))

(reg-sub :participants
         (fn [db _]
           (:participants db)))
