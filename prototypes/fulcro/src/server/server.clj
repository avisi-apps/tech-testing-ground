(ns server.server
  (:require
    [server.parser :refer [api-parser]]
    [com.fulcrologic.fulcro.server.api-middleware :as server]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [ring.middleware.resource :refer [wrap-resource]]
    [ring.adapter.jetty :as jetty]))

(def server (atom nil))
(def ^:private not-found-handler
  (fn [req]
    {:status 404
     :headers {"Content-Type" "text/plain"}
     :body "Not Found"}))

(defn- wrap-default-index [next-handler]
  (fn [request]
    (next-handler
      (if (= "/" (:uri request))
        (assoc request :uri "/index.html" :content-type "text/html")
        request))))
(def app
  (-> not-found-handler
    (server/wrap-api {:uri "/api"
                      :parser api-parser})
    (server/wrap-transit-params)
    (server/wrap-transit-response)
    (wrap-resource "public")
    wrap-content-type
    (wrap-default-index)))
(defn start-server []
  (reset! server (jetty/run-jetty app {:port 3002
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
