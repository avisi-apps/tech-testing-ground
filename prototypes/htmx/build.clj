(ns build
  (:require
    [avisi-apps.tech-testing-ground.prototypes.build :as build]))

(def build-config {:tech-name "htmx"
                   :main 'htmx-prototype.main})
(defn uberjar [_]
  (build/uberjar build-config))
