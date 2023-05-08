(ns avisi.apps.tech-testing-ground.prototypes.electric.components.atlas-kit.dropdown-menu
  (:require
    ["@atlaskit/dropdown-menu" :default DropdownMenu :refer [DropdownItemGroup DropdownItem DropdownMenuStateless]]))

  (defn DropdownMenuComp [data]
        (let [monday-item-id 1]
          [:> DropdownMenu {}
           [:> DropdownItemGroup {}
            (if monday-item-id
              [:> DropdownItem {}
               #_{:onClick #(prn "Delete")}
               "Delete ItemLink"]
              [:> DropdownItem {}
               #_{:onClick #(prn "Create")}
               "Create ItemLink"])]]))
