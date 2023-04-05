(ns avisi.apps.tech-testing-ground.prototypes.shared.platforms.jira.webhooks
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.core.propagate-change :as propagate]
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.jira.domain-mapping :as domain]
    [clojure.edn :as edn]))

(def jira-action->domain-action
  {"issue_created" :create
   "issue_updated" :update
   "issue_deleted" :delete})

(defn webhook-req->propagation-args
  [{{{{{project-id :id} :project
       {status :name} :status
       summary :summary}
        :fields
      key :key}
       :issue}
      :body-params
    {:keys [action]} :path-params}]
  {:platform "jira"
   :board-id (edn/read-string project-id)
   :item
     (->
       {:issue/key key
        :issue/summary summary
        :issue/status status}
       (domain/jira-issue->domain-item))
   :action (jira-action->domain-action action)})

(def propagate-action (propagate/propagate-action-fn webhook-req->propagation-args))

(defn webhook-handler [req] (try (propagate-action req) {:status 200} (catch Exception _ {:status 500})))
