(ns avisi.apps.tech-testing-ground.prototypes.electric.main
  (:gen-class)
  (:require
    ; in prod, load app into server so
    ; it can accept clients
    [avisi.apps.tech-testing-ground.prototypes.electric.components.monday-create-item-link-modal]
    [avisi.apps.tech-testing-ground.prototypes.electric.components.monday-item-view]
    [avisi.apps.tech-testing-ground.prototypes.electric.components.jira-create-item-link-modal]
    [avisi.apps.tech-testing-ground.prototypes.electric.components.jira-item-view]
    [avisi.apps.tech-testing-ground.prototypes.electric.server :as server]))

(defn -main [& args] (server/start-server))
