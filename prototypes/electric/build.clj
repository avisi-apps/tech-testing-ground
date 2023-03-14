(ns build
  (:require
    [avisi-apps.tech-testing-ground.prototypes.build :as build]))

(def build-config {:tech-name "electric"
                   :main 'electric-prototype.main
                   :cljs {:build :prod}})
(defn uberjar [_]
  (build/uberjar build-config))
