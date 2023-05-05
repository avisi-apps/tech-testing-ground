(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.button
  (:require
    ["@atlaskit/button" :default Button]
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]))

(def ui-button (interop/react-factory Button))
