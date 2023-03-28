(ns avisi.apps.tech-testing-ground.prototypes.shared.domain)

(def board-schema
  [:map
   [:id string?]])
(def item-schema
  [:map
   [:id string?]
   [:title string?]
   [:description string?]
   [:status [:enum "To Do" "In Progres" "Done" "Blocked"]]])

(def jira-item-schema
  [:map
   [:key string?]
   [:summary string?]
   [:description string?]
   [:status [:enum "To Do" "In Progres" "Done"]]])

(def monday-item-schema
  [:map
   [:id string?]
   [:name string?]
   [:status [:enum "Working on it" "Done" "Stuck"]]])

(def board-link-schema
  [:map
   [:board-link-id string?]
   [:jira-board-id string?]
   [:monday-board-id string?]])

(def item-link-schema
  [:map
   [:item-link-id string?]
   [:jira-item-id string?]
   [:monday-item-id string?]])
