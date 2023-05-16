(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.domain
  (:require
    [com.fulcrologic.fulcro.components :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

(defsc ItemLink
  [_
   {:keys [monday-item-id jira-item-id]
    :as props}]
  {:query [:monday-item-id :jira-item-id]})
(defsc SelectedItem [_ _]
  {:query [:item-id :board-id]})

(defsc Item [_ _]
  {:query [:item/id]})
(defmutation select-item
             [{:item/keys [item-id board-id] :as item}]
             (action [{:keys [state]}]
                     (swap! state assoc :selected-item item)))
(defmutation add-item-link
             [{:keys [jira-item-id monday-item-id] :as item-link}]
             (action [{:keys [state]}]
                     (swap! state assoc :item-link item-link)))

(defmutation delete-item-link
             [{:keys [platform board-id] {:item/keys [id]} :item :as linked-item}]
             (action [{:keys [state]}]
                     (swap! state dissoc :item-link))
             (remote [env] (->
                             env
                             (m/with-server-side-mutation 'delete-item-link))))
