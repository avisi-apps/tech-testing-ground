(ns server.core
  (:require
    [reitit.ring :as ring]
    [ring.adapter.jetty :as jetty]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.resource :refer [wrap-resource]]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [muuntaja.core :as muuntaja]
    [muuntaja.middleware :as middleware]))

(def not-found-response
  {:status 404
   :headers {"Content-Type" "text/plain"}
   :body "Not Found"})
(defn- wrap-index-as-root
  [next-handler]
  (fn [request] (next-handler (if (= "/" (:uri request)) (assoc request :uri "/index.html") request))))
(defn- catch-req-middleware [next-handler] (fn [request] (def _req request) (next-handler request)))

(defn muuntaja-options [custom-content-negotiation]
  (reduce-kv (fn [m k v] (update-in m [:formats k] merge v)) muuntaja/default-options custom-content-negotiation))

(defn app [{:keys [routes custom-content-negotiation]}]
  (let [content-negotiation (muuntaja-options custom-content-negotiation)]
    (->
      (ring/ring-handler
        (ring/router
          [routes
           ["/ping"
            {:get
               {:handler
                  (fn [_]
                    {:status 200
                     :headers {"Content-Type" "text/plain"}
                     :body "pong"})}}]])
        (constantly not-found-response))
      (middleware/wrap-format content-negotiation)
      (catch-req-middleware)
      (wrap-params)
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-index-as-root))))

(defn start-server
  [{:keys [port]
    :as server-config}]
  (jetty/run-jetty
    (app server-config)
    {:port port
     ;; avoids blocking the main thread
     :join? false}))
