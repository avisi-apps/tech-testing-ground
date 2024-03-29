(ns avisi.apps.tech-testing-ground.prototypes.shared.platforms.jira.routes
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.authentication :as authentication]
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.jira.webhooks :as webhooks]))

(def descriptor
  (let [base-url "https://jaamaask.eu.ngrok.io"]
    {:name "MondayJiraSync"
     :key "nl.avisi.jira.plugins.monday-jira-sync"
     :description "Plugin for syncing monday and jira boards"
     :authentication {:type "jwt"}
     :vendor
       {:name "Avisi"
        :url "http://www.avisi.com"}
     :baseUrl base-url
     :lifecycle
       {:installed "/atlassian/lifecycle/installed"
        :uninstalled "/atlassian/lifecycle/uninstalled"}
     :links {:self (str base-url "/connect/jira/atlassian-connect.json")}
     :enableLicensing false
     :scopes ["read" "write" "delete" "act_as_user"]
     :modules
       {:webhooks
          [{:event "jira:issue_created"
            :url "/atlassian/jira/webhooks/issue_created"}
           {:event "jira:issue_updated"
            :url "/atlassian/jira/webhooks/issue_updated"}
           {:event "jira:issue_deleted"
            :url "/atlassian/jira/webhooks/issue_deleted"}]
        :jiraIssueGlances
          [{:name {:value "JiraMondaySync"}
            :icon
              {:width 24
               :height 24
               :url "/favicon.ico"}
            :key "sync-status-panel"
            :content
              {:type "label"
               :label {:value "JiraMondaySync"}}
            :target
              {:type "web_panel"
               :url "/jira-item-view?project-id={project.id}&issue-key={issue.key}"}}]
        :dialogs
          [{:url "/create-item-link-modal"
            :options
              {:size "large"
               :chrome false}
            :key "create-item-link-modal-module-key"}]}}))

(defn routes [{:keys [item-view-handler create-item-link-modal-handler]}]
  [["/atlassian/lifecycle/:lifecycle"
    {:post
       {:parameters {:body map?}
        :handler
          (fn [req]
            {:status 200
             :headers {"Content-Type" "text/plain"}
             :body "Temporary lifecycle dummy-response"})}}]
   ["/atlassian/jira"
    [["/atlassian-connect.json"
      {:get
         {:handler
            (fn [_]
              {:status 200
               :headers {"content-type" "application/json"}
               :body descriptor})}}]
     ["/webhooks/:action"
      {:middleware
         [(authentication/identify-current-user-middleware
            {:platform "jira"
             :path-to-user-id [:body-params :issue :fields :creator :accountId]})]
       :post {:handler webhooks/webhook-handler}}]]]
   ["/jira-item-view"
    {:middleware
       [(authentication/identify-current-user-middleware
          {:platform "jira"
           :path-to-jwt [:query-params "jwt"]
           :path-to-user-id ["sub"]})]
     :get {:handler item-view-handler}}]
   ["/create-item-link-modal"
    {:middleware
       [(authentication/identify-current-user-middleware
          {:platform "jira"
           :path-to-jwt [:query-params "jwt"]
           :path-to-user-id ["sub"]})]
     :get {:handler create-item-link-modal-handler}}]
   #_["/atlassian/jira/modules/issue-panel"
      {:get
         {:handler
            (fn [_]
              {:status 200
               :headers {"Content-Type" "text/plain"}
               :body "To be added once it's clear what it's used for..."})}}]])
