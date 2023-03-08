(ns server.core
  (:require
    [ring.adapter.jetty :as jetty]
    [reitit.ring :as ring]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.resource :refer [wrap-resource]]
    [ring.middleware.content-type :refer [wrap-content-type]]
    ))

(def ^:private not-found-handler
  (fn [_]
    {:status 404
     :headers {"Content-Type" "text/plain"}
     :body "Not Found"}))
(defn- catch-req-middleware [next-handler]
  (fn [request]
    (def _req request)
    (next-handler request)))

(defn- app [routes custom-middleware]
  (cond->
    not-found-handler
    routes (ring/ring-handler
             (ring/router routes))
    custom-middleware (custom-middleware)
    #_(catch-req-middleware)
    :always (-> (wrap-params)
              (wrap-resource "public")
              (wrap-content-type))))
(defn instantiate-server [routes #_custom-middleware]
  (->
    (app routes #_custom-middleware)
    (jetty/run-jetty {:port 3000
                      ;; avoids blocking the main thread
                      :join? false})))


