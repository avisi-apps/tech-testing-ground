(ns avisi.apps.tech-testing-ground.prototypes.shared.boards
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.domain :as domain]
    [avisi.apps.tech-testing-ground.prototypes.shared.monday :as monday]
    [avisi.apps.tech-testing-ground.prototypes.shared.jira :as jira]))

(defprotocol Board
  (get-items [_])
  (update-item [_ item])
  (add-item [_ item])
  (delete-item [_ item]))

(defrecord MondayBoard [board-id]
  Board
  (get-items [_]
    (->>
      (monday/get-items board-id)
      (mapv domain/monday-item->domain-item)))
  (add-item [_ item]
    (->> item
      (domain/domain-item->monday-item)
      (monday/add-item board-id)
      (domain/monday-item->domain-item)))
  (update-item [_ item]
    (->> item
      (domain/domain-item->monday-item)
      (monday/update-item board-id)
      (domain/monday-item->domain-item)))
  (delete-item [_ item]
    (->> item
      (domain/domain-item->monday-item)
      (monday/delete-item board-id)
      (domain/monday-item->domain-item))))

(defn new-monday-board [board-id]
  (->MondayBoard board-id))

(defrecord JiraBoard [board-id]
  Board
  (get-items [_]
    (->>
      (jira/get-items board-id)
      (mapv domain/jira-issue->domain-item)))
  (add-item [_ item]
    (->> item
      (domain/domain-item->jira-issue)
      (jira/add-item board-id)
      (domain/jira-issue->domain-item)))
  (update-item [_ item]
    (->> item
      (domain/domain-item->jira-issue)
      (jira/update-item board-id)
      (domain/jira-issue->domain-item)))
  (delete-item [_ item]
    (->> item
      (domain/domain-item->jira-issue)
      (jira/delete-item board-id)
      (domain/jira-issue->domain-item))))

(defn new-jira-board [board-id]
  (->JiraBoard board-id))
(comment

  (def monday-board (->MondayBoard 3990111892))

  (get-items monday-board)
  (add-item monday-board {:item/title "via record"})
  (update-item monday-board {:item/id 4228890295
                             :item/title "updated via record"
                             :item/status "In Progress"})
  (delete-item monday-board {:item/id 4229005186})

  (def jira-board (->JiraBoard 10001))

  (get-items jira-board)
  (add-item jira-board {:item/title "via record"
                        :item/description "a description"})
  (update-item jira-board {:item/id "EX-238"
                           :item/title "updated via record"
                           :item/description "changed description"
                           :item/status "In Progress"})
  (delete-item jira-board {:item/id "EX-238"})

  )
