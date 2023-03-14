(ns avisi.apps.tech-testing-ground.prototypes.htmx.server
  (:require
    [avisi.apps.tech-testing-ground.prototypes.htmx.hello-world :as hello-world]
    [avisi.apps.tech-testing-ground.prototypes.shared.server :as server]
    [mount.core :as mount :refer [defstate]]))

(def routes (hello-world/routes))

(def server-config
  {:port (server/get-port "htmx")
   :routes routes})

(defn start-server [] (server/start-server server-config))

(defstate htmx-server :start (start-server) :stop (.stop htmx-server))

(comment (mount/start) (mount/stop) (mount/running-states))
