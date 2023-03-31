(ns avisi.apps.tech-testing-ground.prototypes.shared.authantication
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.database :as db]
    [clojure.data.json :as json])
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

(defmulti ^:private auth-header identity)

(defmethod ^:private auth-header "jira"
  [_]
  (let [email (get-in @current-user ["jira" "email"])
        api-token (get-in @current-user ["jira" "api-token"])
        auth-token (->>
                     (str email ":" api-token)
                     (base64-encode))]
    (str "Basic " auth-token)))

(defmethod ^:private auth-header "monday" [_] (get-in @current-user ["monday" "api-token"]))

(defn identify-current-user-middleware [{:keys [platform path-to-jwt path-to-user-id]}]
  (fn [next-handler]
    (fn [req]
      (def _re req)
      (let [user-id (cond-> req
                      path-to-jwt (-> (get-in path-to-jwt) (get-jwt-payload))
                      :always (get-in path-to-user-id))]
        (->>
          (db/get-current-user
            {:platform platform
             :user-id user-id})
          (reset! current-user)))
      (next-handler req))))

(defn add-auth-header-middleware [platform]
  (fn [next-handler]
    (fn [req]
      (->
        (assoc-in req [:headers "Authorization"] (auth-header platform))
        (next-handler)))))

(comment

  @current-user

  (reset! current-user nil)
  )
