(ns pick-a-pair.core-spec
  (:require-macros [speclj.core :refer [describe it should= should should-not]])
  (:require [speclj.core]
            [reagent.core :as reagent :refer [atom]]
            [pick-a-pair.core :as rc]))


(def isClient
  (not
   (nil? (try
           (.-document js/window)
           (catch js/Object e nil)))))

(def rflush reagent/flush)

(defn add-test-div [name]
  (let [doc     js/document
        body    (.-body js/document)
        div     (.createElement doc "div")]
    (.appendChild body div)
    div))

(defn with-mounted-component [comp f]
  (when isClient
    (let [div (add-test-div "_testreagent")]
      (let [comp (reagent/render-component comp div #(f comp div))]
        (reagent/unmount-component-at-node div)
        (reagent/flush)
        (.removeChild (.-body js/document) div)))))

(defn found-in [re div]
  (let [res (.-innerHTML div)]
    (if (re-find re res)
      true
      (do (println "Not found: " res)
          false))))

(def state {:key :value})
(def state-with-projects {
                          :projects [
                                     {:title "Pair Picker"}
                                     ]})

(describe "test home"
  (it "contains 'Welcome to' in home page"
      (with-mounted-component (rc/home-page state)
        (fn [c div]
          (should (found-in #"Welcome to" div)))))

  (describe "empty state"
            (it "displays a prompt"
                (with-mounted-component (rc/home-page state-without-projects)
                  (fn [c div]
                    (should (found-in #"Please add a project" div))))))

  ;(describe "with projects in the state"
  ;          (it "displays the list of projects"
  ;              (with-mounted-component (rc/home-page state-with-projects)
  ;                (fn [c div]
  ;                  ()))))
  )
