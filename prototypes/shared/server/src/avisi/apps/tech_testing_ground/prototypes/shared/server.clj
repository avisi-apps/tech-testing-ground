(ns avisi.apps.tech-testing-ground.prototypes.shared.server
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.atlassian-connect :as atlassian-connect]
    [reitit.ring :as ring]
    ;[ring.adapter.jetty :as jetty]
    [ring.adapter.jetty9 :as jetty]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.resource :refer [wrap-resource]]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [muuntaja.core :as muuntaja]
    [muuntaja.middleware :as middleware]
    [mount.core :as mount :refer [defstate]]))

(def not-found-response
  {:status 404
   :headers {"Content-Type" "text/plain"}
   :body "Not Found"})

(def ping-route ["/ping"
                 {:get
                  {:handler
                   (fn [_]
                     {:status 200
                      :headers {"content-type" "text/plain"}
                      :body "pong"})}}])
(defn- wrap-index-as-root
  [next-handler]
  (fn [request] (next-handler (if (= "/" (:uri request)) (assoc request :uri "/index.html") request))))
(defn- catch-req-middleware [next-handler] (fn [request] (def _req request) (next-handler request)))

(defn muuntaja-options [custom-content-negotiation]
  (reduce-kv (fn [m k v] (update-in m [:formats k] merge v)) muuntaja/default-options custom-content-negotiation))

(defn wrap-websocket [next-handler ws-handler]
  (fn [request]
    (if (jetty/ws-upgrade-request? request)
      (jetty/ws-upgrade-response (ws-handler request))
      (next-handler request))))
(defn app [{:keys [routes
                   custom-content-negotiation
                   ws-handler
                   jira-handlers]}]
  (let [content-negotiation (muuntaja-options custom-content-negotiation)]
    (->
      (ring/ring-handler
        (ring/router
          [routes
           (atlassian-connect/routes jira-handlers)])
        (constantly not-found-response))
      (middleware/wrap-format content-negotiation)
      (catch-req-middleware)
      (wrap-params)
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-index-as-root)
      (wrap-websocket ws-handler))))
(defn start-server
  [{:keys [port]
    :as server-config}]
  (println (str "\nStarting server on port: " port "\n"))
  (jetty/run-jetty
    (app server-config)
    {:port port
     :join? false}))

(def config
  {:ports
   {:central-server 3000
    :htmx 3000
    :fulcro 3000
    :electric 3000}})
(defn get-port [tech-name] (get-in config [:ports (keyword tech-name)]))
#_(def central-server-config
    {:port 3000
     :routes atlassian-connect/routes})

#_(defstate central-server :start (start-server central-server-config) :stop (.stop central-server))
(comment (mount/start) (mount/stop) (mount/running-states))
