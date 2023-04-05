(ns avisi.apps.tech-testing-ground.prototypes.shared.platforms.jira.integration
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.jira.api-wrapper :as jira]
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.jira.domain-mapping :as domain]))

(def integration-props
  {:identifiers
   {:board-identifier :jira-board-id
    :item-identifier  :jira-item-id}
   :domain-mappings
   {:encode domain/jira-issue->domain-item
    :decode domain/domain-item->jira-issue}
   :item-handling-functions
   {:get-items   jira/get-items
    :create-item jira/add-item
    :update-item jira/update-item
    :delete-item jira/delete-item}})
