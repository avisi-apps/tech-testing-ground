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

(defmulti ^:private current-user->auth-header identity)

(defmethod ^:private current-user->auth-header "jira" [_]
  (let [email (get-in @current-user ["jira" "email"])
        api-token (get-in @current-user ["jira" "api-token"])
        auth-token (->>
                     (str email ":" api-token)
                     (base64-encode))]
    (str "Basic " auth-token)))

(defmethod ^:private current-user->auth-header "monday" [_]
  (get-in @current-user ["monday" "api-token"]))

(defn auth-middleware [platform]
  (fn [next-handler]
    (fn [req]
      (->
        (assoc-in req [:headers "Authorization"] (current-user->auth-header platform))
        (next-handler)))))

(defn identify-current-user-middleware [{:keys [platform path-to-jwt path-to-user-id]}]
  (fn [next-handler]
    (fn [req]
      (let [user-id (->
                      (:query-params req)
                      (get-in path-to-jwt)
                      (get-jwt-payload)
                      (get-in path-to-user-id))]
        (->>
          (db/get-current-user
            {:platform platform
             :user-id user-id})
          (reset! current-user)))
      (next-handler req))))

(defn current-user->monday-user-id []
  (get-in @current-user ["monday" "user-id"]))

(comment

  @current-user

  (current-user->monday-user-id)

  )
