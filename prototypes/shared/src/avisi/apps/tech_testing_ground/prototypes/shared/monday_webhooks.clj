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

; req info extraction

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

(defn webhook-req->monday-board [req]
  (let [id (get-in req [:body-params :event :boardId])]
    {:board/id id}))

(defn webhook-req->board-link [req]
  (let [{:board/keys [id]} (webhook-req->monday-board req)]
    (db/get-board-link {:platform "monday"
                        :board-id id})))

(defn webhook-req->item-link [req]
  (let [{:keys [board-link-id]} (webhook-req->board-link req)
        {monday-item-id :item/id} (webhook-req->monday-item req)]
    (db/get-item-link {:board-link-id board-link-id :monday-item-id monday-item-id})))

(comment
  (webhook-req->monday-item _iu-req)
  (webhook-req->monday-board _iu-req)
  (webhook-req->board-link _iu-req)
  (webhook-req->item-link _iu-req)
  )

; webhooks handlers
(defmulti webhook-handler
  (fn [req]
    (def _req req)
    (get-in req [:body-params :event :type])))

; item created
(defmethod webhook-handler "create_pulse" [req]
  (def _ic-req req)

  (let [{board-id :board/id} (webhook-req->monday-board req)
        domain-item (-> (webhook-req->monday-item req)
                      (domain/monday-item->domain-item))]

    (propagate/propagate-add-item {:platform "monday"
                                   :board-id board-id
                                   :item domain-item}))

  {:status 200})

; status updated
(defmethod webhook-handler "update_column_value" [req]
  (def _iu-req req)

  (let [{board-id :board/id} (webhook-req->monday-board req)
        domain-item (-> (webhook-req->monday-item req)
                      (domain/monday-item->domain-item))]

    (prn domain-item)

    (propagate/propagate-update-item {:platform "monday"
                                      :board-id board-id
                                      :item domain-item}))
  {:status 200}
  )

; name updated
(defmethod webhook-handler "update_name" [req]
  (def _iu-req req)

  (let [{board-id :board/id} (webhook-req->monday-board req)
        domain-item (-> (webhook-req->monday-item req)
                      (domain/monday-item->domain-item))]

    (propagate/propagate-update-item {:platform "monday"
                                      :board-id board-id
                                      :item domain-item}))

  {:status 200})

; item deleted
(defmethod webhook-handler "delete_pulse" [req]
  (def _id-req req)

  (let [{board-id :board/id} (webhook-req->monday-board req)
        domain-item (-> (webhook-req->monday-item req)
                      (domain/monday-item->domain-item))]

    (propagate/propagate-delete-item {:platform "monday"
                                      :board-id board-id
                                      :item domain-item})))
