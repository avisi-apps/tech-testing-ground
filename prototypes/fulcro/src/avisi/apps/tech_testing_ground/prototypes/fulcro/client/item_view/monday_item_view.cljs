(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.item-view.monday-item-view
  (:require
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.button :as button]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.dropdown-menu :as dropdown]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.icons.more :as more]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.current-app :as current-app]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.domain :as domain]
    [com.fulcrologic.fulcro-css.css-injection :as inj]
    [com.fulcrologic.fulcro-css.localized-dom :as dom]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.data-fetch :as df]
    [goog.object :as gobj]
    [promesa.core :as p]))

(def monday-purple "#6161FF")
(def monday-dark "#181B34")
(def monday-light "#F0F3FF")
(def monday (js/window.mondaySdk))
(defn get-monday-context []
  (-> (monday.get "context")
      (p/then #(-> %
                   (js->clj :keywordize-keys true)
                   (:data)))))
(defsc ItemLinkView
  [this
   {{:keys [item-id board-id]} :selected-item
    {:keys [jira-item-id]} :item-link}]
  {:query [{:item-link (comp/get-query domain/ItemLink)}
           {:selected-item (comp/get-query domain/SelectedItem)}]
   :css [[:.background
          {:background-color monday-light
           :height "50vh"
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
           :text-align "center"
           :padding-top "20%"
           :color monday-dark}]]}
  (dom/div :.background
           (inj/style-element {:component ItemLinkView})
           (dom/div :.dropdown-container
                    (dropdown/ui-dropdown-menu
                      {:trigger
                       (fn [trigger-props]
                         (gobj/add trigger-props "iconBefore" (more/ui-more))
                         (gobj/add trigger-props "ref" (gobj/get trigger-props "triggerRef"))
                         (button/ui-button trigger-props))}
                      (dropdown/ui-dropdown-item-group {}
                                                       (if jira-item-id
                                                         (dropdown/ui-dropdown-item
                                                           {:onClick #(comp/transact! this [(domain/delete-item-link {:platform "monday" :board-id board-id :item {:id item-id}})])}
                                                           "Delete ItemLink")
                                                         (dropdown/ui-dropdown-item
                                                           {:onClick
                                                            (fn []
                                                              (p/let [{board-id :boardId item-id :itemId} (get-monday-context)]
                                                                (-> (monday.execute "openAppFeatureModal" #js {:urlPath "monday-create-item-link-modal" :height 600 :width 400})
                                                                    (p/then #(df/load! @current-app/current-app :item-link domain/ItemLink
                                                                                       {:params {:platform "monday" :board-id board-id :item-id (str item-id)}})))))}
                                                           "Create ItemLink")))))
           (dom/div :.item-view-body
                    (dom/p (if jira-item-id
                             (str "This item is linked to jira-item: " jira-item-id)
                             (str "This item has not been linked to a jira-item yet"))))))
(def ui-item-link-view (comp/factory ItemLinkView))

(defn ^:export init []
  (p/let [{board-id :boardId item-id :itemId} (get-monday-context)]
    (current-app/initialize-app
      {:root ItemLinkView
       :client-did-mount
       (fn [app]
         (comp/transact! app [(domain/select-item {:item-id item-id :board-id board-id})])
         (df/load! app :item-link domain/ItemLink
                   {:params {:platform "monday" :board-id board-id :item-id item-id}})
         (js/console.log "Loaded issue-panel"))})))
