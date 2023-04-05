(ns avisi.apps.tech-testing-ground.prototypes.fulcro.server.resolvers
  (:require
    [com.wsscode.pathom3.connect.operation :as pco]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.hello-world :as shared-core]))

(pco/defresolver
  hello-world-resolver
  [_ _]
  {::pco/output [{:hello-world [:message]}]}
  {:hello-world {:message (shared-core/hello-from "fulcro")}})
(def resolvers [hello-world-resolver])
