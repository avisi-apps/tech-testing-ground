(ns avisi.apps.tech-testing-ground.prototypes.htmx.jira-item-view
  (:require
    [avisi.apps.tech-testing-ground.prototypes.htmx.formats :as formats]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :s item-links]
    [clojure.edn :as edn]
    [ctmx.core :as ctmx]))

(ctmx/defcomponent ^:endpoint hello-world [{{:strs [project-id issue-key]} :query-params :as req} _]
  (def _r req)


  (if-let [item-link (item-links/get-item-link {:platform "jira" :board-id (edn/read-string project-id) :item-id issue-key})]
    [:div
     [:sl-dropdown
      [:sl-button {:slot "trigger" :caret "true"} "..."]
      [:sl-menu
       [:sl-menu-item "Delete Item-Link"]]]
     [:p (str "This item is linked to monday-item: " (:monday-item-id item-link))]]
    [:div
     [:sl-dropdown
      [:sl-button {:slot "trigger" :caret "true"} "..."]
      [:sl-menu
       [:sl-menu-item "Create Item-Link"]]]
     [:p "This item is not linked to any monday-item yet."]])

  )

(comment

  (let [{{:strs [project-id issue-key]} :query-params :as req} _r]
    (if-let [item-link (item-links/get-item-link {:platform "jira" :board-id (edn/read-string project-id) :item-id issue-key})]
      [:p (str "This item is linked to monday-item: " (:monday-item-id item-link))]
      [:p "This item is not linked to any monday-item yet."])
    (item-links/get-item-link {:platform "jira" :board-id (edn/read-string project-id) :item-id issue-key})
    #_issue-key)

  )

(defn routes []
  (ctmx/make-routes "/jira-item-view-htmx" (fn [req] (formats/page (hello-world req "")))))
