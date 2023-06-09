(ns avisi.apps.tech-testing-ground.prototypes.electric.components.jira-item-view
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
       [clojure.edn :as edn]
       [reagent.core :as r])))

(def jira-light "#FFFFFF")

(def jira-dark-blue "#172B4D")

(def jira-light-blue "#0052CC")

;

#?(:cljs (defonce !element (atom nil)))

#?(:cljs
   (def react-element
     [(r/create-class
        {:reagent-render (fn [_] [:div {:id "some-id"} "To be used from Electric"])
         :component-did-mount (fn [_]
                                (->>
                                  (.getElementById js/document "some-id")
                                  (reset! !element)))})]))

(e/defn ElectricComponent []
  (e/client
    (let [element (e/watch !element)]
      (when element
        (dom/on
          element
          "click"
          (e/fn [_]
            (e/server (do-something))))))))
;

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
             {:reagent-render (fn [_] [:div {:id "delete-item-link"} "Delete ItemLink"])
              :component-did-mount
              (fn [_]
                (->>
                  (.getElementById js/document "delete-item-link")
                  (reset! !delete-div)))
              })]]
         [:>
          DropdownItem
          {:id "create-item-link-button"
           :onClick (fn [] (js/AP.dialog.create #js {:key "create-item-link-modal-module-key"}))}
          "Create ItemLink"])]]))

(e/defn ItemView [board-id item-id]
  ; following action should be seen as an on-mount initialisation, which electric doesn't provide as this has the same
  ; effect without extra syntax
  (e/server
    (db-proxy/load-item-link
      {:platform "jira"
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
                   :platform "jira"
                   :item {:id item-id}}))))))
      (let [{:keys [monday-item-id]
             :as item-link}
            (->
              (e/server (e/watch db-proxy/!item-links))
              (get-in [:by-jira-id item-id]))]
        (dom/div
          (dom/style
            {:height "100%"
             :background-color jira-dark-blue})
          (dom/div
            (dom/style
              {:color "#FFFFFF"
               :display "flex"
               :padding "5px"
               :justify-content "flex-end"})
            (interop/with-reagent DropdownMenuComp item-link))
          (dom/div
            (dom/style
              {:color jira-light
               :font-family "sans-serif"
               :text-align "center"
               :height "100%"
               :padding-top "30%"})
            (if monday-item-id
              (dom/text (str "This item is linked to monday-item: " monday-item-id))
              (dom/text "This item hasn't been linked to a monday-item yet.")))))
      (catch Pending e (dom/style {:background-color "yellow"})))))

#?(:cljs
   (defn ^:export ^:dev/after-load init! []
     (js/AP.context.getContext
       (fn [res]
         (let [{{{issue-key :key} :issue
                 {project-id :id} :project}
                :jira}
               (js->clj res :keywordize-keys true)]
           (current-app/initialize-app
             (e/boot (binding [dom/node js/document.body] (ItemView. (edn/read-string project-id) issue-key)))))))))
