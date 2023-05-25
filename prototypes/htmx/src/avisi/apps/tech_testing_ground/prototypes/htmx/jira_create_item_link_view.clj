(ns avisi.apps.tech-testing-ground.prototypes.htmx.jira-create-item-link-view
  (:require
    [avisi.apps.tech-testing-ground.prototypes.htmx.formats :as formats]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.board-links :as board-links]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]
    [clojure.edn :as edn]))

(defn item->option [{:item/keys [id]}]
  [:sl-option {:value id} id])
(defn CreateItemLinkModal [{:keys [platform board-id item-id] :as item}]
  (let [available-items (->> item
                             (board-links/get-connected-board)
                             (item-links/get-unlinked-items))]
    (def _i item)
    (def _ai available-items)
    [:sl-card
     {:id "create-item-link-modal"
      :style
      {:width "100%"
       :height "100%"
       :background-color "red"}}
     [:h2 {:slot "header"} "Create ItemLink"]
     [:h3 "Jira Item"]
    [:sl-select {:value item-id :disabled true}
     [:sl-option {:value item-id} item-id] ]
     [:h3 "Monday Item"]
     (into [:sl-select] (mapv item->option available-items))]))

(comment

  (into [:x] [:a :b :c])
  (mapv item->option _ai)

  )

(defn routes []
  [["/jira-create-item-link-modal"
    {:get
     {:handler
      (fn [{:keys [request-method]
            {:strs [project-id issue-key]} :query-params :as req}]
        (def _r3 req)
        (formats/page (CreateItemLinkModal {:platform "jira"
                                            :board-id (edn/read-string project-id)
                                            :item-id issue-key})))}}]
   ])
