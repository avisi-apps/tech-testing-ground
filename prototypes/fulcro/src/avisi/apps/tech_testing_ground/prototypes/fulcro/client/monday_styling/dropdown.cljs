(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.monday-styling.dropdown
  (:require
    ["monday-ui-react-core/dist/Dropdown" :default Dropdown]
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]))
(def ui-dropdown (interop/react-factory Dropdown))
