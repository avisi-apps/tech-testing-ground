(ns avisi.apps.tech-testing-ground.prototypes.electric.server.server
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.server :as server]
    [ring.util.response :refer [redirect]]
    [hyperfiddle.electric-jetty-adapter :as adapter]
    [mount.core :as mount :refer [defstate]]))

(def server-config
  {:port (server/get-port "electric")
   :host "0.0.0.0"
   :routes []
   :resources-path "public"
   :ws-handler (fn [req] (adapter/electric-ws-adapter (partial adapter/electric-ws-message-handler req)))
   :jira-handlers {:item-view-handler (constantly (redirect "jira-item-view.html"))}
   :monday-handlers {:item-view-handler (constantly (redirect "monday-item-view.html"))}})

(defn start-server [] (server/start-server server-config))

(defstate electric-server :start (start-server) :stop (.stop electric-server))

(comment (mount/start) (mount/stop) (mount/running-states))
