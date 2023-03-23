(ns avisi.apps.tech-testing-ground.prototypes.shared.http-client
  (:require
    [clj-http.client :as http]
    [clojure.data.json :as json]))

(defmulti auth-header identity)

(defn ^:private auth-middleware [platform]
  (fn [next-handler]
    (fn [req]
      (->
        (assoc-in req [:headers "Authorization"] (auth-header platform))
        (next-handler)))))

(defn ^:private custom-middleware [platform] (conj clj-http.client/default-middleware (auth-middleware platform)))

(def ^:private http-methods
  {:get http/get
   :post http/post
   :put http/put
   :delete http/delete})

(defn perform-request-fn [platform]
  (let [middleware (custom-middleware platform)]
    (fn [{:keys [method url body]}]
      (let [request-fn (get http-methods method)
            opts (cond-> {:headers {"Content-Type" "application/json"}} body (assoc :body (json/write-str body)))]
        (http/with-middleware
          middleware
          (some->
            (request-fn url opts)
            (:body)
            (json/read-str :key-fn keyword)))))))
