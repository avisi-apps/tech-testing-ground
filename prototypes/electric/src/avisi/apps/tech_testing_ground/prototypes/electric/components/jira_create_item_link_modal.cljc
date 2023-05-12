(ns avisi.apps.tech-testing-ground.prototypes.electric.components.jira-create-item-link-modal
  (:require
    #?(:clj [avisi.apps.tech-testing-ground.prototypes.shared.core.hello-world :as shared-core])
    #?(:cljs ["@atlaskit/modal-dialog" :default Modal :refer [ModalTransition ModalBody ModalFooter ModalHeader ModalTitle]])
    #?(:cljs ["@atlaskit/button" :default Button])
    #?(:cljs ["@atlaskit/form" :refer [ErrorMessage HelperMessage Field FormFooter FormHeader] :default Form])
    #?(:cljs ["@atlaskit/select" :refer [CreatableSelect AsyncSelect] :default Select])
    #?(:cljs [avisi.apps.tech-testing-ground.prototypes.electric.react-interop :as interop])
    #?(:cljs [reagent.core :as r])
    #?(:cljs [missionary.core :as m])
    #?(:cljs [goog.object :as gobj])

    #?(:clj [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links])
    #?(:clj [avisi.apps.tech-testing-ground.prototypes.shared.core.board-links :as board-links])
    #?(:clj [avisi.apps.tech-testing-ground.prototypes.electric.db-proxy :as db-proxy])

    [hyperfiddle.electric :as e]
    [hyperfiddle.electric-dom2 :as dom]))

(defn item->option [{:item/keys [id]}]
  {:label (str id) :value (str id)})

#?(:cljs
   (defn create-item-link-modal [jira-item-id
                                 available-items]
     (let [selected (atom {:jira-item-id jira-item-id :monday-item-id nil})]
       [:> Modal {}
        [:> ModalHeader
         {}
         [:> ModalTitle {} "Create ItemLink"]]
        [:> ModalBody
         {}

         [:div
          {:style {:height "40vh"}}

          [:> Field
           {:label "Jira-item"
            :name "jira-item"}
           (fn []
             (r/as-element
               [:> Select
                {:inputValue jira-item-id
                 ; TODO: option not shown when disabled
                 :isDisabled false}]))]

          [:> Field
           {:label "Monday-item"
            :name "monday-item"}
           (fn []
             (r/as-element
               [:> Select
                {:options (mapv item->option available-items)
                 :onChange #(swap! selected assoc :monday-item-id (.-value %))}]))]]]
        [:> ModalFooter
         {}
         [:div
          {:style {:margin "12px 0px"
                   :width "100%"
                   :display "flex"
                   :justify-content "space-around"}}
          [:> Button
           {:style {:margin "12px 0px"}
            :type "submit"
            :appearance "primary"}

           [:div
            {:id "create-item-link"
             :onClick #(do
                         (->
                           (.getElementById js/document "create-item-link")
                           (.dispatchEvent (js/CustomEvent. "react-event"
                                                            (cljs.core/clj->js {:bubbles true
                                                                                :detail {:action "create-item-link"
                                                                                         :data @selected}}))))
                         (js/AP.dialog.close))}
            "Submit"]]
          [:> Button
           {:style {:margin "12px 0px"}
            :type "submit"
            :appearance "primary"
            :onClick #(js/AP.dialog.close)} "Cancel"]]]])))

(e/defn CreateItemLinkModal [board-id item-id]
  (e/client
    (let [selected-item {:platform "jira" :board-id board-id :item {:item/id item-id}}
          available-items (e/server (->> {:platform "jira" :board-id board-id}
                                         (board-links/get-connected-board)
                                         (item-links/get-unlinked-items)))]

      (let [react-events (->> (m/observe (fn mount [emit!]
                                           (let [f (fn [e] (emit! e))]
                                             (.addEventListener dom/node "react-event" f)
                                             (fn unmount []
                                               (.removeEventListener dom/node "react-event" f)))))
                              (m/reductions {} nil)
                              (new))]
        (when react-events
          (let [event (js->clj (.-detail react-events) :keywordize-keys true)]
            (case (:action event)
              "create-item-link" (let [target-item (->> (get-in event [:data :monday-item-id]) (hash-map :item/id))]
                                   (e/server
                                     (new db-proxy/create-item-link selected-item target-item)))))))



      (interop/with-reagent create-item-link-modal item-id available-items))))
