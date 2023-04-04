(ns avisi.apps.tech-testing-ground.prototypes.shared.core.domain)

(def board-schema [:map [:platform string?] [:id string?]])

(def item-schema
  [:map {:decode/json #(into {} %)}
   [:item/id [string? {:encode/string str}]]
   [:item/title string?]
   [:item/description string?]
   [:item/status [:enum "To Do" "In Progres" "Done" "Blocked"]]])

(def board-link-schema [:map [:board-link-id string?] [:jira-board-id string?] [:monday-board-id string?]])

(def item-link-schema
  [:map
   [:board-link-id string?]
   [:jira-item-id string?]
   [:monday-item-id string?]
   [:item-representation
    [:map
     [:id string?]
     [:title string?]
     [:description string?]
     [:status [:enum "To Do" "In Progres" "Done" "Blocked"]]]]])

(defn map-fn-passthrough [m] (fn [k] (get m k k)))

(defn map-fn
  ([m] (fn [k] (get m k)))
  ([m default] (fn [k] (get m k default))))
