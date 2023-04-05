(ns avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.domain-mapping
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.core.items :as item]
    [clojure.set :as set]
    [hyperfiddle.rcf :refer [tests]]
    [malli.core :as m]
    [malli.transform :as mt]))

(def monday-item-schema
  [:map [:item/id int?] [:item/name string?] [:item/status [:enum "Working on it" "Done" "Stuck"]]])

(def domain-item-keys->monday-item-keys {:item/title :item/name})

(def domain-item-status->monday-item-status
  {"In Progress" "Working on it"
   "Done" "Done"
   "Blocked" "Stuck"})

(def monday-key-transformer
  (mt/key-transformer
    {:encode #((set/map-invert domain-item-keys->monday-item-keys) % %)
     :decode #(domain-item-keys->monday-item-keys % %)}))

(defn domain-item->monday-item [domain-item]
  (as-> domain-item item
    (m/decode
      monday-item-schema
      item
      (mt/transformer monday-key-transformer mt/string-transformer mt/strip-extra-keys-transformer))
    (update item :item/status domain-item-status->monday-item-status)))

(defn monday-item->domain-item [monday-item]
  (as-> monday-item item
    (m/encode item/item-schema item (mt/transformer monday-key-transformer mt/string-transformer))
    (m/encode item/item-schema item mt/strip-extra-keys-transformer)
    (update item :item/status (set/map-invert domain-item-status->monday-item-status))))
(tests
  "domain -> monday"
  (->
    {:item/id "82352345"
     :item/title "Something todo"
     :item/description "A description"
     :item/status "Blocked"
     :item/not-existent-in-monday "shouldn't enter monday-ns"}
    (domain-item->monday-item))
  :=
  {:item/id 82352345
   :item/name "Something todo"
   :item/status "Stuck"}
  ; Unknown status should default to nil
  (->
    {:item/status "To Do"}
    (domain-item->monday-item))
  := {:item/status nil}
  "monday -> domain"
    (->
      {:item/id 523154123
       :item/name "Something todo"
       :item/status "Stuck"
       :item/monday-specific "Shouldn't leave monday-ns"}
      (monday-item->domain-item))
  :=
    {:item/id "523154123"
     :item/title "Something todo"
     :item/status "Blocked"})
