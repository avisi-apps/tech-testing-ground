(ns build
  (:require
    [avisi-apps.tech-testing-ground.prototypes.build :as build]))

(def build-config {:tech-name "fulcro"
                   :main 'avisi.apps.tech-testing-ground.prototypes.fulcro.server.main
                   :cljs {:build :frontend}})
(defn uberjar [_]
  (build/uberjar build-config))
