(ns avisi.apps.tech-testing-ground.prototypes.shared.jwt
  (:require [clojure.data.json :as json]
            [avisi.apps.tech-testing-ground.prototypes.shared.database :as db])
  (:import [org.apache.commons.codec.binary Base64]))

(def current-user (atom nil))
(defn get-jwt-payload [jwt]
  (-> jwt
    (clojure.string/split #"\.")
    second
    Base64/decodeBase64
    String.
    json/read-str))
(defn auth-middleware [next-handler]
  (fn [req]
    (->
      (assoc-in req [:headers "Authorization"] (get @current-user "monday-api-token"))
      (next-handler))))


(defn monday-user-middleware [next-handler]
  (fn [req]
    (let [user (->
                 (:query-params req)
                 (get "sessionToken")
                 (get-jwt-payload)
                 (get-in ["dat" "user_id"])
                 (db/get-current-user))]
      (reset! current-user user))
    (next-handler req)))
(comment

  @current-user

  )
