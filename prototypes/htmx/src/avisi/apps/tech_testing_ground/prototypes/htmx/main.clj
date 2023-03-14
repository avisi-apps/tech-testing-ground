(ns avisi.apps.tech-testing-ground.prototypes.htmx.main
  (:gen-class)
  (:require
    [avisi.apps.tech-testing-ground.prototypes.htmx.server :as server]
    [clojure.string :as str]))
(defn -main [& args] (server/start-server))
