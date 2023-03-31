(ns avisi.apps.tech-testing-ground.prototypes.shared.propagate-change
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.database :as db]
    [avisi.apps.tech-testing-ground.prototypes.shared.boards :as boards]
    [clojure.string :as str]))

(def opposite-platform
  {"jira" "monday"
   "monday" "jira"})

(def platforms
  {"jira"
   {:board-identifier :jira-board-id
    :item-identifier :jira-item-id
    :instantiate-board boards/new-jira-board}
   "monday"
   {:board-identifier :monday-board-id
    :item-identifier :monday-item-id
    :instantiate-board boards/new-monday-board}})
(defn get-platform [platform-name] (platforms platform-name))

(defn get-target-board
  [{source-board-id :source/board-id
    source-platform :source/platform}]
  (let [{target-board-identifier :board-identifier
         instantiate-target-board :instantiate-board}
        (->
          source-platform
          (opposite-platform)
          (get-platform))]
    (->>
      (db/get-board-link
        {:platform source-platform
         :board-id source-board-id})
      (target-board-identifier)
      (instantiate-target-board))))

(defn get-target-item
  [{:source/keys [item]
    source-board-id :source/board-id
    source-platform :source/platform
    {source-item-id :item/id} :source/item}]
  (let [{source-item-identifier :item-identifier} (get-platform source-platform)
        {target-item-identifier :item-identifier} (get-platform (opposite-platform source-platform))
        {:keys [board-link-id]} (db/get-board-link
                                  {:platform source-platform
                                   :board-id source-board-id})]
    (some->>
      {:board-link-id board-link-id
       source-item-identifier source-item-id}
      (db/get-item-link)
      (target-item-identifier)
      (assoc item :item/id))))

(defn get-item-representation
  [{source-board-id :source/board-id
    source-platform :source/platform
    {source-item-id :item/id} :source/item}]
  (let [{source-item-identifier :item-identifier} (get-platform source-platform)
        {:keys [board-link-id]} (db/get-board-link
                                  {:platform source-platform
                                   :board-id source-board-id})]
    (some->>
      {:board-link-id board-link-id
       source-item-identifier source-item-id}
      (db/get-item-link)
      (:item-representation))))

(defn update-item-representation
  [{source-board-id :source/board-id
    source-platform :source/platform
    {source-item-id :item/id
     :as source-item}
    :source/item}]
  (let [{source-item-identifier :item-identifier} (get-platform source-platform)
        {:keys [board-link-id]} (db/get-board-link
                                  {:platform source-platform
                                   :board-id source-board-id})]
    (->
      {:board-link-id board-link-id
       source-item-identifier source-item-id}
      (db/get-item-link)
      (assoc :item-representation (select-keys source-item [:item/title :item/status]))
      (db/update-item-link))))

(defn create-item-link
  [{source-board-id :source/board-id
    source-platform :source/platform
    {source-item-id :item/id
     :as source-item}
    :source/item
    {target-item-id :item/id} :target/item}]
  (let [{:keys [board-link-id]} (db/get-board-link
                                  {:platform source-platform
                                   :board-id source-board-id})
        {source-item-identifier :item-identifier} (get-platform source-platform)
        {target-item-identifier :item-identifier} (get-platform (opposite-platform source-platform))]
    (db/create-item-link
      {:board-link-id board-link-id
       source-item-identifier source-item-id
       target-item-identifier target-item-id
       :item-representation (select-keys source-item [:item/title :item/status])})))

(defn delete-item-link
  [{source-board-id :source/board-id
    source-platform :source/platform
    {source-item-id :item/id} :source/item}]
  (let [{:keys [board-link-id]} (db/get-board-link
                                  {:platform source-platform
                                   :board-id source-board-id})
        {source-item-identifier :item-identifier} (get-platform source-platform)]
    (some->>
      (db/get-item-link
        {:board-link-id board-link-id
         source-item-identifier source-item-id})
      (db/delete-item-link))))

(defn sync-by-default?
  [{source-board-id :source/board-id
    source-platform :source/platform
    {source-item-id :item/id} :source/item}]
  (-> (db/get-board-link
        {:platform source-platform
         :board-id source-board-id})
    (:sync-by-default)))

(defmulti propagate-action (fn [{:keys [action]}] action))

(defmethod propagate-action :create
  [{:source/keys [platform board-id item]
    :as source}]
  (prn (sync-by-default? source))
  (when (and (sync-by-default? source) (not (get-item-representation source)))
    (let [target-item (->
                        (get-target-board source)
                        (boards/add-item item))]
      (create-item-link (assoc source :target/item target-item)))))

(defmethod propagate-action :update
  [{:source/keys [platform board-id item]
    :as source}]
  (when-not (= item (get-item-representation source))
    (let [target-board (get-target-board source)
          target-item (get-target-item source)]
      (boards/update-item target-board target-item)
      (update-item-representation source))))

(defmethod propagate-action :delete
  [{:source/keys [platform board-id item]
    :as source}]
  (let [target-board (get-target-board source)
        target-item (get-target-item source)]
    (when target-item (boards/delete-item target-board target-item) (delete-item-link source))))

(defn propagate-action-fn [webhook-req->propagation-args]
  (fn [req]
    (try
      (->
        req
        (webhook-req->propagation-args)
        (propagate-action))
      (catch Exception e (println "An error occured") (println (ex-message e))))))
