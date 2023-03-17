(ns avisi.apps.tech-testing-ground.prototypes.electric.main
  (:gen-class)
  (:require
    [avisi.apps.tech-testing-ground.prototypes.electric.hello-world :as hello-world] ; in prod, load app into server so
    ; it can accept clients
    [avisi.apps.tech-testing-ground.prototypes.electric.server :as server]))

(defn -main [& args] (server/start-server))
