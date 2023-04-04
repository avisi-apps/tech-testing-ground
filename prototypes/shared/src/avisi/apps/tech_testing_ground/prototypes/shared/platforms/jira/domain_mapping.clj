(ns avisi.apps.tech-testing-ground.prototypes.shared.platforms.jira.domain-mapping
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.core.domain :as domain]
    [malli.core :as m]
    [malli.transform :as mt]
    [clojure.set :as set]
    [hyperfiddle.rcf :refer [tests]]))

(def jira-item-schema
  [:map
   [:issue/key string?]
   [:issue/summary string?]
   [:issue/description string?]
   [:issue/status [:enum "To Do" "In Progres" "Done"]]])

(def domain-item-keys->jira-item-keys
  {:item/id          :issue/key
   :item/title       :issue/summary
   :item/description :issue/description
   :item/status      :issue/status})

(def domain-item-status->jira-item-status
  {"To Do"       "To Do"
   "In Progress" "In Progress"
   "Done"        "Done"})

(def jira-key-transformer
  (mt/key-transformer
    {:encode (domain/map-fn-passthrough domain-item-keys->jira-item-keys)
     :decode (domain/map-fn-passthrough (set/map-invert domain-item-keys->jira-item-keys))}))
(defn jira-issue->domain-item
  [{:keys [status]
    :as   jira-issue}]
  (cond-> (m/decode domain/item-schema jira-issue (mt/transformer jira-key-transformer mt/strip-extra-keys-transformer))
          status (update :item/status (domain/map-fn (set/map-invert domain-item-status->jira-item-status)))))

(defn domain-item->jira-issue [domain-item]
  (as-> domain-item item
        (m/encode domain/item-schema item jira-key-transformer)
        (m/encode jira-item-schema item mt/strip-extra-keys-transformer)
        (update item :issue/status (domain/map-fn domain-item-status->jira-item-status "To Do"))))

(tests
  "domain -> jira"
  (->
    {:item/title                "Something todo"
     :item/description          "A description"
     :item/status               "In Progress"
     :item/not-existent-in-jira "shouldn't enter jira-ns"}
    (domain-item->jira-issue))
  :=
  {:issue/summary     "Something todo"
   :issue/description "A description"
   :issue/status      "In Progress"}
  ; unknown status defaults to "To Do"
  (->
    {:item/status "Blocked"}
    (domain-item->jira-issue))
  := {:issue/status "To Do"}
  "jira -> domain"
  (->
    {:issue/summary     "Something todo"
     :issue/description "A description"
     :issue/status      "In Progress"}
    (jira-issue->domain-item))
  :=
  {:item/title       "Something todo"
   :item/description "A description"
   :item/status      "In Progress"})
