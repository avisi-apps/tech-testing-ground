(ns avisi.apps.tech-testing-ground.prototypes.htmx.item-views.jira-item-view
  (:require
    [avisi.apps.tech-testing-ground.prototypes.htmx.formats :as formats]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]
    [clojure.edn :as edn]))

(def jira-light "#FFFFFF")

(def jira-dark-blue "#172B4D")

(def jira-light-blue "#0052CC")

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
       {:hx-delete "/jira/item-link"
        :hx-target "#item-link-view"
        :hx-vals
          {:platform "jira"
           :board-id board-id
           :item-id item-id}}
       "Delete Item-Link"]]
     [:sl-menu
      [:sl-menu-item {:onclick "AP.dialog.create({key: 'create-item-link-modal-module-key'})"} "Create Item-Link"]])])

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
   {:id (str "item-link-" (:jira-item-id item-link))
    :style
      {:height "100%"
       :font-family "sans-serif"
       :text-align "center"
       :padding-top "30%"
       :color jira-light}}
   (if item-link
     [:p (str "This item is linked to monday-item: " (:monday-item-id item-link))]
     [:p "This item is not linked to any monday-item yet."])])

(defn ItemLinkView
  [{:keys [item-link board-id item-id]
    :as args}]
  [:div
   {:id "item-link-view"
    :hx-trigger "itemLinkCreated from:body"
    :hx-get "/jira/item-link"
    :hx-vals
      {:board-id board-id
       :item-id item-id}
    :hx-swap "outerHTML"
    :style
      {:background-color jira-dark-blue
       :height "100%"}}
   (ItemLinkMenu args)
   (ItemLink args)])

(defn routes []
  [["/jira"
    {}
    ["/item-link"
     {:get
        {:handler
           (fn
             [{{:strs [board-id item-id]} :query-params
               :as req}]
             (let [item-link (item-links/get-item-link
                               {:platform "jira"
                                :board-id (edn/read-string board-id)
                                :item-id item-id})]
               (formats/atlas-page
                 (ItemLinkView
                   {:platform "jira"
                    :item-id item-id
                    :board-id (edn/read-string board-id)
                    :item-link item-link})
                 ; atlassian events get propagated as normal browser-events so htmx can listen for them
                 ; TODO: more elegant solution, i.e. write wrapper/helper
                 {:scripts
                    [[:script
                      "AP.events.on('itemLinkCreated', function(){\n  console.log(\"item-created\"); document.body.dispatchEvent(new Event('itemLinkCreated', {'bubbles':true}));\n });"]]})))}
      :post
        {:handler
           (fn
             [{{:strs [platform board-id jira-item monday-item]} :form-params
               :as req}]
             (item-links/create-item-link
               {:platform platform
                :board-id (edn/read-string board-id)
                :item {:item/id jira-item}}
               {:item/id monday-item})
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
             (formats/atlas-page
               (ItemLinkView
                 {:platform "jira"
                  :board-id board-id
                  :item-id item-id
                  :item-link nil})))}}]]])
