(ns server
  (:require
    [hello-world]
    [ring.adapter.jetty :as jetty]
    [reitit.ring :as ring]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.resource :refer [wrap-resource]]
    [ring.middleware.content-type :refer [wrap-content-type]]
    ))

(defonce server (atom nil))

(defn routes []
  (hello-world/routes))

(def not-found-response
  {:status 404
   :headers {"Content-Type" "text/plain"}
   :body "Not Found"})
(defn- catch-req-middleware [next-handler]
  (fn [request]
    (def _req request)
    (next-handler request)))
(defn app []
  (->
    (ring/ring-handler
      (ring/router (routes))
      (constantly not-found-response))
    (catch-req-middleware)
    (wrap-params)
    (wrap-resource "public")
    (wrap-content-type)))
(defn start-server []
  (reset! server (jetty/run-jetty (app) {:port 3000
                                         ;; avoids blocking the main thread
                                         :join? false})))
(defn stop-server []
  (when-some [s @server]
    (.stop s)
    (reset! server nil)))

(defn restart-server []
  (stop-server)
  (start-server))

(comment

  (restart-server)

  )