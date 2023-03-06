(ns server.resolvers
  (:require [com.wsscode.pathom.connect :as pc]))

(pc/defresolver hello-world-resolver [_ _]
  {::pc/output [{:hello-world [:message]}]}
  {:hello-world {:message "Hello from Fulcro's server"}})
(def resolvers [hello-world-resolver])
