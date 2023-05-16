(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.item-view.jira-create-item-link-modal
  (:require
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.button :as button]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.form :as form]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.modal :as modal-dialog]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.select :as select]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.current-app :as current-app]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.domain :as domain]
    [clojure.edn :as edn]
    [com.fulcrologic.fulcro-css.css-injection :as inj]
    [com.fulcrologic.fulcro-css.localized-dom :as dom]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.data-fetch :as df]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

(defn item->option [{:item/keys [id]}]
  {:label (str id) :value (str id)})

(defmutation create-item-link
  [{{monday-item-id :item/id} :target {{jira-item-id :item/id} :item} :source}]
  (action [{:keys [state]}]
          (swap! state assoc :created {:monday-item-id monday-item-id :jira-item-id jira-item-id}))
  (remote [env] (->
                  env
                  (m/with-server-side-mutation 'create-item-link)))
  (ok-action [{:keys [state]}]
             (->> (get @state :created)
                  (str)
                  (js/AP.events.emit "item-link-created"))
             (js/AP.dialog.close)))

(defsc ItemLinkModal [this {:keys [selected-item available-items]}]
  {:query [{:selected-item (comp/get-query domain/SelectedItem)}
           {:available-items (comp/get-query domain/Item)}
           [df/marker-table :item-link]]
   :initLocalState
   (fn [_ _] {:monday-item-id ""})
   :css
   [[:.modal-body {:height "40h"}]
    [:.button-container
     {:margin "12px 0px"
      :display "flex"
      :justify-content "space-around"}]]}

  (let [monday-item-id (comp/get-state this :monday-item-id)
        jira-item-id (:item-id selected-item)
        board-id (:board-id selected-item)]
    (inj/style-element {:component ItemLinkModal})
    (modal-dialog/ui-modal
      {}
      (modal-dialog/ui-modal-header
        {}
        (modal-dialog/ui-modal-title {} "Create ItemLink"))
      (modal-dialog/ui-modal-body
        {}
        (dom/div :.modal-body
          (form/ui-field
            {:label "Jira-item"
             :name "jira-item"}
            (fn []
              (select/ui-select
                {:inputValue jira-item-id
                 :isDisabled true})))

          (form/ui-field
            {:label "Monday-item"
             :name "monday-item"}
            (fn []
              (select/ui-select
                {:options (mapv item->option available-items)
                 :onChange (fn [e] (comp/update-state! this assoc :monday-item-id (.-value e)))})))))

      (modal-dialog/ui-modal-footer
        {}
        (dom/div
          {:style {:margin "12px 0px"
                   :width "100%"
                   :display "flex"
                   :justify-content "space-around"}}
          (button/ui-button
            {:style {:margin "12px 0px"}
             :type "submit"
             :appearance "primary"
             :onClick
             #(comp/transact! this [(create-item-link
                                      {:source
                                       {:platform "jira" :board-id board-id :item {:item/id jira-item-id}}
                                       :target {:item/id monday-item-id}})])}
            "Submit")
          (button/ui-button
            {:style {:margin "12px 0px"}
             :type "submit"
             :appearance "primary"
             :onClick #(js/AP.dialog.close)}
            "Cancel"))))))

(def ui-item-link-modal (comp/factory ItemLinkModal))

(defn ^:export init []
  (js/AP.context.getContext
    (fn [res]
      (let [{{{issue-key :key} :issue {project-id :id} :project} :jira}
            (js->clj res :keywordize-keys true)]
        (current-app/initialize-app
          {:root ItemLinkModal
           :client-did-mount
           (fn [app]
             (comp/transact! app [(domain/select-item {:item-id issue-key :board-id (edn/read-string project-id)})])
             (df/load! app :available-items domain/Item
                       {:params {:platform "jira" :board-id (edn/read-string project-id)}})
             (js/console.log "Loaded modal"))})))))
