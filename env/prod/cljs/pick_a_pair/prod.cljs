(ns pick-a-pair.prod
  (:require [pick-a-pair.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
