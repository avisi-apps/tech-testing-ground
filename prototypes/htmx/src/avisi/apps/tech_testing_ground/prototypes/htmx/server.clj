(ns avisi.apps.tech-testing-ground.prototypes.htmx.server
  (:require
    [avisi.apps.tech-testing-ground.prototypes.htmx.hello-world :as hello-world]
    [avisi.apps.tech-testing-ground.prototypes.htmx.jira-item-view :as jira-item-view]
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.server :as server]
    [avisi.apps.tech-testing-ground.prototypes.htmx.formats :as formats]
    [mount.core :as mount :refer [defstate]]
    [ring.util.response :refer [redirect]]))

(def routes [(hello-world/routes) (jira-item-view/routes)])

(def server-config
  {:routes routes
   :jira-handlers {:item-view-handler (fn [{:keys [query-string] :as req}]
                                        (def _r3 req)
                                        (formats/page (jira-item-view/hello-world req "")))
                   :create-item-link-modal-handler (constantly (redirect "jira-create-item-link-modal.html"))}
   :monday-handlers {:item-view-handler (constantly (redirect "monday-item-view.html"))
                     :create-item-link-modal-handler (constantly (redirect "monday-create-item-view-modal.html"))}})

(defn start-server [] (server/start-server server-config))

(defstate htmx-server :start (start-server) :stop (.stop htmx-server))

(comment (mount/start) (mount/stop) (mount/running-states))
