(ns htmx.main
  (:require
    [htmx.server :as server]))
(defn -main
  [& args]
  (server/start-server))
