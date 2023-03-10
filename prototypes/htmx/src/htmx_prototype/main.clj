(ns htmx-prototype.main
  (:require
    [htmx.server :as server]))
(defn -main
  [& args]
  (server/start-server))
