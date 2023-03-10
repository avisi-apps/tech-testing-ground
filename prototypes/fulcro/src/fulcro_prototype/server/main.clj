(ns fulcro-prototype.server.main
  (:require [server.server :as server])
  (:gen-class))

(defn -main
  [& args]
  (println "Starting server on port 3002...")
  (server/start-server))
