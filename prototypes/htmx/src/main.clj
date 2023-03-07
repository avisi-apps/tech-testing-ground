(ns main
  (:require
    [server]))
(defn -main
  [& args]
  (server/start-server))
