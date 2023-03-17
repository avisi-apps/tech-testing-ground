(ns ^:dev/always avisi.apps.tech-testing-ground.prototypes.electric.client.main-page
  (:require
    [avisi.apps.tech-testing-ground.prototypes.electric.components.hello-world :as hello-world]
    [avisi.apps.tech-testing-ground.prototypes.electric.client.current-app :as current-app]
    [hyperfiddle.electric :as e]
    [hyperfiddle.electric-dom2 :as dom]))

(defn ^:export init! []
  (current-app/initialize-app (e/boot (binding [dom/node js/document.body] (hello-world/HelloWorld.)))))
