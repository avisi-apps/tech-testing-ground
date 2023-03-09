(ns server.server
  (:require
    [server.parser :as parser]
    [com.wsscode.pathom3.connect.operation.transit :as pcot]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [ring.middleware.resource :refer [wrap-resource]]
    [ring.adapter.jetty :as jetty]
    [reitit.ring :as ring]
    [muuntaja.core :as muuntaja]
    [muuntaja.middleware :as middleware]
    [mount.core :as mount :refer [defstate]]))

(def not-found-response
  {:status 404
   :headers {"Content-Type" "text/plain"}
   :body "Not Found"})
(defn- catch-req-middleware [next-handler]
  (fn [request]
    (def _req request)
    (next-handler request)))
(defn- wrap-default-index [next-handler]
  (fn [request]
    (next-handler
      (if (= "/" (:uri request))
        (assoc request :uri "/index.html" :content-type "text/html")
        request))))

(defn pathom-query-handler [{:keys [body-params]}]
  {:status 200
   :accept "application/transit+json"
   :body (parser/api-parser body-params)})

(def muuntaja-options
  (update-in
    muuntaja/default-options
    [:formats "application/transit+json"]
    merge {:decoder-opts {:handlers pcot/read-handlers}
           :encoder-opts {:handlers pcot/write-handlers}}))

(def routes
  [["/api" {:post {:handler pathom-query-handler}}]
   ["/ping" {:get {:handler (fn [_] {:status 200
                                     :headers {"Content-Type" "text/plain"}
                                     :body "pong"})}}]])

(def app
  (->
    (ring/ring-handler
      (ring/router routes)
      (constantly not-found-response))
    (catch-req-middleware)
    (middleware/wrap-format muuntaja-options)
    (wrap-resource "public")
    wrap-content-type
    (wrap-default-index)))
(defn start-server []
  (jetty/run-jetty app {:port 3002
                        ;; avoids blocking the main thread
                        :join? false}))
(defstate server
  :start (start-server)
  :stop (.stop server))
(comment

  (mount/start)

  (mount/stop)

  )
