(ns avisi.apps.tech-testing-ground.prototypes.electric.main
  (:gen-class)
  (:require
    [avisi.apps.tech-testing-ground.prototypes.electric.hello-world :as hello-world] ; in prod, load app into server so
    ; it can accept clients
    [avisi.apps.tech-testing-ground.prototypes.shared.server :as server]))

(def electric-server-config
  {:host "0.0.0.0"
   :port (server/get-port "electric")
   :resources-path "public"})

(defn -main [& args] (server/start-server electric-server-config))

; On CLJS side we reuse src/user.cljs for prod entrypoint
