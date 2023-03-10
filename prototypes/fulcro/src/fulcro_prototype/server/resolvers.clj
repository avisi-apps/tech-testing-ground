(ns fulcro-prototype.server.resolvers
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [shared.core :as shared]))

(pco/defresolver hello-world-resolver [_ _]
  {::pco/output [{:hello-world [:message]}]}
  {:hello-world {:message (shared/hello-from "fulcro")}})
(def resolvers [hello-world-resolver])
