(ns htmx.server
  (:require
    [htmx.hello-world :as hello-world]
    [server.core :as server]
    [mount.core :as mount :refer [defstate]]))

(def routes
  (hello-world/routes))

(def server-config {:port 3001
                    :routes routes})

(defn start-server []
  (server/start-server server-config))

(defstate htmx-server
  :start (start-server)
  :stop (.stop htmx-server))

(comment

  (mount/start)

  (mount/stop)

  (mount/running-states)

  )



