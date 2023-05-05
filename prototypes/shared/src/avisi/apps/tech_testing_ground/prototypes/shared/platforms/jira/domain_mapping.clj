(ns avisi.apps.tech-testing-ground.prototypes.shared.platforms.jira.domain-mapping
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.core.items :as item]
    [clojure.set :as set]
    [hyperfiddle.rcf :refer [tests]]
    [malli.core :as m]
    [malli.transform :as mt]))

(def domain-item-keys->jira-item-keys
  {:item/id :issue/key
   :item/title :issue/summary
   :item/description :issue/description
   :item/status :issue/status})

(def domain-item-status->jira-item-status
  {"To Do" "To Do"
   "In Progress" "In Progress"
   "Done" "Done"})

(def jira-item-statusses
  (->> (vals domain-item-status->jira-item-status)
       (into [:enum])))

(def jira-item-schema
  [:map
   [:issue/key string?]
   [:issue/summary string?]
   [:issue/description string?]
   [:issue/status jira-item-statusses]])

(def jira-key-transformer
  (mt/key-transformer
    {:encode #(domain-item-keys->jira-item-keys % %)
     :decode #((set/map-invert domain-item-keys->jira-item-keys) % %)}))

(defn jira-issue->domain-item
  [{:keys [status]
    :as jira-issue}]
  (cond-> (m/decode item/item-schema jira-issue (mt/transformer jira-key-transformer mt/strip-extra-keys-transformer))
          status (update :item/status (set/map-invert domain-item-status->jira-item-status))))

(defn domain-item->jira-issue [domain-item]
  (as-> domain-item item
        (m/encode item/item-schema item jira-key-transformer)
        (m/encode jira-item-schema item mt/strip-extra-keys-transformer)
        (update item :issue/status #(domain-item-status->jira-item-status % "To Do"))))

(tests
  "domain -> jira"
  (->
    {:item/id "EX-123"
     :item/title "Something todo"
     :item/description "A description"
     :item/status "In Progress"
     :item/not-existent-in-jira "shouldn't enter jira-ns"}
    (domain-item->jira-issue))
  :=
  {:issue/key "EX-123"
   :issue/summary "Something todo"
   :issue/description "A description"
   :issue/status "In Progress"}
  ; unknown status defaults to "To Do"
  (->
    {:item/status "Blocked"}
    (domain-item->jira-issue))
  := {:issue/status "To Do"}
  "jira -> domain"
  (->
    {:issue/key "EX-123"
     :issue/summary "Something todo"
     :issue/description "A description"
     :issue/status "In Progress"}
    (jira-issue->domain-item))
  :=
  {:item/id "EX-123"
   :item/title "Something todo"
   :item/description "A description"
   :item/status "In Progress"})
