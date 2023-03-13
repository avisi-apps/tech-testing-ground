(ns htmx-prototype.main
  (:gen-class)
  (:require
    [htmx-prototype.server :as server]
    [clojure.string :as str]))
(defn -main [& args] (server/start-server))
