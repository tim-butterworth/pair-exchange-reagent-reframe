(ns pick-a-pair.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 :showing
 (fn [db _]
   db))

(reg-sub
  :participants
  (fn [query-v _]
    (subscribe [:participants])))

(reg-sub
  :input-value
  (fn [query-v _]
    (subscribe [:input-value])))

(reg-sub
  :projects
  (fn [query-v _]
    (subscribe [:projects])))




