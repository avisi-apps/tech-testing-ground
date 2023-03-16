(ns avisi.apps.tech-testing-ground.prototypes.shared.monday)

(defn routes [{:keys [item-view-handler]}]
  [["/monday-item-view"
    {:get
     {:handler item-view-handler}}]])
