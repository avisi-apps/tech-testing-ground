(ns avisi.apps.tech-testing-ground.prototypes.fulcro.server.server
  (:require
    [avisi.apps.tech-testing-ground.prototypes.fulcro.server.parser :as parser]
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.server :as server]
    [com.wsscode.pathom3.connect.operation.transit :as pcot]
    [mount.core :as mount :refer [defstate]]
    [ring.util.response :refer [redirect]]))

(defn pathom-query-handler [{:keys [body-params]}]
  {:status 200
   :accept "application/transit+json"
   :body (parser/api-parser body-params)})

(def pathom-content-negotiation
  {"application/transit+json"
     {:decoder-opts {:handlers pcot/read-handlers}
      :encoder-opts {:handlers pcot/write-handlers}}})

(def routes [["/api" {:post {:handler pathom-query-handler}}]])

(def server-config
  {:routes routes
   :custom-content-negotiation pathom-content-negotiation
   :jira-handlers {:item-view-handler (constantly (redirect "jira-item-view.html"))}
   :monday-handlers {:item-view-handler (constantly (redirect "monday-item-view.html"))}})

(defn start-server [] (server/start-server server-config))

(defstate fulcro-server :start (start-server) :stop (.stop fulcro-server))
(comment (mount/start) (mount/stop) (mount/running-states))
