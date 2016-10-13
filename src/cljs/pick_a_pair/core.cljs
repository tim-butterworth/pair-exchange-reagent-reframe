(ns pick-a-pair.core
  (:require [reagent.core :as reagent]
            [pick-a-pair.views :as views]
            [re-frame.core :refer [dispatch-sync]]))

(defn mount-root []
  (dispatch-sync [:initialize])
  (reagent/render [views/main-page]
                  (js/document.getElementById "app")))

(defn init! []
  (mount-root))
