(ns avisi.apps.tech-testing-ground.prototypes.shared.jira-webhooks
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.database :as db]
    [avisi.apps.tech-testing-ground.prototypes.shared.jira :as jira]
    [avisi.apps.tech-testing-ground.prototypes.shared.monday :as monday]
    [avisi.apps.tech-testing-ground.prototypes.shared.domain :as domain]
    [avisi.apps.tech-testing-ground.prototypes.shared.boards :as boards]
    [avisi.apps.tech-testing-ground.prototypes.shared.propagate-change :as propagate]
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

(defn webhook-req->jira-board [req]
  {:board/id (get-in req path-to-project-id)})



(comment

  (webhook-req->jira-issue _ic-req)
  (req->board _ic-req)
  (webhook-req->jira-board _ic-req)
  (req->item-link _ic-req)

  )

(defn issue-created-handler [req]
  (def _ic-req req)

  (let [{board-id :board/id} (webhook-req->jira-board req)
        domain-item (-> (webhook-req->jira-issue req)
                      (domain/jira-issue->domain-item))]

    (propagate/propagate-add-item {:platform "jira"
                                   :board-id board-id
                                   :item domain-item}))
  {:status 200})

(defn issue-updated-handler [req]
  (def _iu-req req)

  (let [{board-id :board/id} (webhook-req->jira-board req)
        domain-item (-> (webhook-req->jira-issue req)
                      (domain/jira-issue->domain-item))]

    (propagate/propagate-update-item {:platform "jira"
                                      :board-id board-id
                                      :item domain-item}))

  {:status 200})

(defn issue-deleted-handler [req]
  (def _id-req req)

  (let [{board-id :board/id} (webhook-req->jira-board req)
        domain-item (-> (webhook-req->jira-issue req)
                      (domain/jira-issue->domain-item))]

    (propagate/propagate-delete-item {:platform "jira"
                                      :board-id board-id
                                      :item domain-item}))

  {:status 200})
