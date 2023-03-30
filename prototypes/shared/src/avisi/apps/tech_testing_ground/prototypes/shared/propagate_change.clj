(ns avisi.apps.tech-testing-ground.prototypes.shared.propagate-change
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.database :as db]
    [avisi.apps.tech-testing-ground.prototypes.shared.boards :as boards]
    [clojure.string :as str]
    [clojure.edn :as edn]
    ))

(defn board-key->platform [board-key]
  (->>
    board-key
    (name)
    (re-find #"^[^-]*")))

(defn platform->item-key [platform]
  (keyword (str platform "-item-id")))

(defn platform->board-key [platform]
  (keyword (str platform "-board-id")))

(defn instantiate-board [platform board-id]
  (case platform
    "monday" (boards/new-monday-board board-id)
    "jira" (boards/new-jira-board board-id)))

(defmulti get-target-board
  (fn [{:source/keys [platform]}] platform))

(defmethod get-target-board "jira"
  [{:source/keys [board-id]}]
  (->
    (db/get-board-link {:platform "jira" :board-id (edn/read-string board-id)})
    (:monday-board-id)
    (boards/new-monday-board)))
(defmethod get-target-board "monday"
  [{:source/keys [board-id]}]
  (->
    (db/get-board-link {:platform "monday" :board-id board-id})
    (:jira-board-id)
    (boards/new-jira-board)))

(defmulti get-target-item
  (fn [{:source/keys [platform]}] platform))

(defmethod get-target-item "jira"
  [{:source/keys [platform board-id item]
    {source-item-id :item/id} :source/item}]
  (let [{:keys [board-link-id]} (db/get-board-link {:platform platform :board-id (edn/read-string board-id)})]
    (some->>
      (db/get-item-link {:board-link-id board-link-id :jira-item-id source-item-id})
      (:monday-item-id)
      (assoc item :item/id))))

(defmethod get-target-item "monday"
  [{:source/keys [platform board-id item]
    {source-item-id :item/id} :source/item}]
  (let [{:keys [board-link-id]} (db/get-board-link {:platform platform :board-id board-id})]
    (some->>
      (db/get-item-link {:board-link-id board-link-id :monday-item-id source-item-id})
      (:jira-item-id)
      (assoc item :item/id))))

(defmulti create-item-link
  (fn [{:source/keys [platform]}] platform))

(defmethod create-item-link "jira"
  [{:source/keys [platform board-id]
    {target-item-id :item/id} :target/item
    {source-item-id :item/id} :source/item}]
  (let [{:keys [board-link-id]} (db/get-board-link {:platform platform :board-id (edn/read-string board-id)})]
    (db/create-item-link {:board-link-id board-link-id :jira-item-id source-item-id :monday-item-id target-item-id})))

(defmethod create-item-link "monday"
  [{:source/keys [platform board-id]
    {target-item-id :item/id} :target/item
    {source-item-id :item/id} :source/item}]
  (let [{:keys [board-link-id]} (db/get-board-link {:platform platform :board-id board-id})]
    (db/create-item-link {:board-link-id board-link-id :jira-item-id target-item-id :monday-item-id source-item-id})))

(defmulti delete-item-link
  (fn [{:source/keys [platform]}] platform))

(defmethod delete-item-link "jira"
  [{:source/keys [platform board-id]
    {source-item-id :item/id} :source/item}]
  (let [{:keys [board-link-id]} (db/get-board-link {:platform platform :board-id (edn/read-string board-id)})]
    (->>
      (db/get-item-link {:board-link-id board-link-id :jira-item-id source-item-id})
      (db/delete-item-link))))

(defmethod delete-item-link "monday"
  [{:source/keys [platform board-id]
    {source-item-id :item/id} :source/item}]
  (let [{:keys [board-link-id]} (db/get-board-link {:platform platform :board-id board-id})]
    (->>
      (db/get-item-link {:board-link-id board-link-id :monday-item-id source-item-id})
      (db/delete-item-link))))

(defonce last-created (atom nil))
(defonce last-updated (atom nil))

(defn last-created? [{:item/keys [title] :as item}]
  (= (:item/title @last-created) title))

(defn last-updated? [{:item/keys [title status] :as item}]
  (and @last-updated
    (= (select-keys @last-updated [:item/title :item/status]) (select-keys item [:item/title :item/status]))))

(comment

  @last-created
  @last-updated

  )

(defn propagate-add-item [{:keys [platform board-id item]}]

  (when-not (last-created? item)

    (let [target-item (->
                        (get-target-board {:source/platform platform :source/board-id board-id})
                        (boards/add-item item))]

      (create-item-link {:source/platform platform
                         :source/board-id board-id
                         :source/item item
                         :target/item target-item})

      (reset! last-created item))))

(defn propagate-update-item [{:keys [platform board-id item]}]

  (when-not (last-updated? item)

    (let [target-board (get-target-board {:source/platform platform
                                          :source/board-id board-id})
          target-item (get-target-item {:source/platform platform
                                        :source/board-id board-id
                                        :source/item item})]
      (boards/update-item target-board target-item)))

  (reset! last-updated item))

(defn propagate-delete-item [{:keys [platform board-id item] :as source}]
  (let [target-board (get-target-board {:source/platform platform
                                        :source/board-id board-id})
        target-item (get-target-item {:source/platform platform
                                      :source/board-id board-id
                                      :source/item item})]
    (def _ti target-item)
    (def _tb target-board)
    (def _s source)

    (when target-item
      (boards/delete-item target-board target-item)
      (delete-item-link {:source/platform platform
                         :source/board-id board-id
                         :source/item item}))))

(comment

  (let [{:keys [platform board-id item]} _s]
    (delete-item-link {:source/platform platform
                       :source/board-id board-id
                       :source/item item}))

  )
