(ns fulcro-prototype.server.main
  (:gen-class)
  (:require [fulcro-prototype.server.server :as server]))

(defn -main
  [& args]
  (println "Starting server on port 3002...")
  (server/start-server))
