(ns ^:dev/always avisi.apps.tech-testing-ground.prototypes.electric.client.item-view.monday-create-item-link-modal
  (:require
    [avisi.apps.tech-testing-ground.prototypes.electric.components.hello-world :as hello-world]
    [avisi.apps.tech-testing-ground.prototypes.electric.client.current-app :as current-app]
    [hyperfiddle.electric :as e]
    [hyperfiddle.electric-dom2 :as dom]))

(defn ^:export ^:dev/after-load init! []
  (current-app/initialize-app (e/boot (binding [dom/node js/document.body] (hello-world/HelloWorld.)))))
