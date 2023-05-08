(ns ^:dev/always avisi.apps.tech-testing-ground.prototypes.electric.client.item-view.monday-item-view
  (:require
    [avisi.apps.tech-testing-ground.prototypes.electric.client.current-app :as current-app]
    [avisi.apps.tech-testing-ground.prototypes.electric.components.monday-item-view :as item-view]
    [hyperfiddle.electric :as e]
    [hyperfiddle.electric-dom2 :as dom]))

(defn ^:export ^:dev/after-load init! []
  (current-app/initialize-app (e/boot (binding [dom/node js/document.body] (item-view/HelloWorld.)))))
