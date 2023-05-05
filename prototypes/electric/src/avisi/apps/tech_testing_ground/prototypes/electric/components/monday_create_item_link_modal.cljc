(ns avisi.apps.tech-testing-ground.prototypes.electric.components.monday-create-item-link-modal
  #?(:clj
       (:require
         [hyperfiddle.electric :as e]
         [avisi.apps.tech-testing-ground.prototypes.shared.core.board-links :as board-links]
         [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]
         [avisi.apps.tech-testing-ground.prototypes.electric.db-proxy :as db-proxy]))
  #?(:cljs
       (:require
         ["@atlaskit/modal-dialog" :default Modal :refer [ModalTransition ModalBody ModalFooter ModalHeader ModalTitle]]
         ["@atlaskit/button" :default Button]
         ["@atlaskit/form" :refer [ErrorMessage HelperMessage Field FormFooter FormHeader] :default Form]
         ["@atlaskit/select" :refer [CreatableSelect AsyncSelect] :default Select]
         [avisi.apps.tech-testing-ground.prototypes.electric.react-interop :as interop]
         [avisi.apps.tech-testing-ground.prototypes.electric.current-app :as current-app]
         [reagent.core :as r]
         [promesa.core :as p]
         [hyperfiddle.electric :as e]
         [hyperfiddle.electric-dom2 :as dom])))

(defn item->option [{:item/keys [id]}]
  {:label (str id)
   :value (str id)})

#?(:cljs (def monday (js/window.mondaySdk)))

#?(:cljs (def !create-item-link-button (atom nil)))

#?(:cljs (def !target-item (atom nil)))

#?(:cljs
     (defn create-item-link-modal [monday-item-id available-items]
       [:>
        Modal
        {}
        [:> ModalHeader {} [:> ModalTitle {} "Create ItemLink"]]
        [:>
         ModalBody
         {}
         [:div
          {:style {:height "40vh"}}
          [:>
           Field
           {:label "Jira-item"
            :name "jira-item"}
           (fn []
             (r/as-element
               [:>
                Select
                {:options (mapv item->option available-items)
                 :onChange #(reset! !target-item (.-value %))}]))]
          [:>
           Field
           {:label "Monday-item"
            :name "monday-item"}
           (fn []
             (r/as-element
               [:>
                Select
                {:inputValue monday-item-id
                 ; TODO: option not shown when disabled
                 :isDisabled false}]))]]]
        [:>
         ModalFooter
         {}
         [:div
          {:style
             {:margin "12px 0px"
              :width "100%"
              :display "flex"
              :justify-content "space-around"}}
          [:>
           Button
           {:style {:margin "12px 0px"}
            :type "submit"
            :appearance "primary"
            :onClick #(monday.execute "closeAppFeatureModal")}
           [(r/create-class
              {:component-did-mount
                 (fn [_]
                   (->>
                     (.getElementById js/document "create-item-link-button")
                     (reset! !create-item-link-button)))
               :reagent-render (fn [_] [:div {:id "create-item-link-button"} "Submit"])})]]
          [:>
           Button
           {:style {:margin "12px 0px"}
            :type "submit"
            :appearance "primary"
            :onClick #(monday.execute "closeAppFeatureModal")}
           "Cancel"]]]]))

(e/defn CreateItemLinkModal [board-id item-id]
  (e/client
    (let [selected-item {:platform "monday"
                         :board-id board-id
                         :item {:item/id item-id}}
          target-item {:item/id (e/watch !target-item)}
          available-items (e/server
                            (->>
                              {:platform "monday"
                               :board-id board-id}
                              (board-links/get-connected-board)
                              (item-links/get-unlinked-items)))]
      (let [create-item-button (e/watch !create-item-link-button)]
        (when create-item-button
          (dom/on
            create-item-button
            "click"
            (e/fn [_] (e/server (db-proxy/create-item-link selected-item target-item))))))
      (interop/with-reagent create-item-link-modal item-id available-items))))

#?(:cljs
     (defn get-monday-context []
       (->
         (monday.get "context")
         (p/then
           #(->
               %
               (js->clj :keywordize-keys true)
               (:data))))))
#?(:cljs
     (defn ^:export ^:dev/after-load init! []
       (p/let [{board-id :boardId
                item-id :itemId}
                 (get-monday-context)]
         (current-app/initialize-app
           (e/boot (binding [dom/node js/document.body] (CreateItemLinkModal. board-id (str item-id))))))))
