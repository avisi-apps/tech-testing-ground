(ns avisi.apps.tech-testing-ground.prototypes.shared.domain
  (:require
    [malli.core :as m]
    [malli.transform :as mt]
    [clojure.set :as set]
    [hyperfiddle.rcf :refer [tests]]
    ))

(def board-schema
  [:map
   [:id string?]])
(def item-schema
  [:map
   [:item/id string?]
   [:item/title string?]
   [:item/description string?]
   [:item/status [:enum "To Do" "In Progres" "Done" "Blocked"]]])



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

(defn map-fn-passthrough [m]
  (fn [k] (get m k k)))

(defn map-fn
  ([m] (fn [k] (get m k)))
  ([m default] (fn [k] (get m k default))))

; monday transformations
(def monday-item-schema
  [:map
   [:item/id string?]
   [:item/name string?]
   [:item/status [:enum "Working on it" "Done" "Stuck"]]])

(def domain-item-keys->monday-item-keys
  {:item/title :item/name})

(def domain-item-status->monday-item-status
  {"In Progress" "Working on it"
   "Done" "Done"
   "Blocked" "Stuck"})

(def monday-key-transformer
  (mt/key-transformer
    {:encode (map-fn-passthrough domain-item-keys->monday-item-keys)
     :decode (map-fn-passthrough (set/map-invert domain-item-keys->monday-item-keys))}))
(defn monday-item->domain-item [monday-item]
  (as-> monday-item item
    (m/decode item-schema item (mt/transformer
                                 monday-key-transformer
                                 mt/strip-extra-keys-transformer))
    (update item :item/status (set/map-invert domain-item-status->monday-item-status))))

(defn domain-item->monday-item [domain-item]
  (as-> domain-item item
    (m/encode item-schema item monday-key-transformer)
    (m/encode monday-item-schema item mt/strip-extra-keys-transformer)
    (update item :item/status (map-fn domain-item-status->monday-item-status))))

(tests

  "domain -> monday"
  (->
    {:item/title "Something todo"
     :item/description "A description"
     :item/status "Blocked"
     :item/not-existent-in-monday "shouldn't enter monday-ns"}
    (domain-item->monday-item))
  :=
  {:item/name "Something todo"
   :item/status "Stuck"}

  ; Unknown status should default to nil
  (->
    {:item/status "To Do"}
    (domain-item->monday-item))
  :=
  {:item/status nil}

  "monday -> domain"
  (->
    {:item/name "Something todo"
     :item/status "Stuck"
     :item/monday-specific "Shouldn't leave monday-ns"}
    (monday-item->domain-item))
  :=
  {:item/title "Something todo"
   :item/status "Blocked"})

(def jira-item-schema
  [:map
   [:issue/key string?]
   [:issue/summary string?]
   [:issue/description string?]
   [:issue/status [:enum "To Do" "In Progres" "Done"]]])

(def domain-item-keys->jira-item-keys
  {:item/id :issue/key
   :item/title :issue/summary
   :item/description :issue/description
   :item/status :issue/status})

(def domain-item-status->jira-item-status
  {"To Do" "To Do"
   "In Progress" "In Progress"
   "Done" "Done"})

(def jira-key-transformer
  (mt/key-transformer
    {:encode (map-fn-passthrough domain-item-keys->jira-item-keys)
     :decode (map-fn-passthrough (set/map-invert domain-item-keys->jira-item-keys))}))
(defn jira-issue->domain-item [jira-issue]
  (as-> jira-issue item
    (m/decode item-schema item (mt/transformer
                                 jira-key-transformer
                                 mt/strip-extra-keys-transformer))
    (update item :item/status (map-fn (set/map-invert domain-item-status->jira-item-status)))))

(defn domain-item->jira-issue [domain-item]
  (as-> domain-item item
    (m/encode item-schema item jira-key-transformer)
    (m/encode jira-item-schema item mt/strip-extra-keys-transformer)
    (update item :issue/status (map-fn domain-item-status->jira-item-status "To Do"))))

(tests

  "domain -> jira"
  (->
    {:item/title "Something todo"
     :item/description "A description"
     :item/status "In Progress"
     :item/not-existent-in-jira "shouldn't enter jira-ns"}
    (domain-item->jira-issue))
  :=
  {:issue/summary "Something todo"
   :issue/description "A description"
   :issue/status "In Progress"}

  ; unknown status defaults to "To Do"
  (->
    {:item/status "Blocked"}
    (domain-item->jira-issue))
  :=
  {:issue/status "To Do"}

  "jira -> domain"
  (->
    {:issue/summary "Something todo"
     :issue/description "A description"
     :issue/status "In Progress"}
    (jira-issue->domain-item))
  :=
  {:item/title "Something todo"
   :item/description "A description"
   :item/status "In Progress"}

  )
