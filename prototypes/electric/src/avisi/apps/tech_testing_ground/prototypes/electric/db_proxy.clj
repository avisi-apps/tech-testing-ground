(ns avisi.apps.tech-testing-ground.prototypes.electric.db-proxy
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]))

(defonce !item-links (atom nil))

(defn load-item-link [{:keys [platform board-id item-id]}]
  (let [item-link (item-links/get-item-link
                    {:platform platform
                     :board-id board-id
                     :item-id item-id})]
    (swap! !item-links assoc-in [:by-jira-id (:jira-item-id item-link)] item-link)
    (swap! !item-links assoc-in [:by-monday-id (:monday-item-id item-link)] item-link)))

(defn create-item-link [source-item target-item]
  (let [item-link (item-links/create-item-link source-item target-item)]
    (swap! !item-links assoc-in [:by-jira-id (:jira-item-id item-link)] item-link)
    (swap! !item-links assoc-in [:by-monday-id (:monday-item-id item-link)] item-link)))

(defn delete-item-link
  [{board-id :board-id
    platform :platform
    {id :id} :item
    :as item-link}]
  (let [deleted (item-links/delete-item-link item-link)]
    (swap! !item-links update :by-jira-id dissoc (:jira-item-id deleted))
    (swap! !item-links update :by-monday-id dissoc (:monday-item-id deleted))))

(comment @!item-links (reset! !item-links nil) (get-in @!item-links [:by-jira-id "ME-174"]))
