(ns avisi.apps.tech-testing-ground.prototypes.htmx.server
  (:require
    [avisi.apps.tech-testing-ground.prototypes.htmx.item-views.jira-create-item-link-modal :as jira-create-item-link]
    [avisi.apps.tech-testing-ground.prototypes.htmx.item-views.jira-item-view :as jira-item-view]
    [avisi.apps.tech-testing-ground.prototypes.htmx.item-views.monday-create-item-link-modal :as
     monday-create-item-link]
    [avisi.apps.tech-testing-ground.prototypes.htmx.item-views.monday-item-view :as monday-item-view]
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.server :as server]
    [mount.core :as mount :refer [defstate]]
    [ring.util.response :refer [redirect]]))

(def routes
  [(jira-item-view/routes) (jira-create-item-link/routes) (monday-item-view/routes) (monday-create-item-link/routes)])

(defn jira-req->domain-query-string [{{:strs [project-id issue-key]} :query-params}]
  (str "?" "board-id=" project-id "&" "item-id=" issue-key))

(defn monday-req->domain-query-string [{{:strs [boardId itemId]} :query-params}]
  ; TODO: find out and fix monday sometimes sending query-params double in an array
  (let [boardId (if (sequential? boardId) (first boardId) boardId)
        itemId (if (sequential? itemId) (first itemId) itemId)]
    (str "?" "board-id=" boardId "&" "item-id=" itemId)))

(def server-config
  {:routes routes
   :jira-handlers
     {:item-view-handler (fn [req] (redirect (str "/jira/item-link" (jira-req->domain-query-string req))))
      :create-item-link-modal-handler
        (fn [req] (redirect (str "/jira/create-item-link-modal" (jira-req->domain-query-string req))))}
   :monday-handlers
     {:item-view-handler (fn [req] (redirect (str "/monday/item-link" (monday-req->domain-query-string req))))
      :create-item-link-modal-handler
        (fn [req] (redirect (str "/monday/create-item-link-modal" (monday-req->domain-query-string req))))}})

(defn start-server [] (server/start-server server-config))

(defstate htmx-server :start (start-server) :stop (.stop htmx-server))

(comment (mount/start) (mount/stop) (mount/running-states))
