(ns avisi.apps.tech-testing-ground.prototypes.fulcro.server.parser
  (:require
    [avisi.apps.tech-testing-ground.prototypes.fulcro.server.mutations :as mutations]
    [avisi.apps.tech-testing-ground.prototypes.fulcro.server.resolvers :as resolvers]
    [com.wsscode.pathom3.connect.indexes :as pci]
    [com.wsscode.pathom3.connect.planner :as pcp]
    [com.wsscode.pathom3.interface.eql :as p.eql]
    [taoensso.timbre :as log]))

(defonce plan-cache* (atom {}))

(def env
  (->
    {::pcp/plan-cache* plan-cache*}
    (pci/register [resolvers/resolvers mutations/mutations])))

(defn api-parser [{:keys [request query]}]
  (log/info "Process" query)
  (try (p.eql/process (assoc env :request request) query) (catch Exception e (prn (ex-message e)))))


(comment
  (api-parser {:query [{:hello-world [:message]} {:x [:message]}]})
  (api-parser {:query [{:x [:message]}]})
  (api-parser
    {:query
       [({:available-items [:item/id]}
         {:platform "jira"
          :board-id 10002})]})
  (api-parser
    {:query
       [({:item [:item/id]}
         {:board
            {:platform "jira"
             :board-id 10002}
          :item {:item/id "ME-126"}})]}))
