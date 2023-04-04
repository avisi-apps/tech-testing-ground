(ns avisi.apps.tech-testing-ground.prototypes.shared.core.propagate-change
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.core.board-links :as board-links]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.boards :as boards]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]))

(defmulti propagate-action (fn [{:keys [action]}] action))

(defmethod propagate-action :create
  [{:keys [platform board-id item]
    :as   source}]
  (when (and (board-links/sync-by-default? source) (not (item-links/get-item-representation source)))
    (let [to-be-connected (->
                            (board-links/get-connected-board source)
                            (boards/add-item item))]
      (item-links/create-item-link source to-be-connected))))

(defmethod propagate-action :update
  [{:keys [platform board-id item]
    :as   source}]
  (when-not (= item (item-links/get-item-representation source))
    (let [target-board (board-links/get-connected-board source)
          target-item (item-links/get-connected-item source)]
      (boards/update-item target-board target-item)
      (item-links/update-item-representation source))))

(defmethod propagate-action :delete
  [{:keys [platform board-id item]
    :as   source}]
  (let [target-board (board-links/get-connected-board source)
        target-item (item-links/get-connected-item source)]
    (when target-item (boards/delete-item target-board target-item) (item-links/delete-item-link source))))

(defn propagate-action-fn [webhook-req->propagation-args]
  (fn [req]
    (try
      (->
        req
        (webhook-req->propagation-args)
        (propagate-action))
      (catch Exception e (println "An error occured") (println (ex-message e))))))
