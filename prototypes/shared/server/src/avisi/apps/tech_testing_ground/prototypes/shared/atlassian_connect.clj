(ns avisi.apps.tech-testing-ground.prototypes.shared.atlassian-connect)

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
     {:jiraIssueGlances
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
         :url "/jira-issue-panel"}
        }]}}))

(def routes
  ["/connect/jira/atlassian-connect.json"
   {:get
    {:handler (fn [_]
                {:status 200
                 :headers {"content-type" "application/json"}
                 :body descriptor})}}])