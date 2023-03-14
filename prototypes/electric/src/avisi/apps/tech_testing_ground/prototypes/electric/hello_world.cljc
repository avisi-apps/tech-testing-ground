(ns avisi.apps.tech-testing-ground.prototypes.electric.hello-world
  (:require
    #?(:clj [avisi.apps.tech-testing-ground.prototypes.shared.core :as shared-core])
    [hyperfiddle.electric :as e]
    [hyperfiddle.electric-dom2 :as dom]))

(e/defn HelloWorld [] (e/client (dom/text (e/server (shared-core/hello-from "electric")))))