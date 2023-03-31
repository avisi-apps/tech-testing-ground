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

(defmulti webhook-event->monday-item (fn [{:keys [type]}] type))

(defmethod webhook-event->monday-item "create_pulse"
  [{id :pulseId name :pulseName}]
  {:item/id id
   :item/name name})

(defmethod webhook-event->monday-item "update_name"
  [{id :pulseId {name :name} :value}]
  {:item/id id
   :item/name name})

(defmethod webhook-event->monday-item "update_column_value"
  [{id :pulseId {{status :text} :label} :value}]
  {:item/id id
   :item/status status})

(defmethod webhook-event->monday-item "delete_pulse"
  [{id :itemId}]
  {:item/id id})

(def webhook-event-type->domain-action
  {"create_pulse" :create
   "update_column_value" :update
   "update_name" :update
   "delete_pulse" :delete})

(defn webhook-req->propagation-args [{{{event-type :type board-id :boardId :as event} :event} :body-params :as req}]
  (def _r req)
  {:source/platform "monday"
   :source/board-id board-id
   :source/item
   (->
     (webhook-event->monday-item event)
     (domain/monday-item->domain-item))
   :action (webhook-event-type->domain-action event-type)})

(def propagate-action (propagate/propagate-action-fn webhook-req->propagation-args))

(defn webhook-handler [req] (propagate-action req))

(comment

  (let [{{{event-type :type board-id :boardId} :event :as event} :body-params :as req} _r]

    (webhook-event->monday-item event)
    )

  )
