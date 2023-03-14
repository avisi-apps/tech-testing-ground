(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.current-app
  (:require
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp]
    [com.fulcrologic.fulcro.networking.http-remote :as http-remote]
    [com.fulcrologic.fulcro.data-fetch :as df]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.hello-world :as root]))

(defonce
  app
  (app/fulcro-app
    {:remotes
       {:remote
          (http-remote/fulcro-http-remote
            {:request-middleware
               (->
                 (http-remote/wrap-fulcro-request
                   (fn [req] (assoc-in req [:headers "Accept"] "application/transit+json"))))})}}))
(defn ^:export init
  "Shadow-cljs sets this up to be our entry-point function. See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (app/mount! app root/Root "app")
  (df/load! app :hello-world root/HelloWorld)
  (js/console.log "Loaded"))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (app/mount! app root/Root "app")
  ;; As of Fulcro 3.3.0, this addition will help with stale queries when using dynamic routing:
  (comp/refresh-dynamic-queries! app)
  (js/console.log "Hot reload"))
