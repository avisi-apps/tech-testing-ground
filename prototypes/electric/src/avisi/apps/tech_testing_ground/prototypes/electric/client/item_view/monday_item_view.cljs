(ns ^:dev/always avisi.apps.tech-testing-ground.prototypes.electric.client.item-view.monday-item-view
  (:require
    [avisi.apps.tech-testing-ground.prototypes.electric.client.current-app :as current-app]
    [avisi.apps.tech-testing-ground.prototypes.electric.components.hello-world :as hello-world]
    [avisi.apps.tech-testing-ground.prototypes.electric.components.monday-item-view :as item-view]
    [hyperfiddle.electric :as e]
    [hyperfiddle.electric-dom2 :as dom]
    [promesa.core :as p]))

(def monday (js/window.mondaySdk))
(defn get-monday-context []
  (-> (monday.get "context")
      (p/then #(-> %
                   (js->clj :keywordize-keys true)
                   (:data)))))

(defn ^:export ^:dev/after-load init! []
  (p/let [{board-id :boardId item-id :itemId} (get-monday-context)]
         (current-app/initialize-app
           (e/boot (binding [dom/node js/document.body]
                     (hello-world/HelloWorld.))))))
