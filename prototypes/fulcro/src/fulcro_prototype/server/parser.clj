(ns fulcro-prototype.server.parser
  (:require
    [com.wsscode.pathom3.connect.indexes :as pci]
    [com.wsscode.pathom3.interface.eql :as p.eql]
    [com.wsscode.pathom3.connect.planner :as pcp]
    [taoensso.timbre :as log]
    [fulcro-prototype.server.resolvers :as resolvers]))

(defonce plan-cache* (atom {}))

(def env
  (->
    {::pcp/plan-cache* plan-cache*}
    (pci/register [resolvers/resolvers])))

(defn api-parser [query] (log/info "Process" query) (p.eql/process env query))


(comment (api-parser [{:hello-world [:message]}]))