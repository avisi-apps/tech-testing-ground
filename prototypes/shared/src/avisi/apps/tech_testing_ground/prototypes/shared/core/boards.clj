(ns avisi.apps.tech-testing-ground.prototypes.shared.core.boards
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.integration :as integration]))

(defn get-items [{:keys [platform board-id]}]
  (let [{{:keys [get-items]} :item-handling-functions
         {:keys [encode]} :domain-mappings}
          (integration/get-integration-props platform)]
    (->>
      (get-items board-id)
      (mapv encode))))

(defn add-item [{:keys [platform board-id]} item]
  (let [{{:keys [create-item]} :item-handling-functions
         {:keys [encode decode]} :domain-mappings}
          (integration/get-integration-props platform)]
    (->>
      item
      (decode)
      (create-item board-id)
      (encode))))

(defn update-item [{:keys [platform board-id]} item]
  (let [{{:keys [update-item]} :item-handling-functions
         {:keys [encode decode]} :domain-mappings}
          (integration/get-integration-props platform)]
    (->>
      item
      (decode)
      (update-item board-id)
      (encode))))

(defn delete-item [{:keys [platform board-id]} item]
  (let [{{:keys [delete-item]} :item-handling-functions
         {:keys [encode decode]} :domain-mappings}
          (integration/get-integration-props platform)]
    (->>
      item
      (decode)
      (delete-item board-id)
      (encode))))

(comment
  (get-items
    {:platform "monday"
     :board-id 3990111892})
  (add-item
    {:platform "monday"
     :board-id 3990111892}
    {:item/title "via record"})
  (update-item
    {:platform "monday"
     :board-id 3990111892}
    {:item/id "4255726976"
     :item/title "updated via record"
     :item/status "In Progress"})
  (delete-item
    {:platform "monday"
     :board-id 3990111892}
    {:item/id "4255726976"})
  (get-items
    {:platform "jira"
     :board-id 10002})
  (add-item
    {:platform "jira"
     :board-id 10002}
    {:item/title "via record"
     :item/description "a description"})
  (update-item
    {:platform "jira"
     :board-id 10002}
    {:item/id "ME-114"
     :item/title "updated via record"
     :item/description "changed description"
     :item/status "In Progress"})
  (delete-item
    {:platform "jira"
     :board-id 10002}
    {:item/id "ME-114"}))
