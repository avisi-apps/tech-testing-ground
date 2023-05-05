(ns avisi.apps.tech-testing-ground.prototypes.electric.components.monday-item-view
  (:import
    [hyperfiddle.electric Pending])
  #?(:clj
       (:require
         [hyperfiddle.electric :as e]
         [avisi.apps.tech-testing-ground.prototypes.electric.db-proxy :as db-proxy]))
  #?(:cljs
       (:require
         ["@atlaskit/dropdown-menu" :default DropdownMenu :refer [DropdownItemGroup DropdownItem]]
         [hyperfiddle.electric :as e]
         [hyperfiddle.electric-dom2 :as dom]
         [avisi.apps.tech-testing-ground.prototypes.electric.react-interop :as interop]
         [avisi.apps.tech-testing-ground.prototypes.electric.current-app :as current-app]
         [reagent.core :as r]
         [promesa.core :as p])))

(def monday-purple "#6161FF")

(def monday-dark "#181B34")

(def monday-light "#F0F3FF")

#?(:cljs (def monday (js/window.mondaySdk)))

#?(:cljs
     (defn get-monday-context []
       (->
         (monday.get "context")
         (p/then
           #(->
               %
               (js->clj :keywordize-keys true)
               (:data))))))

#?(:cljs (defonce !delete-div (atom nil)))

#?(:cljs
     (defn DropdownMenuComp
       [{:keys [monday-item-id jira-item-id]
         :as item-link}]
       [:>
        DropdownMenu
        {}
        [:>
         DropdownItemGroup
         {}
         (if item-link
           [:>
            DropdownItem
            [(r/create-class
               {:component-did-mount
                  (fn [_]
                    (->>
                      (.getElementById js/document "delete-item-link")
                      (reset! !delete-div)))
                :reagent-render (fn [_] [:div {:id "delete-item-link"} "Delete ItemLink"])})]]
           [:>
            DropdownItem
            {:id "create-item-link-button"
             :onClick
               #(monday.execute
                   "openAppFeatureModal"
                   #js
                    {:urlPath "monday-create-item-link-modal"
                     :height 600
                     :width 400})}
            "Create ItemLink"])]]))

(e/defn ItemView [board-id item-id]
  (e/server
    (db-proxy/load-item-link
      {:platform "monday"
       :board-id board-id
       :item-id item-id}))
  (e/client
    (try
      (let [delete-div (e/watch !delete-div)]
        (when delete-div
          (dom/on
            delete-div
            "click"
            (e/fn [e]
              (e/server
                (db-proxy/delete-item-link
                  {:board-id board-id
                   :platform "monday"
                   :item {:id (str item-id)}}))))))
      (let [{:keys [jira-item-id]
             :as item-link}
              (->
                (e/server (e/watch db-proxy/!item-links))
                (get-in [:by-monday-id (str item-id)]))]
        (dom/div
          (dom/style
            {:height "50vh"
             :background-color monday-light})
          (dom/div
            (dom/style
              {:color "#FFFFFF"
               :display "flex"
               :padding "5px"
               :justify-content "flex-end"})
            (interop/with-reagent DropdownMenuComp item-link))
          (dom/div
            (dom/style
              {:color monday-dark
               :font-family "sans-serif"
               :text-align "center"
               :height "100%"
               :padding-top "30%"})
            (if jira-item-id
              (dom/text (str "This item is linked to jira-item: " jira-item-id))
              (dom/text "This item hasn't been linked to a jira-item yet.")))))
      (catch Pending e (dom/style {:background-color "yellow"})))))

#?(:cljs
     (defn ^:export ^:dev/after-load init! []
       (p/let [{board-id :boardId
                item-id :itemId}
                 (get-monday-context)]
         (current-app/initialize-app (e/boot (binding [dom/node js/document.body] (ItemView. board-id item-id)))))))
