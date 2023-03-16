(ns avisi.apps.tech-testing-ground.prototypes.electric.client.current-app)

(defonce ^:private reactor nil)

(defonce ^:private current-app nil)

(defn ^:private mount-current-app []
  (set! reactor (current-app
                  #(js/console.log "Reactor success:" %)
                  #(js/console.error "Reactor failure:" %))))

(defn initialize-app [app]
  (set! current-app app)
  (mount-current-app))

(defn ^:dev/after-load start! []
  (assert (nil? reactor) "reactor already running")
  (mount-current-app))

(defn ^:dev/before-load stop! []
  (when reactor (reactor))
  (set! reactor nil))
