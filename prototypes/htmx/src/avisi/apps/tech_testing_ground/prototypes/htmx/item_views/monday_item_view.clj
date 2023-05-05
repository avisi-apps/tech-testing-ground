(ns avisi.apps.tech-testing-ground.prototypes.htmx.item-views.monday-item-view
  (:require
    [avisi.apps.tech-testing-ground.prototypes.htmx.formats :as formats]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]
    [clojure.edn :as edn]))

(def monday-purple "#6161FF")

(def monday-dark "#181B34")

(def monday-light "#F0F3FF")

(defn item-link-dropdown
  [{:keys [board-id item-id item-link]
    :as args}]
  [:sl-dropdown
   [:sl-icon-button
    {:slot "trigger"
     :caret "true"
     :name "three-dots"
     :label "Settings"}]
   (if item-link
     [:sl-menu
      [:sl-menu-item
       {:hx-delete "/monday/item-link"
        :hx-target "#item-link-view"
        :hx-vals
          {:platform "monday"
           :board-id board-id
           :item-id item-id}}
       "Delete Item-Link"]]
     [:sl-menu
      [:sl-menu-item
       {:onclick
          (str
            "monday.execute('openAppFeatureModal',
       { urlPath: 'monday-create-item-link-modal',
         height: 600,
         width: 400 }).then(
          (res) => {\n document.body.dispatchEvent(new Event('itemLinkCreated', {'bubbles':true}))
          });")}
       "Create Item-Link"]])])

(defn ItemLinkMenu
  [{:keys [item-link]
    :as args}]
  [:div
   {:style
      {:display "flex"
       :justify-content "flex-end"}}
   (item-link-dropdown args)])

(defn ItemLink [{:keys [item-link]}]
  [:div
   {:id (str "item-link-" (:monday-item-id item-link))
    :style
      {:height "100%"
       :font-family "sans-serif"
       :text-align "center"
       :padding-top "30%"
       :color monday-dark}}
   (if item-link
     [:p (str "This item is linked to jira-item: " (:jira-item-id item-link))]
     [:p "This item is not linked to any monday-item yet."])])

(defn ItemLinkView
  [{:keys [item-link board-id item-id]
    :as args}]
  [:div
   {:id "item-link-view"
    :hx-trigger "itemLinkCreated from:body"
    :hx-get "/monday/item-link"
    :hx-vals
      {:board-id board-id
       :item-id item-id}
    :hx-swap "outerHTML"
    :style
      {:background-color monday-light
       :height "100%"}}
   (ItemLinkMenu args)
   (ItemLink args)])

(defn routes []
  [["/monday"
    {}
    ["/item-link"
     {:get
        {:handler
           (fn
             [{{:strs [board-id item-id]} :query-params
               :as req}]
             (let [item-link (item-links/get-item-link
                               {:platform "monday"
                                :board-id (edn/read-string board-id)
                                :item-id item-id})]
               (formats/monday-page
                 (ItemLinkView
                   {:platform "monday"
                    :item-id item-id
                    :board-id (edn/read-string board-id)
                    :item-link item-link}))))}
      :post
        {:handler
           (fn
             [{{:strs [platform board-id jira-item monday-item]} :params
               :as req}]
             (item-links/create-item-link
               {:platform platform
                :board-id (edn/read-string board-id)
                :item {:item/id monday-item}}
               {:item/id jira-item})
             {:status 204
              :headers {"HX-Trigger" "itemLinkCreated"}})}
      :delete
        {:handler
           (fn
             [{{:strs [platform board-id item-id]} :form-params
               :as req}]
             (item-links/delete-item-link
               {:platform platform
                :board-id (edn/read-string board-id)
                :item {:id item-id}})
             (formats/monday-page
               (ItemLinkView
                 {:platform "monday"
                  :board-id board-id
                  :item-id item-id
                  :item-link nil})))}}]]])
