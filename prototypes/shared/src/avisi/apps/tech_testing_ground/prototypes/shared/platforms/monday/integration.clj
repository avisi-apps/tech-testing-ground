(ns avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.integration
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.api-wrapper :as monday]
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.domain-mapping :as domain]))

(def integration-props
  {:identifiers
     {:board-identifier :monday-board-id
      :item-identifier :monday-item-id}
   :domain-mappings
     {:encode domain/monday-item->domain-item
      :decode domain/domain-item->monday-item}
   :item-handling-functions
     {:get-item-by-id monday/get-item-by-id
      :get-items monday/get-items
      :create-item monday/add-item
      :update-item monday/update-item
      :delete-item monday/delete-item}})
