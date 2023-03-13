(ns build
  (:require
    [avisi-apps.tech-testing-ground.prototypes.build :as build]))

(defn uberjar [_]
  (build/uberjar {:tech-name "fulcro"
                  :main 'fulcro-prototype.server.main
                  :cljs true}))
