(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.dropdown-menu
  (:require
    ["@atlaskit/dropdown-menu" :default DropdownMenu :refer [DropdownItemGroup DropdownItem DropdownMenuStateless]]
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop])
  )

(def ui-dropdown-menu (interop/react-factory DropdownMenu))
(def ui-dropdown-menu-stateless (interop/react-factory DropdownMenuStateless))
(def ui-dropdown-item-group (interop/react-factory DropdownItemGroup))
(def ui-dropdown-item (interop/react-factory DropdownItem))
