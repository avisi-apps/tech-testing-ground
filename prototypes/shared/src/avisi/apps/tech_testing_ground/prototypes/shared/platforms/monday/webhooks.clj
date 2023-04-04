(ns avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.webhooks
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.domain-mapping :as domain]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.propagate-change :as propagate]))

(defmulti webhook-event->monday-item (fn [{:keys [type]}] type))

(defmethod webhook-event->monday-item "create_pulse"
  [{id :pulseId name :pulseName}]
  {:item/id id
   :item/name name})

(defmethod webhook-event->monday-item "update_name"
  [{id :pulseId {name :name} :value :as req}]
  {:item/id id
   :item/name name})

(defmethod webhook-event->monday-item "update_column_value"
  [{id :pulseId {{status :text} :label} :value :as req}]
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

(defn webhook-req->propagation-args [{{{event-type :type board-id :boardId :as event} :event} :body-params}]
  {:platform "monday"
   :board-id board-id
   :item
   (->
     (webhook-event->monday-item event)
     (domain/monday-item->domain-item))
   :action (webhook-event-type->domain-action event-type)})

(def propagate-action (propagate/propagate-action-fn webhook-req->propagation-args))

(defn webhook-handler [req]
  (try
    (propagate-action req)
    {:status 200}
    (catch Exception _ {:status 500})))
