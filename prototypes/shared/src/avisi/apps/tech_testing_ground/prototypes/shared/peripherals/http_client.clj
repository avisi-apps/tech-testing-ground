(ns avisi.apps.tech-testing-ground.prototypes.shared.peripherals.http-client
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.authentication :as auth]
    [clj-http.client :as http]
    [clojure.data.json :as json]))

(defn ^:private custom-middleware [platform] (conj clj-http.client/default-middleware (auth/add-auth-header-middleware platform)))

(def ^:private http-methods
  {:get    http/get
   :post   http/post
   :put    http/put
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
