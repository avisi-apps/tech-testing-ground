(ns server.main
  (:require [server.server :as server])
  (:gen-class))

(defn -main
  [& args]
  (println "Starting server on port 3000...")
  (server/start {:port 3000}))
