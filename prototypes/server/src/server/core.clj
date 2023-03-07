(ns server.core
  (:require
    [ring.adapter.jetty :as jetty]
    [reitit.ring :as ring]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.resource :refer [wrap-resource]]
    [ring.middleware.content-type :refer [wrap-content-type]]))

(defn- catch-req-middleware [next-handler]
  (fn [request]
    (def _req request)
    (next-handler request)))

(defn- app [routes #_custom-middleware]
  (->
    (ring/ring-handler
      (ring/router routes))
    #_(custom-middleware)
    #_(catch-req-middleware)
    (wrap-params)
    (wrap-resource "public")
    (wrap-content-type)))
(defn instantiate-server [routes #_custom-middleware]
  (->
    (app routes #_custom-middleware)
    (jetty/run-jetty {:port 3000
                      ;; avoids blocking the main thread
                      :join? false})))


