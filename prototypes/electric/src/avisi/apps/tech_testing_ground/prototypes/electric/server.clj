(ns avisi.apps.tech-testing-ground.prototypes.electric.server
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.server :as server]
    avisi.apps.tech-testing-ground.prototypes.electric.components.jira-item-view
    avisi.apps.tech-testing-ground.prototypes.electric.components.jira-create-item-link-modal
    avisi.apps.tech-testing-ground.prototypes.electric.components.monday-item-view
    avisi.apps.tech-testing-ground.prototypes.electric.components.monday-create-item-link-modal
    [hyperfiddle.electric-jetty-adapter :as adapter]
    [mount.core :as mount :refer [defstate]]
    [ring.util.response :refer [redirect]]))

(def server-config
  {:routes []
   :ws-handler (fn [req] (adapter/electric-ws-adapter (partial adapter/electric-ws-message-handler req)))
   :jira-handlers
     {:item-view-handler (constantly (redirect "jira-item-view.html"))
      :create-item-link-modal-handler (constantly (redirect "jira-create-item-link-modal.html"))}
   :monday-handlers
     {:item-view-handler (constantly (redirect "monday-item-view.html"))
      :create-item-link-modal-handler (constantly (redirect "monday-create-item-link-modal.html"))}})

(defn start-server [] (server/start-server server-config))

(defstate electric-server :start (start-server) :stop (.stop electric-server))

(comment (mount/start) (mount/stop) (mount/running-states))
