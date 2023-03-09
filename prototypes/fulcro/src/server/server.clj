(ns server.server
  (:require
    [com.wsscode.pathom3.interface.eql :as p.eql]
    [server.parser :as parser]
    [com.wsscode.pathom3.connect.operation.transit :as pcot]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [ring.middleware.resource :refer [wrap-resource]]
    [ring.adapter.jetty :as jetty]
    [reitit.ring :as ring]
    [muuntaja.core :as muuntaja]
    [muuntaja.middleware :as middleware]))

(defonce server (atom nil))
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

(defn app []
  (->
    (ring/ring-handler
      (ring/router ["/api" {:post {:handler pathom-query-handler}}])
      (constantly not-found-response))
    (catch-req-middleware)
    (middleware/wrap-format muuntaja-options)
    (wrap-resource "public")
    wrap-content-type
    (wrap-default-index)))
(defn start-server []
  (reset! server (jetty/run-jetty (app) {:port 3002
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
