(ns avisi.apps.tech-testing-ground.prototypes.shared.current-user
  (:require
    [clojure.data.json :as json]
    [avisi.apps.tech-testing-ground.prototypes.shared.database :as db])
  (:import
    java.util.Base64))

(defonce ^:private current-user (atom nil))

(defn ^:private base64-encode [to-encode] (.encodeToString (Base64/getEncoder) (.getBytes to-encode)))

(defn ^:private base64-decode [to-decode] (String. (.decode (Base64/getDecoder) to-decode)))

(defn ^:private get-jwt-payload [jwt]
  (->
    jwt
    (clojure.string/split #"\.")
    (second)
    (base64-decode)
    (json/read-str)))

(defn monday-auth-middleware [next-handler]
  (fn [req]
    (->
      (assoc-in req [:headers "Authorization"] (get-in @current-user ["monday" "api-token"]))
      (next-handler))))

(defn jira-auth-middleware [next-handler]
  (fn [req]
    (let [email (get-in @current-user ["jira" "email"])
          api-token (get-in @current-user ["jira" "api-token"])
          auth-token (->>
                       (str email ":" api-token)
                       (base64-encode))]
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

(defn current-user->monday-user-id []
  (get-in @current-user ["monday" "user-id"]))

(comment

  @current-user

  (current-user->monday-user-id)

  )
