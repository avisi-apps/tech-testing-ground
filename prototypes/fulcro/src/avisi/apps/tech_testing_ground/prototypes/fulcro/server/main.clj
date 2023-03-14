(ns avisi.apps.tech-testing-ground.prototypes.fulcro.server.main
  (:gen-class)
  (:require
    [avisi.apps.tech-testing-ground.prototypes.fulcro.server.server :as server]))

(defn -main [& args] (server/start-server))
