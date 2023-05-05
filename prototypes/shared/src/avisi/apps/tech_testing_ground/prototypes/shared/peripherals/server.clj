(ns avisi.apps.tech-testing-ground.prototypes.shared.peripherals.server
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.jira.routes :as jira]
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.routes :as monday]
    [muuntaja.core :as muuntaja]
    [muuntaja.middleware :as middleware]
    [reitit.ring :as ring]
    [ring.adapter.jetty9 :as jetty]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.resource :refer [wrap-resource]]))

(def not-found-response
  {:status 404
   :headers {"Content-Type" "text/plain"}
   :body "Not Found"})

(def ping-route
  ["/ping"
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

(defn- log-exception-middleware
  [next-handler]
  (fn [request]
    (try
      (next-handler request)
      (catch Exception e
        (prn (ex-message e))
        {:status 500
         :body "Something went wrong."}))))

(defn muuntaja-options [custom-content-negotiation]
  (reduce-kv (fn [m k v] (update-in m [:formats k] merge v)) muuntaja/default-options custom-content-negotiation))

(defn wrap-websocket [next-handler ws-handler]
  (fn [request]
    (if (jetty/ws-upgrade-request? request) (jetty/ws-upgrade-response (ws-handler request)) (next-handler request))))

(defn app
  [{:keys [routes custom-content-negotiation ws-handler jira-handlers monday-handlers]
    :or {routes []}}]
  (let [content-negotiation (muuntaja-options custom-content-negotiation)]
    (->
      (ring/ring-handler
        (ring/router [routes ping-route (jira/routes jira-handlers) (monday/routes monday-handlers)])
        (constantly not-found-response))
      (catch-req-middleware)
      (log-exception-middleware)
      (middleware/wrap-format content-negotiation)
      (wrap-params)
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-index-as-root)
      (wrap-websocket ws-handler))))

; TODO: everything uses port 3000 till there's an implemented solution for integrating all prototypes with the same
(defn start-server
  [{:keys [host port resources-path]
    :or
      {port 3000
       host "0.0.0.0"
       resources-path "public"}
    :as server-config}]
  (println (str "\nStarting server on port: " port "\n"))
  (jetty/run-jetty
    (app server-config)
    {:host host
     :port port
     :resources-path resources-path
     :join? false}))
