(ns avisi.apps.tech-testing-ground.prototypes.htmx.item-views.monday-create-item-link-modal
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
    [:div
     {:id "create-item-link-modal"
      :_ "on itemLinkCreated call window.mondaySdk().execute('closeAppFeatureModal')"
      :style
        {:width "100%"
         :height "100%"}}
     [:h2 "Create ItemLink"]
     [:form
      {:style
         {:height "80%"
          :display "flex"
          :flex-direction "column"
          :justify-content "space-between"}
       :hx-post "/monday/item-link"
       :hx-target "#create-item-link-modal"
       :hx-vals
         {:platform "monday"
          :board-id board-id
          :monday-item item-id}}
      [:div
       {:style {:margin-top "10vh"}}
       [:h4 "Jira-item"]
       (into [:sl-select {:name "jira-item"}] (mapv item->option available-items))
       [:h4 "Monday-item"]
       [:sl-select
        {:name "monday-item"
         :value item-id
         :disabled true}
        [:sl-option {:value item-id} item-id]]]
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
         :onclick "monday.execute('closeAppFeatureModal')"}
        "Cancel"]]]]))

(defn routes []
  [["/monday"
    {}
    ["/create-item-link-modal"
     {:get
        {:handler
           (fn
             [{{:strs [board-id item-id]} :query-params
               :as req}]
             (formats/monday-page
               (CreateItemLinkModal
                 {:platform "monday"
                  :board-id (edn/read-string board-id)
                  :item-id item-id})))}}]]])
