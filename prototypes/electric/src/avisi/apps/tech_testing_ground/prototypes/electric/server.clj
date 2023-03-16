(ns avisi.apps.tech-testing-ground.prototypes.electric.server
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.server :as server]
    [ring.util.response :refer [redirect]]
    [hyperfiddle.electric-jetty-adapter :as adapter]))

(def server-config
  {:port (server/get-port "electric")
   :host "0.0.0.0"
   :routes []
   :resources-path "public"
   :ws-handler (fn [req] (adapter/electric-ws-adapter (partial adapter/electric-ws-message-handler req)))
   :jira-handlers {:issue-panel-handler (constantly (redirect "iframe-content.html"))}})

(defn start-server [] (server/start-server server-config))

(comment

  (start-server)

  )
;(defstate htmx-server :start (start-server) :stop (.stop htmx-server))
;
;(comment (mount/start) (mount/stop) (mount/running-states))