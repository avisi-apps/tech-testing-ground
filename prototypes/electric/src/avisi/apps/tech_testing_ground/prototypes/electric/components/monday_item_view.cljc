(ns avisi.apps.tech-testing-ground.prototypes.electric.components.monday-item-view
  (:import [hyperfiddle.electric Pending])
  ;#?(:cljs (:require-macros [avisi.apps.tech-testing-ground.prototypes.electric.components.jira-item-view :refer [with-reagent]]))
  #?(:clj
     (:require
       [avisi.apps.tech-testing-ground.prototypes.shared.core.hello-world :as shared-core]
       [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]
       [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-link]
       [hyperfiddle.electric :as e]
       [hyperfiddle.electric-dom2 :as dom]
       [avisi.apps.tech-testing-ground.prototypes.electric.db-proxy :as db-proxy]
       ))
  #?(:cljs
     (:require
       ["@atlaskit/dropdown-menu" :default DropdownMenu :refer [DropdownItemGroup DropdownItem]]
       [hyperfiddle.electric :as e]
       [hyperfiddle.electric-dom2 :as dom]
       [avisi.apps.tech-testing-ground.prototypes.electric.react-interop :as interop]
       [missionary.core :as m]
       [avisi.apps.tech-testing-ground.prototypes.electric.db-proxy :as db-proxy]
       #_[avisi.apps.tech-testing-ground.prototypes.electric.components.atlas-kit.dropdown-menu :as dropdown-menu])))

(def jira-light "#FFFFFF")
(def jira-dark-blue "#172B4D")
(def jira-light-blue "#0052CC")

#?(:cljs
   (defn DropdownMenuComp [{:keys [monday-item-id jira-item-id] :as item-link}]
     [:> DropdownMenu {}
      [:> DropdownItemGroup {}
       (if item-link
         [:> DropdownItem
          [:div (interop/action-dispatch-props {:label "delete-item-link"})
           "Delete ItemLink"]]
         [:> DropdownItem
          {:id "create-item-link-button"
           :onClick (fn [] (js/AP.dialog.create #js {:key "create-item-link-modal-module-key"}))}
          "Create ItemLink"])]]))

(e/defn ItemView [board-id item-id]
  (e/client
    (try

      (let [react-events (new (interop/react-event-subscription dom/node))]

        (when react-events
          (case (.-action (.-detail react-events))
            "delete-item-link" (e/server (new db-proxy/delete-item-link {:board-id board-id :platform "jira" :item {:id item-id}})))))

      (let [{:keys [monday-item-id] :as item-link}
            (e/server (new db-proxy/get-item-link-by-id {:platform "jira" :board-id board-id :item-id item-id}))]

        (dom/div
          (dom/style
            {:height "100%"
             :background-color jira-dark-blue})
          (dom/div
            (dom/style {:color "#FFFFFF"
                        :display "flex"
                        :padding "5px"
                        :justify-content "flex-end"})
            (interop/with-reagent DropdownMenuComp item-link))
          (dom/div
            (dom/style
              {
               :color jira-light
               :font-family "sans-serif"
               :text-align "center"
               :height "100%"
               :padding-top "30%"})
            (if monday-item-id
              (dom/text (str "This item is linked to monday-item: " monday-item-id))
              (dom/text "This item hasn't been linked to a monday-item yet.")))))
      (catch Pending e
        (dom/style {:background-color "yellow"})))))
