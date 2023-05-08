(ns avisi.apps.tech-testing-ground.prototypes.electric.db-proxy
  #?(:clj
     (:require
       [hyperfiddle.electric :as e]
       [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]))
  (:import (hyperfiddle.electric Pending)))

#?(:clj (defonce !item-link (atom nil)))
(e/def item-link (e/server (e/watch !item-link)))

(defn get-item-link-by-id [{:keys [platform board-id item-id]}]
  (let [item-link (e/server (item-links/get-item-link {:platform platform :board-id board-id :item-id item-id}))]
    (e/server (reset! !item-link item-link))
    (e/watch !item-link)))

(defn delete-item-link [{board-id :board-id
                         platform :platform
                         {id :id} :item :as item}]
  (item-links/delete-item-link item)
  (e/server (reset! !item-link nil)))

(comment

  (item-link/get-item-link {:platform "jira" :board-id board-id :item-id item-id})
  )
