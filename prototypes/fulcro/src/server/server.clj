(ns server.server
  (:require
    [server.parser :refer [api-parser]]
    [org.httpkit.server :as http]
    [com.fulcrologic.fulcro.server.api-middleware :as server]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [ring.middleware.resource :refer [wrap-resource]]))

(def ^:private not-found-handler
  (fn [req]
    {:status 404
     :headers {"Content-Type" "text/plain"}
     :body "Not Found"}))

(defn- wrap-default-index [next-handler]
  (fn [request]
    (prn (:uri request))
    (next-handler
      (if (= "/" (:uri request))
        (assoc request :uri "/index.html" :content-type "text/html")
        request))))
(def middleware
  (-> not-found-handler
    (server/wrap-api {:uri "/api"
                      :parser api-parser})
    (server/wrap-transit-params)
    (server/wrap-transit-response)
    (wrap-resource "public")
    wrap-content-type
    (wrap-default-index)))

(defonce stop-fn (atom nil))

(defn start [opts]
  (reset! stop-fn (http/run-server middleware opts)))

(defn stop []
  (when @stop-fn
    (@stop-fn)
    (reset! stop-fn nil)))