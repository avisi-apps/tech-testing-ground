(ns server
  (:require
    [hello-world]
    [server.core :as server]))

(defonce server (atom nil))

(defn app-routes []
  (hello-world/routes))
(defn start-server []
  (reset! server (server/instantiate-server (app-routes))))
(defn stop-server []
  (when-some [s @server]
    (.stop s)
    (reset! server nil)))

(defn restart-server []
  (stop-server)
  (start-server))

(comment

  (restart-server)

  )