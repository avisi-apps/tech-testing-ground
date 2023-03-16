(ns ^:dev/always user ; Electric currently needs to rebuild everything when any file changes. Will fix
  (:require
    [avisi.apps.tech-testing-ground.prototypes.electric.components.hello-world :as hello-world]
    [avisi.apps.tech-testing-ground.prototypes.electric.client.main-page :as current-app]
    hyperfiddle.electric
    hyperfiddle.electric-dom2))

(defn main []
  #_(prn hello-world/HelloWorld)
  (current-app/electric-main))
