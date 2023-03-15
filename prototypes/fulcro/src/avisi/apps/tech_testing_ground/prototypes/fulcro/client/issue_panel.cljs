(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.issue-panel
  (:require
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.hello-world :as hello-world]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.client.current-app :as current-app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.data-fetch :as df]))

(defsc Root
  [_
   {:keys [hello-world]
    :as props}]
  {:query [{:hello-world (comp/get-query hello-world/HelloWorld)}]}
  (hello-world/ui-hello-world hello-world))

(defn ^:export init
  []
  (current-app/initialize-app
    {:root Root
     :client-did-mount
     (fn [app]
       (df/load! app :hello-world hello-world/HelloWorld)
       (js/console.log "Loaded issue-panel"))}))
