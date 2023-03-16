; Electric currently needs to rebuild everything when any file changes.
(ns ^:dev/always avisi.apps.tech-testing-ground.prototypes.electric.client.main-page
  (:require
    [avisi.apps.tech-testing-ground.prototypes.electric.components.hello-world :as hello-world]
    [hyperfiddle.electric :as e]
    [hyperfiddle.electric-dom2 :as dom]))

(defonce reactor nil)

(defonce root-component (atom nil))

(def electric-main
  (hyperfiddle.electric/boot                                ; Electric macroexpansion - Clojure to signals compiler
    (binding [hyperfiddle.electric-dom2/node js/document.body]
      (hello-world/HelloWorld.))))

(defn ^:dev/after-load ^:export start! []
  (assert (nil? reactor) "reactor already running")
  (set! reactor (electric-main
                  #(js/console.log "Reactor success:" %)
                  #(js/console.error "Reactor failure:" %))))

(defn ^:dev/before-load stop! []
  (when reactor (reactor))                                  ; teardown
  (set! reactor nil))
