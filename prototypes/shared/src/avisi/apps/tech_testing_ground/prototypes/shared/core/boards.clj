(ns avisi.apps.tech-testing-ground.prototypes.shared.core.boards
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.jira.api-wrapper :as jira]
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.jira.domain-mapping :as jira-domain-mapping]
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.api-wrapper :as monday]
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.domain-mapping :as monday-domain-mapping]))

(defmulti get-platform-props name)

(defmethod get-platform-props "jira" [_]
  {:identifiers
   {:board-identifier :jira-board-id
    :item-identifier  :jira-item-id}
   :domain-mappings
   {:encode jira-domain-mapping/jira-issue->domain-item
    :decode jira-domain-mapping/domain-item->jira-issue}
   :item-handling-functions
   {:get-items   jira/get-items
    :create-item jira/add-item
    :update-item jira/update-item
    :delete-item jira/delete-item}})

(defmethod get-platform-props "monday" [_]
  {:identifiers
   {:board-identifier :monday-board-id
    :item-identifier  :monday-item-id}
   :domain-mappings
   {:encode monday-domain-mapping/monday-item->domain-item
    :decode monday-domain-mapping/domain-item->monday-item}
   :item-handling-functions
   {:get-items   monday/get-items
    :create-item monday/add-item
    :update-item monday/update-item
    :delete-item monday/delete-item}})

(def opposite-platform
  {"jira"   "monday"
   "monday" "jira"})

(defn get-board-identifier [platform]
  (-> platform
      (get-platform-props)
      (get-in [:identifiers :board-identifier])))

(defn get-item-identifier [platform]
  (-> platform
      (get-platform-props)
      (get-in [:identifiers :item-identifier])))

(defn get-items [{:keys [platform board-id]}]
  (let [{{:keys [get-items]} :item-handling-functions
         {:keys [encode]}    :domain-mappings} (get-platform-props platform)]
    (->>
      (get-items board-id)
      (mapv encode))))

(defn add-item [{:keys [platform board-id]} item]
  (let [{{:keys [create-item]}   :item-handling-functions
         {:keys [encode decode]} :domain-mappings} (get-platform-props platform)]
    (->>
      item
      (decode)
      (create-item board-id)
      (encode))))

(defn update-item [{:keys [platform board-id]} item]
  (let [{{:keys [update-item]}   :item-handling-functions
         {:keys [encode decode]} :domain-mappings} (get-platform-props platform)]
    (->>
      item
      (decode)
      (update-item board-id)
      (encode))))

(defn delete-item [{:keys [platform board-id]} item]
  (let [{{:keys [delete-item]}   :item-handling-functions
         {:keys [encode decode]} :domain-mappings} (get-platform-props platform)]
    (->>
      item
      (decode)
      (delete-item board-id)
      (encode))))

(comment
  (get-items
    {:platform "monday" :board-id 3990111892})
  (add-item
    {:platform "monday" :board-id 3990111892}
    {:item/title "via record"})
  (update-item
    {:platform "monday" :board-id 3990111892}
    {:item/id     "4255726976"
     :item/title  "updated via record"
     :item/status "In Progress"})
  (delete-item
    {:platform "monday" :board-id 3990111892}
    {:item/id "4255726976"})
  (get-items {:platform "jira" :board-id 10002})
  (add-item
    {:platform "jira" :board-id 10002}
    {:item/title       "via record"
     :item/description "a description"})
  (update-item
    {:platform "jira" :board-id 10002}
    {:item/id          "ME-114"
     :item/title       "updated via record"
     :item/description "changed description"
     :item/status      "In Progress"})
  (delete-item {:platform "jira" :board-id 10002} {:item/id "ME-114"}))
