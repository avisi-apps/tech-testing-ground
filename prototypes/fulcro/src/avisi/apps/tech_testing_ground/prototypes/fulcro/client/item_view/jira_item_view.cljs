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
    [com.fulcrologic.fulcro.mutations :as m]
    [com.fulcrologic.fulcro.mutations :refer [defmutation]]
    [goog.object :as gobj]))

(def jira-light "#FFFFFF")
(def jira-dark-blue "#172B4D")
(def jira-light-blue "#0052CC")

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
(defsc ItemLinkView
  [this
   {{:keys [item-id board-id]} :selected-item
    {:keys [monday-item-id]} :item-link}]
  {:query [:monday-item-id :jira-item-id]
   :css [[:.dropdown {:color "green"
                      :display "flex"
                      :justify-content "flex-start"}]]}
  (dom/div {:style
            {:background-color jira-dark-blue
             :height "100%"
             :width "100%"}}
           (dom/div {:style {:color "#FFFFFF"
                             :display "flex"
                             :padding "5px"
                             :justify-content "flex-end"}}
                    (dropdown/ui-dropdown-menu
                      {:trigger
                       (fn [trigger-props]
                         (gobj/add trigger-props "iconBefore" (more/ui-more))
                         (gobj/add trigger-props "ref" (gobj/get trigger-props "triggerRef"))
                         (button/ui-button trigger-props))}
                      (dropdown/ui-dropdown-item-group {}
                                                       (if monday-item-id
                                                         (dropdown/ui-dropdown-item
                                                           {:onClick #(comp/transact! this [(delete-item-link {:platform "jira" :board-id board-id :item {:id item-id}})])}
                                                           "Delete ItemLink")
                                                         (dropdown/ui-dropdown-item
                                                           {:onClick (fn [] (js/AP.dialog.create #js {:key "create-item-link-modal-module-key"}))} "Create ItemLink")))))
           (dom/div {:style
                     {:height "100%"
                      :font-family "sans-serif"
                      :text-align "center"
                      :padding-top "20%"
                      :color jira-light}}
                    (dom/p (if monday-item-id
                             (str "This item is linked to monday-item: " monday-item-id)
                             "This item isn't linked to a monday-item yet")))))

(def ui-item-link-view (comp/factory ItemLinkView))

(defsc Root
  [this
   {:keys [selected-item item-link]
    :as props}]
  (dom/div
    {:style
     {:height "100%"}}
    (inj/style-element {:component Root})
    (ui-item-link-view {:selected-item selected-item
                        :item-link item-link})))

(defn ^:export init []
  (js/AP.context.getContext
    (fn [res]
      (let [{{{issue-key :key} :issue {project-id :id} :project} :jira}
            (js->clj res :keywordize-keys true)]
        (current-app/initialize-app
          {:root Root
           :client-did-mount
           (fn [app]
             (comp/transact! app [(select-item {:item-id issue-key :board-id (edn/read-string project-id)})])
             (df/load! app :item-link domain/ItemLink
                       {:params {:platform "jira" :board-id (edn/read-string project-id) :item-id issue-key}})
             (js/AP.events.on "item-link-created"
                              (fn [data]
                                (comp/transact! app [(add-item-link (edn/read-string data))])))
             (js/console.log "Loaded issue-panel"))})))))
