(ns avisi.apps.tech-testing-ground.prototypes.shared.peripherals.database.current-user
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.database.main :refer [db]]
    [firestore-clj.core :as f]))


(defn get-current-user [{:keys [platform user-id]}]
  (->
    (f/coll db "users")
    (f/filter= (str platform ".user-id") user-id)
    (f/pull)
    (vals)
    (first)))

(comment
  (get-current-user
    {:platform "monday"
     :user-id  36052059})
  (get-current-user
    {:platform "jira"
     :user-id  "630c7cca56010c40d4461641"}))
