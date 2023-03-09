(ns server
  (:require
    [hello-world]
    [server.core :as server]
    [mount.core :as mount :refer [defstate]]))

(def routes
  (hello-world/routes))

(def server-config {:port 3001
                    :routes routes})

(defstate htmx-server
  :start (server/start-server server-config)
  :stop (.stop htmx-server))

(comment

  (type htmx-server)

  (mount/start )

  (mount/stop)

  (mount/running-states)

  )



