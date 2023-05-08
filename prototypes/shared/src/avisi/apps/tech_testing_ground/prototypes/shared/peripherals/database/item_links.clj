(ns avisi.apps.tech-testing-ground.prototypes.shared.peripherals.database.item-links
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.database.config :refer [db]]
    [firestore-clj.core :as f]
    [malli.core :as m]
    [malli.transform :as mt]))

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

; needed to deal with bug in firestore-clj where properties with map values get returned as java hash-maps instead of
; clojure maps
(def ^:private fire-store-item-link-schema [:map ["item-representation" [:map {:decode/json #(into {} %)}]]])

(def ^:private item-link-transformer
  (mt/transformer
    (mt/key-transformer
      {:encode (comp str symbol)
       :decode keyword})
    mt/string-transformer))
(defn ^:private encode-item-link [item-link] (m/encode item-link-schema item-link item-link-transformer))

(defn ^:private decode-item-link [item-link]
  (as-> item-link il
        (m/decode fire-store-item-link-schema il mt/json-transformer)
        (m/decode item-link-schema il item-link-transformer)))

(defn create-item-link
  [{:keys [board-link-id jira-item-id monday-item-id item-representation]
    :as item-link}]
  (->
    db
    (f/coll "item-links")
    (f/add! (encode-item-link item-link))
    (f/pull)))

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
    (f/delete!))
  nil)

(defn get-item-link
  [{:keys [board-link-id jira-item-id monday-item-id]
    :as item}]
  (let [filter (cond-> {"board-link-id" board-link-id}
                       jira-item-id (assoc "jira-item-id" jira-item-id)
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

(defn get-item-links [{:keys [board-link-id]}]
  (->>
    (->
      db
      (f/coll "item-links")
      (f/filter= {"board-link-id" board-link-id})
      (f/pull))
    (mapv
      (fn [[document-id item-link]]
        (->
          item-link
          (assoc "item-link-id" document-id)
          (decode-item-link))))))

(comment
  (encode-item-link
    {:board-link-id "123AB"
     :jira-item-id "456CD"
     :monday-item-id 123
     :item-representation
     {:item/title "Example item"
      :item/status "To Do"}}))
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
     :jira-item-id "ME-120"})
  (delete-item-link {:item-link-id "Zargf93gtvZUxrfWJ8iB"}))
