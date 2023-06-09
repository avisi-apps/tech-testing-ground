(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.item-view.jira-item-view
  (:require
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.button :as button]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.dropdown-menu :as dropdown]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.icons.more :as more]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.current-app :as current-app]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.domain :as domain]
    [clojure.edn :as edn]
    [com.fulcrologic.fulcro-css.css-injection :as inj]
    [com.fulcrologic.fulcro-css.localized-dom :as dom]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.data-fetch :as df]
    [goog.object :as gobj]))

(def jira-light "#FFFFFF")

(def jira-dark-blue "#172B4D")

(def jira-light-blue "#0052CC")

(defsc ItemLinkView
  [this
   {{:keys [item-id board-id]} :selected-item
    {:keys [monday-item-id]} :item-link}]
  {:query [{:item-link (comp/get-query domain/ItemLink)} {:selected-item (comp/get-query domain/SelectedItem)}]
   :css
     [[:.background
       {:background-color jira-dark-blue
        :height "100%"
        :width "100%"}]
      [:.dropdown-container
       {:display "flex"
        :padding "5px"
        :justify-content "flex-end"}]
      [:.dropdown
       {:color "green"
        :display "flex"
        :justify-content "flex-start"}]
      [:.item-view-body
       {:height "100%"
        :font-family "sans-serif"
        :text-align "center"
        :padding-top "20%"
        :color jira-light}]]}
  (dom/div
    :.background
    (inj/style-element {:component ItemLinkView})
    (dom/div
      :.dropdown-container
      (dropdown/ui-dropdown-menu
        {:trigger
           (fn [trigger-props]
             (gobj/add trigger-props "iconBefore" (more/ui-more))
             (gobj/add trigger-props "ref" (gobj/get trigger-props "triggerRef"))
             (button/ui-button trigger-props))}
        (dropdown/ui-dropdown-item-group
          {}
          (if monday-item-id
            (dropdown/ui-dropdown-item
              {:onClick
                 #(comp/transact!
                     this
                     [(domain/delete-item-link
                        {:platform "jira"
                         :board-id board-id
                         :item {:id item-id}})])}
              "Delete ItemLink")
            (dropdown/ui-dropdown-item
              {:onClick (fn [] (js/AP.dialog.create #js {:key "create-item-link-modal-module-key"}))}
              "Create ItemLink")))))
    (dom/div
      :.item-view-body
      (dom/p
        (if monday-item-id
          (str "This item is linked to monday-item: " monday-item-id)
          "This item isn't linked to a monday-item yet")))))

(def ui-item-link-view (comp/factory ItemLinkView))

(defn ^:export init []
  (js/AP.context.getContext
    (fn [res]
      (let [{{{issue-key :key} :issue
              {project-id :id} :project}
               :jira}
              (js->clj res :keywordize-keys true)]
        (current-app/initialize-app
          {:root ItemLinkView
           :client-did-mount
             (fn [app]
               (comp/transact!
                 app
                 [(domain/select-item
                    {:item-id issue-key
                     :board-id (edn/read-string project-id)})])
               (df/load!
                 app
                 :item-link
                 domain/ItemLink
                 {:params
                    {:platform "jira"
                     :board-id (edn/read-string project-id)
                     :item-id issue-key}})
               (js/AP.events.on
                 "item-link-created"
                 (fn [data] (comp/transact! app [(domain/add-item-link (edn/read-string data))])))
               (js/console.log "Loaded issue-panel"))})))))
