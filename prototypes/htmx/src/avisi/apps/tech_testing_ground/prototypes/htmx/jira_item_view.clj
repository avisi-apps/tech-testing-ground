(ns avisi.apps.tech-testing-ground.prototypes.htmx.jira-item-view
  (:require
    [avisi.apps.tech-testing-ground.prototypes.htmx.formats :as formats]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]
    [clojure.edn :as edn]))

(defn ItemLinkMenu [{:keys [jira-item-id board-id item-id] :as item-link}]
  [:div
   {:style
    {:display "flex"
     :justify-content "flex-end"}}

   [:item-link-dropdown]

   #_[:dropdown-menu {:trigger "test"}
      [:dropdown-item-group
       [:dropdown-item "Een"]
       [:dropdown-item "Twee"]
       [:dropdown-item "Drie"]]]
   ])


(defn ItemLink [item-link]
  [:div
   {:id (str "item-link-" (:jira-item-id item-link))
    :style
    {:height "100%"
     :font-family "sans-serif"
     :text-align "center"
     :padding-top "20%"}}
   (if item-link
     [:p (str "This item is linked to monday-item: " (:monday-item-id item-link))]
     [:p "This item is not linked to any monday-item yet."])])

(defn ItemLinkView [item-link]
  [:div {:id "item-link-view"}

   #_(ItemLinkMenu item-link)

   [:dropdown-custom-element
    {:id "dropdown"
     :trigger "Test"}
    [:dropdown-item-custom-element
     {:class "dropdown-item"
      :label "Add"
      :hx-post "/menu-item"
      ;:hx-trigger "changed"
      :hx-target "#dropdown"
      :hx-swap "beforeend"}
     "Add"]
    [:dropdown-item-custom-element
     {:class "dropdown-item"
      :label "Change"
      :hx-put "/menu-item"
      :hx-target "#dropdown > .dropdown-item"
      :hx-swap "outerHTML"}
     "Change"]
    [:dropdown-item-custom-element
     {:class "dropdown-item"
      :label "Delete"
      :hx-delete "/menu-item"
      :hx-target "#dropdown > .dropdown-item"
      :hx-swap "outerHTML"}
     "Delete"]]

   (ItemLink item-link)])


(defn routes []
  [["/jira-item-link-view"
    {:get
     {:handler
      (fn [{:keys [request-method]
            {:strs [project-id issue-key]} :query-params :as req}]
        (def _r3 req)
        (let [item-link (item-links/get-item-link {:platform "jira" :board-id (edn/read-string project-id) :item-id issue-key})]
          (formats/page (ItemLinkView item-link))))}}]
   ["/item-link"
    {:delete
     {:handler
      (fn [req]
        (prn "deleting")
        (formats/page (ItemLinkView nil)))}}]
   ["/menu-item"
    {:post
     {:handler
      (fn [req]
        (formats/partial-update [:dropdown-item-custom-element {:class "dropdown-item shadow-node" :label "Added"} "Added"]))}
     :delete
     {:handler
      (fn [_]
        (formats/partial-update ""))}
     :put
     {:handler
      (fn [_]
        (formats/partial-update [:dropdown-item-custom-element {:class "dropdown-item" :label "Changed"} "Changed"]))}
     }]
   ])

(defn generic-dropdown [item-link board-id item-id]
  [:sl-dropdown
   [:sl-icon-button {:slot "trigger" :caret "true" :name "three-dots" :label "Settings"}]
   (if item-link
     [:sl-menu
      [:sl-menu-item
       {:hx-delete "item-link"
        :hx-target "#item-link-view"
        :hx-vals {:platform "jira"
                  :board-id board-id
                  :item-id item-id}}
       "Delete Item-Link"]]
     [:sl-menu
      [:sl-menu-item
       {:onclick "AP.dialog.create({key: 'create-item-link-modal-module-key'})"}
       "Create Item-Link"]])])
