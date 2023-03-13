(ns htmx-prototype.main
  (:gen-class)
  (:require
    [htmx-prototype.server :as server]))
(defn -main
  [& args]
  (server/start-server))
