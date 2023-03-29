(ns avisi.apps.tech-testing-ground.prototypes.shared.jira-webhooks
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.database :as db]
    [avisi.apps.tech-testing-ground.prototypes.shared.jira :as jira]
    [avisi.apps.tech-testing-ground.prototypes.shared.monday :as monday]
    [avisi.apps.tech-testing-ground.prototypes.shared.domain :as domain]
    [clojure.edn :as edn]))

(def path-to-project-id [:body-params :issue :fields :project :id])
(def path-to-issue-key [:body-params :issue :key])
(def path-to-account-id [:body-params :issue :creator :accountId])
(def path-to-name [:body-params :issue :fields :summary])
(def path-to-status [:body-params :issue :fields :status :name])

(defn webhook-req->jira-issue [req]
  {:issue/key (get-in req path-to-issue-key)
   :issue/summary (get-in req path-to-name)
   :issue/status (get-in req path-to-status)})

(defn req->board [req]
  {:board/id (get-in req path-to-project-id)})

(defn req->board-link [req]
  (let [{:board/keys [id]} (req->board req)]
    (db/get-board-link {:platform "jira"
                        :board-id (edn/read-string id)})))

(defn req->item-link [req]
  (let [{:keys [board-link-id]} (req->board-link req)
        {jira-item-id :issue/key} (webhook-req->jira-issue req)]
    (db/get-item-link {:board-link-id board-link-id :jira-item-id jira-item-id})))

(comment

  (webhook-req->jira-issue _ic-req)
  (req->board _ic-req)
  (req->board-link _ic-req)
  (req->item-link _ic-req)

  )

(defn issue-created-handler [req]
  (def _ic-req req)

  (let [{jira-item-id :issue/key :as jira-issue} (webhook-req->jira-issue req)
        domain-item (->
                      (dissoc jira-issue :issue/key)
                      (domain/jira-issue->domain-item))
        {:keys [board-link-id monday-board-id]} (req->board-link req)]

    (jira/set-last-created domain-item)

    (when-not (monday/last-created? monday-board-id domain-item)
      (when-let [{{monday-item-id :id} :create_item} (monday/add-item-to-board monday-board-id domain-item)]
        (when-not (db/get-item-link {:board-link-id board-link-id
                                     :jira-item-id jira-item-id
                                     :monday-item-id monday-item-id})
          (db/create-item-link {:board-link-id board-link-id
                                :jira-item-id jira-item-id
                                :monday-item-id monday-item-id}))))
    {:status 200}))

(defn issue-updated-handler [req]
  (def _iu-req req)

  (let [{:keys [monday-board-id]} (req->board-link req)
        {:keys [monday-item-id]} (req->item-link req)
        jira-issue (webhook-req->jira-issue req)
        domain-item (-> jira-issue
                      (domain/jira-issue->domain-item)
                      (assoc :item/id monday-item-id))]

    (jira/set-last-updated domain-item)

    (when-not (monday/last-updated? monday-board-id domain-item)
      (monday/update-item monday-board-id domain-item))
    {:status 200}))

(comment

  (webhook-req->jira-issue _iu-req)

  )

(defn issue-deleted-handler [req]
  (def _id-req req)

  (let [{:keys [monday-item-id] :as item-link} (req->item-link req)
        domain-item {:item/id monday-item-id}]

    (when monday-item-id
      (monday/delete-item domain-item)
      (db/delete-item-link item-link))

    {:status 200}))

(comment

  (req->item-link _id-req)

  (webhook-req->jira-issue _id-req)

  (let [req _id-req]

    (let [{:keys [monday-item-id]} (req->item-link req)
          jira-issue (webhook-req->jira-issue req)
          monday-item {:item/id monday-item-id}]

      monday-item
      (req->item-link req)
      #_(jira/set-last-deleted jira-issue)

      #_(when-not (monday/last-deleted? "onzin" monday-item)
        (monday/delete-item monday-item))

      )

    )


  )
