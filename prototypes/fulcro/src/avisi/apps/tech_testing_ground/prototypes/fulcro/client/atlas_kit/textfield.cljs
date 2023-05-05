(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.textfield
  (:require
    ["@atlaskit/textfield" :default Textfield]
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]))
(def ui-textfield (interop/react-input-factory Textfield))
