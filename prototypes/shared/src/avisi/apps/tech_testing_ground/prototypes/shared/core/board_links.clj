(ns avisi.apps.tech-testing-ground.prototypes.shared.core.board-links
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.core.boards :as boards]
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.database.board-links :as db]))

(defn get-connected-board
  [{source-platform :platform
    source-board-id :board-id}]
  (let [target-platform (boards/opposite-platform source-platform)
        target-board-identifier (boards/get-board-identifier target-platform)
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

(comment
  (get-connected-board {:platform "jira" :board-id 10002})
  (get-connected-board {:platform "monday" :board-id 3990111892})
  (sync-by-default? {:platform "jira" :board-id 10002})
  (sync-by-default? {:platform "monday" :board-id 3990111892}))
