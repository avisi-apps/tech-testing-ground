(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.item-view.monday-create-item-link-modal
  (:require
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.button :as button]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.form :as form]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.modal :as modal-dialog]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.select :as select]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.current-app :as current-app]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.domain :as domain]
    [com.fulcrologic.fulcro-css.css-injection :as inj]
    [com.fulcrologic.fulcro-css.localized-dom :as dom]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.data-fetch :as df]
    [com.fulcrologic.fulcro.mutations :as m]
    [com.fulcrologic.fulcro.mutations :refer [defmutation]]
    [promesa.core :as p]))

(def monday (js/window.mondaySdk))
(defn get-monday-context []
  (-> (monday.get "context")
      (p/then #(-> %
                   (js->clj :keywordize-keys true)
                   (:data)))))
(defn item->option [{:item/keys [id]}]
  {:label (str id) :value (str id)})

(defmutation create-item-link
  [_]
  (action [{:keys [app]}]
          (df/set-load-marker! app :item-link :creating))
  (remote [env] (->
                  env
                  (m/with-server-side-mutation 'create-item-link)))
  (ok-action [{:keys [app]}]
             (df/remove-load-marker! app :item-link)
             (monday.execute "closeAppFeatureModal")))
(defsc ItemLinkModal [this {:keys [selected-item available-items] :as props}]
  {:query [{:selected-item (comp/get-query domain/SelectedItem)}
           {:available-items (comp/get-query domain/Item)}]
   :initLocalState (fn [_ _] {:jira-item-id ""})
   :css
   [[:.modal-body {:height "40h"}]
    [:.button-container
     {:margin "12px 0px"
      :display "flex"
      :justify-content "space-around"}]]}
  (let [jira-item-id (comp/get-state this :jira-item-id)
        monday-item-id (:item-id selected-item)
        board-id (:board-id selected-item)]
    (inj/style-element {:component ItemLinkModal})
    (modal-dialog/ui-modal {}
                           (modal-dialog/ui-modal-header {}
                                                         (modal-dialog/ui-modal-title {} "Create ItemLink"))
                           (modal-dialog/ui-modal-body {}
                                                       (dom/div :.modal-body
                                                                (form/ui-field
                                                                  {:label "Jira-item"
                                                                   :name "jira-item"}
                                                                  (fn []
                                                                    (select/ui-select
                                                                      {:options (mapv item->option available-items)
                                                                       :onChange (fn [e] (comp/update-state! this assoc :jira-item-id (.-value e)))})))
                                                                (form/ui-field
                                                                  {:label "Monday-item"
                                                                   :name "monday-item"}
                                                                  (fn []
                                                                    (select/ui-select
                                                                      {:inputValue monday-item-id
                                                                       :isDisabled true})))))
                           (modal-dialog/ui-modal-footer {}
                                                         (dom/div :.button-container
                                                                  (button/ui-button
                                                                    {:style {:margin "12px 0px"}
                                                                     :type "submit"
                                                                     :appearance "primary"
                                                                     :onClick
                                                                     #(comp/transact! this [(create-item-link
                                                                                              {:source
                                                                                               {:platform "monday" :board-id board-id :item {:item/id (str monday-item-id)}}
                                                                                               :target {:item/id jira-item-id}})])}
                                                                    "Submit")
                                                                  (button/ui-button
                                                                    {:style {:margin "12px 0px"}
                                                                     :type "submit"
                                                                     :appearance "primary"
                                                                     :onClick #(monday.execute "closeAppFeatureModal")}
                                                                    "Cancel"))))))

(def ui-item-link-modal (comp/factory ItemLinkModal))

(defn ^:export init []
  (p/let [{board-id :boardId item-id :itemId} (get-monday-context)]
    (current-app/initialize-app
      {:root ItemLinkModal
       :client-did-mount
       (fn [app]
         (comp/transact! app [(domain/select-item {:item-id item-id :board-id board-id})])
         (df/load! app :available-items domain/Item {:params {:platform "monday" :board-id board-id}})
         (js/console.log "Loaded modal"))})))
