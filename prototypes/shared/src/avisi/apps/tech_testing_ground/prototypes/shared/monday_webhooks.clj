(ns avisi.apps.tech-testing-ground.prototypes.shared.monday-webhooks
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.boards :as boards]
    [avisi.apps.tech-testing-ground.prototypes.shared.domain :as domain]
    [avisi.apps.tech-testing-ground.prototypes.shared.jira :as jira]
    [avisi.apps.tech-testing-ground.prototypes.shared.database :as db]
    [avisi.apps.tech-testing-ground.prototypes.shared.monday :as monday]
    [avisi.apps.tech-testing-ground.prototypes.shared.propagate-change :as propagate]
    [malli.core :as m]
    [malli.transform :as mt]))

(defmulti webhook-req->monday-item
  (fn [req] (get-in req [:body-params :event :type])))

(defmethod webhook-req->monday-item "create_pulse" [req]
  (let [id (get-in req [:body-params :event :pulseId])
        name (get-in req [:body-params :event :pulseName])]
    {:item/id id
     :item/name name}))

(defmethod webhook-req->monday-item "update_name" [req]
  (let [id (get-in req [:body-params :event :pulseId])
        name (get-in req [:body-params :event :value :name])]
    {:item/id id
     :item/name name}))

(defmethod webhook-req->monday-item "update_column_value" [req]
  (let [id (get-in req [:body-params :event :pulseId])
        status (get-in req [:body-params :event :value :label :text])]
    {:item/id id
     :item/status status}))

(defmethod webhook-req->monday-item "delete_pulse" [req]
  (let [id (get-in req [:body-params :event :itemId])]
    {:item/id id}))

(defn webhook-req->board-id [req]
  (get-in req [:body-params :event :boardId]))

(defn webhook-req->action [req]
  (case (get-in req [:body-params :event :type])
    "create_pulse" :create
    "update_column_value" :update
    "update_name" :update
    "delete_pulse" :delete))

(defn webhook-req->propagation-args [req]
  {:source/platform "monday"
   :source/board-id (webhook-req->board-id req)
   :source/item (-> (webhook-req->monday-item req) (domain/monday-item->domain-item))
   :action (webhook-req->action req)})

(def propagate-action
  (propagate/propagate-action-fn webhook-req->propagation-args))

(defn webhook-handler [req]
  (def _mwh-req req)
  (propagate-action req))

(comment

  (webhook-req->propagation-args _mwh-req)

  )
