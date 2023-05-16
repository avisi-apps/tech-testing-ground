(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.current-app
  (:require
    [com.fulcrologic.fulcro.application :as f-app]
    [com.fulcrologic.fulcro.networking.http-remote :as http-remote]))

(defonce current-app (atom nil))

(defonce ^:private mount-element-id "app")

(def ^:private remotes
  {:remote
   (http-remote/fulcro-http-remote
     {:request-middleware
      (->
        (http-remote/wrap-fulcro-request
          (fn [req] (assoc-in req [:headers "Accept"] "application/transit+json"))))})})

(defn ^:private reset-current-app [{:keys [client-did-mount]}]
  (reset!
    current-app
    (f-app/fulcro-app
      {:remotes remotes
       :client-did-mount client-did-mount})))

(defn ^:private mount-current-app [root] (f-app/mount! @current-app root mount-element-id))

(defn initialize-app
  [{:keys [root client-did-mount]
    :as app-config}]
  (reset-current-app app-config)
  (mount-current-app root))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (let [root (f-app/root-class @current-app)]
    (mount-current-app root))
  (js/console.log "Hot reload"))

(comment
  @current-app
  (reset! current-app nil)
  (f-app/current-state @current-app))
