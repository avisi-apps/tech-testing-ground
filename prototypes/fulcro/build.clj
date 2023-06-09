(ns build
  (:require
    [avisi-apps.tech-testing-ground.prototypes.build :as build]))

(def build-config
  {:tech-name "fulcro"
   :main 'avisi.apps.tech-testing-ground.prototypes.fulcro.server.main
   ; TODO: fix error monday-create-item-link-modal not opening after release-build
   ;:cljs {:build :frontend}
   })
(defn uberjar [_] (build/uberjar build-config))
