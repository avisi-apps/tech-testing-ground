(ns fulcro-prototype.server.server
  (:require
    [server.core :as server]
    [server.parser :as parser]
    [com.wsscode.pathom3.connect.operation.transit :as pcot]
    [mount.core :as mount :refer [defstate]]))

(defn pathom-query-handler [{:keys [body-params]}]
  {:status 200
   :accept "application/transit+json"
   :body (parser/api-parser body-params)})

(def pathom-content-negotiation
  {"application/transit+json" {:decoder-opts {:handlers pcot/read-handlers}
                               :encoder-opts {:handlers pcot/write-handlers}}})

(def routes
  [["/api" {:post {:handler pathom-query-handler}}]])

(def server-config {:port 3002
                    :routes routes
                    :custom-content-negotiation pathom-content-negotiation})

(defn start-server []
  (server/start-server server-config))

(defstate fulcro-server
  :start (start-server)
  :stop (.stop fulcro-server))
(comment

  (mount/start)

  (mount/stop)

  (mount/running-states)

  )
