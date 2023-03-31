(ns avisi.apps.tech-testing-ground.prototypes.shared.database
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.domain :as domain]
    [firestore-clj.core :as f]
    [malli.core :as m]
    [malli.transform :as mt]
    [clojure.walk :as walk]))

;TODO: make configurable for prod
(defonce db (f/emulator-client "tech-testing-ground" "localhost:8080"))

(defn get-current-user [{:keys [platform user-id]}]
  (->
    (f/coll db "users")
    (f/filter= (str platform ".user-id") user-id)
    (f/pull)
    (vals)
    (first)))

(def board-link-schema
  [:map
   [:id string?]
   [:jira-board-id int?]
   [:monday-board-id int?]])

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

(def item-link-schema
  [:map
   [:board-link-id string?]
   [:jira-item-id string?]
   [:monday-item-id int?]
   [:item-representation domain/item-schema]])

(def item-link-transformer
  (mt/transformer (mt/key-transformer {:encode name
                                       :decode keyword}) mt/string-transformer))
(defn encode-item-link [item-link]
  (m/encode item-link-schema item-link item-link-transformer))

(defn decode-item-link [item-link]
  (m/decode item-link-schema item-link item-link-transformer))

(comment
  (encode-item-link
    {:board-link-id "123AB"
     :jira-item-id "456CD"
     :monday-item-id 123
     :item-representation
     {:item/title "Example item"
      :item/status "To Do"}}))

(defn create-item-link
  [{:keys [board-link-id jira-item-id monday-item-id item-representation]
    :as item-link}]
  (->
    db
    (f/coll "item-links")
    (f/add! (encode-item-link item-link))))

(defn get-item-link
  [{:keys [board-link-id jira-item-id monday-item-id]
    :as item}]
  (let [filter (cond-> {"board-link-id" board-link-id}
                 jira-item-id (assoc "jira-item-id" jira-item-id)
                 ; TODO: fix id conversion with schema
                 monday-item-id (assoc "monday-item-id" (str monday-item-id)))]
    (when-let [[document-id item-link] (->
                                         db
                                         (f/coll "item-links")
                                         (f/filter= filter)
                                         (f/pull)
                                         (first))]
      (some->
        item-link
        (assoc :item-link-id document-id)
        (decode-item-link)))))

(defn update-item-link
  [{:keys [board-link-id jira-item-id monday-item-id item-link-id item-representation]
    :as item-link}]
  (->
    db
    (f/doc (str "item-links/" item-link-id))
    (f/set! (encode-item-link item-link))))

(defn delete-item-link [{:keys [item-link-id]}]
  (->
    db
    (f/doc (str "item-links/" item-link-id))
    (f/delete!)))

(comment
  (get-current-user
    {:platform "monday"
     :user-id 36052059})
  (get-current-user
    {:platform "jira"
     :user-id "630c7cca56010c40d4461641"}))

(comment
  (get-board-link
    {:platform "jira"
     :board-id 10001})
  (get-board-link
    {:platform "monday"
     :board-id 3990111892}))
(comment
  (create-item-link
    {:board-link-id 123
     :monday-item-id 456
     :jira-item-id 789})
  (get-item-link
    {:board-link-id 123
     :monday-item-id 456})
  (get-item-link
    {:board-link-id "hxet7w2KeklGVguP9R3q"
     :jira-item-id "EX-69"})
  (delete-item-link {:item-link-id "Zargf93gtvZUxrfWJ8iB"}))
