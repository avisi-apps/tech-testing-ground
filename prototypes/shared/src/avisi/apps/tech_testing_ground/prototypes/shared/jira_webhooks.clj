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
(def path-to-action [:path-params :action])

(defn webhook-req->jira-issue [req]
  {:issue/key (get-in req path-to-issue-key)
   :issue/summary (get-in req path-to-name)
   :issue/status (get-in req path-to-status)})

(defn webhook-req->board-id [req]
  (->
    (get-in req path-to-project-id)
    (edn/read-string)))

(defn webhook-req->action [req]
  (case (get-in req path-to-action)
    "issue_created" :create
    "issue_updated" :update
    "issue_deleted" :delete))

(defn webhook-req->propagation-args [req]
  {:source/platform "jira"
   :source/board-id (webhook-req->board-id req)
   :source/item (-> (webhook-req->jira-issue req) (domain/jira-issue->domain-item))
   :action (webhook-req->action req)})

(def propagate-action
  (propagate/propagate-action-fn webhook-req->propagation-args))

(defn webhook-handler [req]
  (def _jwh-req req)
  (propagate-action req))

(comment

  (webhook-req->propagation-args _jwh-req)

  )
