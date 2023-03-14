(ns avisi.apps.tech-testing-ground.prototypes.shared.atlassian-descriptor
  (:require
    [clojure.data.json :as json]))

(def descriptor
  (let [base-url ""]
    {:name "MondayJiraSync"
     :key "nl.avisi.jira.plugins.monday-jira-sync"
     :description "Plugin for syncing monday and jira boards"
     :authentication {:type "jwt"}
     :vendor
     {:name "Avisi"
      :url "http://www.avisi.com"}
     :baseUrl "https://baseurl.com"
     :lifecycle
     {:installed "/atlassian/lifecycle/installed"
      :uninstalled "/atlassian/lifecycle/uninstalled"}
     :links {:self "https://baseurl.com/connect/jira/atlassian-connect.json"}
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

(comment

  (json/write-str descriptor)

  )
