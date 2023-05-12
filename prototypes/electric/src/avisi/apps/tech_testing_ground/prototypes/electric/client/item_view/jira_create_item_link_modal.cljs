(ns ^:dev/always avisi.apps.tech-testing-ground.prototypes.electric.client.item-view.jira-create-item-link-modal
  (:require
    [avisi.apps.tech-testing-ground.prototypes.electric.client.current-app :as current-app]
    [avisi.apps.tech-testing-ground.prototypes.electric.components.jira-create-item-link-modal :as item-link-modal]
    [hyperfiddle.electric :as e]
    [hyperfiddle.electric-dom2 :as dom]
    [clojure.edn :as edn]
    ))

(defn ^:export ^:dev/after-load init! []
  (js/AP.context.getContext
    (fn [res]
      (let [{{{issue-key :key} :issue {project-id :id} :project} :jira}
            (js->clj res :keywordize-keys true)]
        (current-app/initialize-app
          (e/boot (binding [dom/node js/document.body]
                    (item-link-modal/CreateItemLinkModal.
                      (edn/read-string project-id) issue-key))))))))
