(ns avisi.apps.tech-testing-ground.prototypes.htmx.hello-world
  (:require
    [ctmx.core :as ctmx]
    [avisi.apps.tech-testing-ground.prototypes.htmx.formats :as formats]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.hello-world :as shared-core]))

(ctmx/defcomponent ^:endpoint hello-world [_ _] [:div#hello (shared-core/hello-from "htmx")])

(defn routes [] (ctmx/make-routes "/hello-world" (fn [req] (formats/page (hello-world req)))))
