(ns avisi.apps.tech-testing-ground.prototypes.shared.peripherals.database.board-links
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.database.main :refer [db]]
    [firestore-clj.core :as f]
    [malli.core :as m]
    [malli.transform :as mt]))

(def board-link-schema
  [:map
   [:id string?]
   [:jira-board-id int?]
   [:monday-board-id int?]
   [:sync-by-default boolean?]])

(defn get-board-link [{:keys [platform board-id]}]
  (as->
    (->>
      (->
        (f/coll db "board-links")
        (f/filter= (str platform "-board-id") board-id)
        (f/pull))
      (map (fn [[k v]] (assoc v :board-link-id k)))
      (first))
    board-link
    (m/decode board-link-schema board-link (mt/key-transformer {:decode keyword}))))

(comment
  (get-board-link
    {:platform "jira"
     :board-id 10001})
  (get-board-link
    {:platform "monday"
     :board-id 3990111892}))
