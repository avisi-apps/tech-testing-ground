(ns avisi.apps.tech-testing-ground.prototypes.fulcro.server.mutations
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.core.boards :as boards]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-link]
    [com.wsscode.pathom3.connect.operation :as pco]))

(pco/defmutation create-item-link [env {:keys [source target]}]
  {::pco/op-name 'create-item-link}
  (->
    source
    #_(update :item (partial boards/get-item-by-id source))
    (item-link/create-item-link target)))

(pco/defmutation delete-item-link [env {:keys [platform board-id] {:item/keys [id]} :item :as linked-item}]
  {::pco/op-name 'delete-item-link}
  (item-link/delete-item-link linked-item))

(def mutations [create-item-link delete-item-link])
