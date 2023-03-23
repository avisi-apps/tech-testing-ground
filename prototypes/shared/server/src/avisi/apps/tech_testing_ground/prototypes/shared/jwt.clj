(ns avisi.apps.tech-testing-ground.prototypes.shared.jwt
  (:require
    [clojure.data.json :as json]
    [avisi.apps.tech-testing-ground.prototypes.shared.database :as db])
  (:import
    java.util.Base64))

(defonce current-user (atom nil))

(defn encode [to-encode] (.encodeToString (Base64/getEncoder) (.getBytes to-encode)))

(defn decode [to-decode] (String. (.decode (Base64/getDecoder) to-decode)))

(defn get-jwt-payload [jwt]
  (->
    jwt
    (clojure.string/split #"\.")
    (second)
    (decode)
    (json/read-str)))

(defn monday-auth-middleware [next-handler]
  (fn [req]
    (->
      (assoc-in req [:headers "Authorization"] (get @current-user "monday-api-token"))
      (next-handler))))

(defn jira-auth-middleware [next-handler]
  (fn [req]
    (let [email (get-in @current-user ["jira" "email"])
          api-token (get-in @current-user ["jira" "api-token"])
          auth-token (->>
                       (str email ":" api-token)
                       (encode))]
      (->
        (assoc-in req [:headers "Authorization"] (str "Basic " auth-token))
        (next-handler)))))

(defn jira-user-middleware [next-handler]
  (fn [req]
    (let [user-id (->
                    (:query-params req)
                    (get "jwt")
                    (get-jwt-payload)
                    (get "sub"))]
      (->>
        (db/get-current-user
          {:platform "jira"
           :user-id user-id})
        (reset! current-user)))
    (next-handler req)))

(defn monday-user-middleware [next-handler]
  (fn [req]
    (let [user-id (->
                    (:query-params req)
                    (get "sessionToken")
                    (get-jwt-payload)
                    (get-in ["dat" "user_id"]))]
      (->>
        (db/get-current-user
          {:platform "monday"
           :user-id user-id})
        (reset! current-user)))
    (next-handler req)))

(comment @current-user)
