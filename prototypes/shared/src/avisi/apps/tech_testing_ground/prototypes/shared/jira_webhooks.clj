(ns avisi.apps.tech-testing-ground.prototypes.shared.jira-webhooks
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.database :as db]
    [avisi.apps.tech-testing-ground.prototypes.shared.monday :as monday]
    [clojure.edn :as edn]))

(def path-to-project-id [:body-params :issue :fields :project :id])
(def path-to-issue-key [:body-params :issue :key])
(def path-to-account-id [:body-params :issue :creator :accountId])
(def path-to-name [:body-params :issue :fields :summary])
(def path-to-status [:body-params :issue :fields :status :name])

(defn translate-status [status]
  ({"To Do" -1
    "In Progress" "Working on it"
    "Done" "Done"} status))


(defn req->item [req]
  {:item/id (get-in req path-to-issue-key)
   :item/name (get-in req path-to-name)
   :item/status (get-in req path-to-status)})

(defn req->board [req]
  {:board/id (get-in req path-to-project-id)})

(defn req->board-link [req]
  (let [{:board/keys [id]} (req->board req)]
    (db/get-board-link {:platform "jira"
                        :board-id (edn/read-string id)})))

(defn req->item-link [req]
  (let [{:keys [board-link-id]} (req->board-link req)
        {jira-item-id :item/id} (req->item req)]
    (db/get-item-link {:board-link-id board-link-id :jira-item-id jira-item-id})))
(comment

  (req->item _ic-req)
  (req->board _ic-req)
  (req->board-link _ic-req)
  (req->item-link _ic-req)

  )

(defn domain-item->monday-item [item]
  (update item :status translate-status))

(defn issue-created-handler [req]
  (def _ic-req req)

  (let [{jira-item-id :item/id :as item} (req->item req)
        {:keys [board-link-id monday-board-id]} (req->board-link req)]
    (when-let [{{monday-item-id :id} :create_item} (->>
                                                     (domain-item->monday-item item)
                                                     (monday/add-item-to-board monday-board-id))]
      (db/create-item-link {:board-link-id board-link-id
                            :jira-item-id jira-item-id
                            :monday-item-id monday-item-id}))
    {:status 200}))

(defn issue-updated-handler [req]
  (def _iu-req req)

  (let [{:keys [monday-board-id]} (req->board-link req)
        {:keys [monday-item-id]} (req->item-link req)
        item (->
               (req->item req)
               (assoc :item/id monday-item-id)
               (domain-item->monday-item))]
    (monday/update-item monday-board-id item)
    {:status 200}))

(defn issue-deleted-handler [req]
  (def _id-req req)

  (let [{:keys [monday-item-id]} (req->item-link req)]
    (monday/delete-item {:item/id monday-item-id})
    {:status 200}))
