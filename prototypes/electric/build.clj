(ns build
  (:require
    [avisi-apps.tech-testing-ground.prototypes.build :as build]))

(def build-config
  {:tech-name "electric"
   :main 'avisi.apps.tech-testing-ground.prototypes.electric.main
   ;TODO: fix release builds
   ;:cljs {:build :prod}
   })
(defn uberjar [_] (build/uberjar build-config))
