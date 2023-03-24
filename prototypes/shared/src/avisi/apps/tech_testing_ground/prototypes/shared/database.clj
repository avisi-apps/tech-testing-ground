(ns avisi.apps.tech-testing-ground.prototypes.shared.database
  (:require
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

(def item-link-firestore-schema
  [:map
   ["jira-item-id" string?]
   ["monday-item-id" int?]])
(def firestore-board-link-schema
  [:map
   ["item-links"
    [:vector {:decode/json (fn [v] (mapv #(into {} %) v))}
     item-link-firestore-schema]]
   ["jira"
    [:map {:decode/json (fn [v] (into {} v))}
     [:board-id int?]]]
   ["monday"
    [:map {:decode/json (fn [v] (into {} v))}
     [:board-id int?]]]])

(def item-link-schema
  [:map
   [:jira-item-id string?]
   [:monday-item-id int?]])

(def board-link-schema
  [:map
   [:item-links
    [:vector item-link-schema]]
   [:jira
    [:map
     [:board-id int?]]]
   [:monday
    [:map
     [:board-id int?]]]])
(defn get-board-link [{:keys [platform board-id]}]
  (as-> (->> (->
               (f/coll db "board-links")
               (f/filter= (str "jira" ".board-id") 10001)
               (f/pull))
          (map (fn [[k v]] (assoc v :id k)))
          (first)) board-link
    ; map-values get returned as java-hashmaps, so they need to be converted to clj-maps. This is most likely a bug in the firestore-clj lib, will look into contributing a fix later but this will do for now.
    (m/decode firestore-board-link-schema board-link mt/json-transformer)
    (m/decode board-link-schema board-link (mt/key-transformer {:decode keyword}))))

(defn update-board-link [{:keys [id] :as board-link}]
  (let [board-link (m/encode board-link-schema board-link (mt/key-transformer {:encode name}))]
    (->
      (f/doc db (str "board-links/" id))
      (f/set! board-link))))

(comment

  (->
    (m/encode board-link-schema _bl (mt/key-transformer {:encode name}))
    ;(get "jira")
    ;(class)
    )

  )

(comment
  (get-current-user
    {:platform "monday"
     :user-id 36052059})
  (get-current-user
    {:platform "jira"
     :user-id "630c7cca56010c40d4461641"})
  (get-board-link
    {:platform "jira"
     :board-id 10001})
  (get-board-link
    {:platform "monday"
     :board-id 3990111892}))
