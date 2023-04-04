(ns avisi.apps.tech-testing-ground.prototypes.shared.item-links
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.board-links :as board-links]
    [avisi.apps.tech-testing-ground.prototypes.shared.boards :as boards]
    [avisi.apps.tech-testing-ground.prototypes.shared.database :as db]
    [clojure.set :as set]))

(defn create-item-link
  [{source-board-id :board-id
    source-platform :platform
    {source-item-id :item/id
     :as            source-item}
    :item}
   {target-item-id :item/id}]
  (let [{:keys [board-link-id]} (db/get-board-link
                                  {:platform source-platform
                                   :board-id source-board-id})
        source-item-identifier (boards/get-item-identifier source-platform)
        target-item-identifier (boards/get-item-identifier (boards/opposite-platform source-platform))]
    (db/create-item-link
      {:board-link-id         board-link-id
       source-item-identifier source-item-id
       target-item-identifier target-item-id
       :item-representation   (select-keys source-item [:item/title :item/status])})))

(defn update-item-representation
  [{source-board-id :board-id
    source-platform :platform
    {source-item-id :id
     :as            source-item}
    :item}]
  (let [source-item-identifier (boards/get-item-identifier source-platform)
        {:keys [board-link-id]} (db/get-board-link
                                  {:platform source-platform
                                   :board-id source-board-id})]
    (->
      {:board-link-id         board-link-id
       source-item-identifier source-item-id}
      (db/get-item-link)
      (assoc :item-representation (select-keys source-item [:item/title :item/status]))
      (db/update-item-link))))

(defn delete-item-link
  [{source-board-id      :board-id
    source-platform      :platform
    {source-item-id :id} :item}]
  (let [{:keys [board-link-id]} (db/get-board-link
                                  {:platform source-platform
                                   :board-id source-board-id})
        source-item-identifier (boards/get-item-identifier source-platform)]
    (some->>
      (db/get-item-link
        {:board-link-id         board-link-id
         source-item-identifier source-item-id})
      (db/delete-item-link))))

(defn get-item-links [{:keys [platform board-id] :as board}]
  (-> board
      (db/get-board-link)
      (db/get-item-links)))

(defn get-unlinked-items [{:keys [platform board-id] :as board}]
  (let [item-identifier (boards/get-item-identifier platform)
        item-links (get-item-links board)
        items (boards/get-items board)
        unlinked-item-ids (set/difference (set (map :item/id items)) (set (map item-identifier item-links)))]
    (filter
      (fn [{:item/keys [id]}] (unlinked-item-ids id)) items)))

(defn get-item-representation
  [{source-board-id           :board-id
    source-platform           :platform
    {source-item-id :item/id} :item}]
  (let [source-item-identifier (boards/get-item-identifier source-platform)
        {:keys [board-link-id]} (db/get-board-link
                                  {:platform source-platform
                                   :board-id source-board-id})]
    (some->>
      {:board-link-id         board-link-id
       source-item-identifier source-item-id}
      (db/get-item-link)
      (:item-representation))))

(defn get-connected-item
  [{:keys                     [item]
    source-board-id           :board-id
    source-platform           :platform
    {source-item-id :item/id} :item}]
  (let [source-item-identifier (boards/get-item-identifier source-platform)
        target-item-identifier (boards/get-item-identifier (boards/opposite-platform source-platform))
        {:keys [board-link-id]} (db/get-board-link
                                  {:platform source-platform
                                   :board-id source-board-id})]
    (some->>
      {:board-link-id         board-link-id
       source-item-identifier source-item-id}
      (db/get-item-link)
      (target-item-identifier)
      (assoc item :item/id))))

(comment

  (get-item-links {:platform "jira" :board-id 10002})

  (get-item-links {:platform "monday" :board-id 3990111892})

  (->
    {:platform "jira" :board-id 10002}
    (board-links/get-connected-board)
    (boards/get-items))

  (->
    {:platform "jira" :board-id 10002}
    (board-links/get-connected-board)
    (get-unlinked-items))

  (->
    {:platform "monday" :board-id 3990111892}
    (boards/get-items))

  (->
    {:platform "monday" :board-id 3990111892}
    (get-item-links))

  (->
    {:platform "jira" :board-id 10002}
    (get-item-links))

  (->
    {:platform "monday" :board-id 3990111892}
    (get-unlinked-items))

  (->
    {:platform "jira" :board-id 10002}
    (get-unlinked-items))

  )
