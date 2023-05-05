(ns avisi.apps.tech-testing-ground.prototypes.htmx.item-views.jira-create-item-link-modal
  (:require
    [avisi.apps.tech-testing-ground.prototypes.htmx.formats :as formats]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.board-links :as board-links]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]
    [clojure.edn :as edn]))

(defn item->option [{:item/keys [id]}] [:sl-option {:value id} id])
(defn CreateItemLinkModal
  [{:keys [platform board-id item-id]
    :as item}]
  (let [available-items (->>
                          item
                          (board-links/get-connected-board)
                          (item-links/get-unlinked-items))]
    [:sl-card
     {:id "create-item-link-modal"
      :_ "on itemLinkCreated call AP.events.emit('itemLinkCreated') AP.dialog.close()"
      :style
        {:width "100%"
         :height "100%"}}
     [:h2 {:slot "header"} "Create ItemLink"]
     [:form
      {:hx-post "/jira/item-link"
       :hx-target "#create-item-link-modal"
       :hx-vals
         {:platform "jira"
          :board-id board-id
          :jira-item item-id}}
      [:h4 "Jira-item"]
      [:sl-select
       {:name "jira-item"
        :value item-id
        :disabled true}
       [:sl-option {:value item-id} item-id]]
      [:h4 "Monday-item"]
      (into [:sl-select {:name "monday-item"}] (mapv item->option available-items))
      [:div
       {:style
          {:padding "1em"
           :padding-top "2em"
           :display "flex"
           :justify-content "space-around"}}
       [:sl-button
        {:type "submit"
         :variant "primary"}
        "Submit"]
       [:sl-button
        {:type "cancel"
         :variant "primary"
         :onclick "AP.dialog.close()"}
        "Cancel"]]]]))

(defn routes []
  [["/jira"
    {}
    ["/create-item-link-modal"
     {:get
        {:handler
           (fn
             [{{:strs [board-id item-id]} :query-params
               :as req}]
             (formats/atlas-page
               (CreateItemLinkModal
                 {:platform "jira"
                  :board-id (edn/read-string board-id)
                  :item-id item-id})))}}]]])
