(ns server.resolvers
  (:require [com.wsscode.pathom.connect :as pc]
            [shared.core :as shared]))

(pc/defresolver hello-world-resolver [_ _]
  {::pc/output [{:hello-world [:message]}]}
  {:hello-world {:message (shared/hello-from "fulcro")}})
(def resolvers [hello-world-resolver])
