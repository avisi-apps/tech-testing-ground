(ns avisi.apps.tech-testing-ground.prototypes.electric.components.hello-world
  (:require
    #?(:clj [avisi.apps.tech-testing-ground.prototypes.shared.core.hello-world :as shared-core])
    [hyperfiddle.electric :as e]
    [hyperfiddle.electric-dom2 :as dom]))

(e/defn HelloWorld [] (e/client (dom/text (e/server (shared-core/hello-from "electric")))))
