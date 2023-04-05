(ns avisi.apps.tech-testing-ground.prototypes.shared.peripherals.database.board-links
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.database.config :refer [db]]
    [firestore-clj.core :as f]
    [malli.core :as m]
    [malli.transform :as mt]))

(def board-link-schema [:map [:id string?] [:jira-board-id int?] [:monday-board-id int?] [:sync-by-default boolean?]])

(defn decode-board-link [board-link] (m/decode board-link-schema board-link (mt/key-transformer {:decode keyword})))

(defn encode-board-link [board-link] (m/encode board-link-schema board-link (mt/key-transformer {:encode name})))

(defn get-board-link [{:keys [platform board-id]}]
  (when-let [[document-id board-link] (->
                                        (f/coll db "board-links")
                                        (f/filter= (str platform "-board-id") board-id)
                                        (f/pull)
                                        (first))]
    (some->
      board-link
      (assoc :board-link-id document-id)
      (decode-board-link))))

(defn update-board-link
  [{:keys [board-link-id]
    :as board-link}]
  (->
    db
    (f/doc (str "board-links/" board-link-id))
    (f/set! (encode-board-link board-link))))


(comment
  (get-board-link
    {:platform "jira"
     :board-id 10002})
  (get-board-link
    {:platform "monday"
     :board-id 3990111892})
  (->
    (get-board-link
      {:platform "monday"
       :board-id 3990111892})
    (assoc :sync-by-default false)
    (update-board-link)))
