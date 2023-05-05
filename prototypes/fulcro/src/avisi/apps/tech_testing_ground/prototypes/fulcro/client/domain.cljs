(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.domain
  (:require
    [com.fulcrologic.fulcro.components :refer [defsc]]))

(defsc ItemLink
  [_
   {:keys [monday-item-id jira-item-id]
    :as props}]
  {:query [:monday-item-id :jira-item-id]})

(defsc Item [_ _]
  {:query [:item/id]})
