(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.select
  (:require
    ["@atlaskit/select" :refer [CreatableSelect AsyncSelect] :default Select]
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]))

(def ui-select (interop/react-factory Select))
(def ui-creatable-select (interop/react-factory CreatableSelect))
(def ui-async-select (interop/react-factory AsyncSelect))
