(ns avisi.apps.tech-testing-ground.prototypes.shared.core.board-links
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.integration :as platform]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.boards :as boards]
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.database.board-links :as db]))

(defn get-connected-board
  [{source-platform :platform
    source-board-id :board-id}]
  (let [target-platform (platform/opposite-platform source-platform)
        target-board-identifier (platform/get-board-identifier target-platform)
        target-board-id (->
                          (db/get-board-link
                            {:platform source-platform
                             :board-id source-board-id})
                          (target-board-identifier))]
    {:platform target-platform :board-id target-board-id}))

(defn sync-by-default?
  [{:keys [board-id platform]}]
  (-> (db/get-board-link
        {:platform platform
         :board-id board-id})
      (:sync-by-default)))

(defn set-sync-by-default [{:keys [board-id platform]} sync-by-default]
  (-> (db/get-board-link
        {:platform platform
         :board-id board-id})
      (assoc :sync-by-default sync-by-default)
      (db/update-board-link)))

(comment
  (get-connected-board {:platform "jira" :board-id 10002})
  (get-connected-board {:platform "monday" :board-id 3990111892})
  (sync-by-default? {:platform "jira" :board-id 10002})
  (sync-by-default? {:platform "monday" :board-id 3990111892})
  (set-sync-by-default {:platform "monday" :board-id 3990111892} true)
  (set-sync-by-default {:platform "monday" :board-id 3990111892} false))
