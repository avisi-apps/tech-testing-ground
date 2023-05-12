(ns avisi.apps.tech-testing-ground.prototypes.electric.db-proxy
  #?(:clj
     (:require
       [hyperfiddle.electric :as e]
       [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]))
  #?(:cljs
     (:require
       [hyperfiddle.electric :as e]))
  (:import (hyperfiddle.electric Pending)))

#?(:clj (defonce !item-link (atom nil)))
(e/def item-link (e/server (e/watch !item-link)))

(e/defn get-item-link-by-id [{:keys [platform board-id item-id]}]
  (let [item-link (e/server (item-links/get-item-link {:platform platform :board-id board-id :item-id item-id}))]
    (e/server (reset! !item-link item-link)))
  item-link)

(e/defn create-item-link [source-item target-item]
  (e/server
    (let [item-link (-> (item-links/create-item-link source-item target-item))]
      (reset! !item-link item-link))))
(e/defn delete-item-link [{board-id :board-id
                           platform :platform
                           {id :id} :item :as item}]
  (item-links/delete-item-link item)
  (e/server (reset! !item-link nil)))
