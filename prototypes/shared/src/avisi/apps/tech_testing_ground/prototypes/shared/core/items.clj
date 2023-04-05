(ns avisi.apps.tech-testing-ground.prototypes.shared.core.items)

(def item-schema
  [:map
   [:item/id [string? {:encode/string str}]]
   [:item/title string?]
   [:item/description string?]
   [:item/status [:enum "To Do" "In Progres" "Done" "Blocked"]]])
