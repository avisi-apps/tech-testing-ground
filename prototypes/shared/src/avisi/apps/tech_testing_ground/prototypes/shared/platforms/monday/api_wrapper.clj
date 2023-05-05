(ns avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.api-wrapper
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.http-client :as http-client]
    [clojure.data.json :as json]))

(def ^:private api-url "https://api.monday.com/v2/")

(def ^:private send-query!
  (let [perform-request (http-client/perform-request-fn "monday")]
    (fn [query]
      (->
        (perform-request
          {:method :post
           :url api-url
           :body query})
        (:data)))))

(defn res->item
  [{:keys [id name]
    [{status :text}] :column_values}]
  {:item/id id
   :item/name name
   :item/status status})

(defn get-item-by-id [board-id {:item/keys [id]}]
  (->>
    (send-query!
      {:query
       "query ($board_id: Int, $item_id: Int) \n{boards (ids: [$board_id]) \n  {items (ids: [$item_id])\n    {\n      id \n      name\n      column_values(ids: \"status\") {text}\n    }}}"
       :variables {:board_id board-id :item_id id}})
    (:boards)
    (first)
    (:items)
    (first)
    (res->item)))

(defn get-items [board-id]
  (->>
    (send-query!
      {:query
       "query ($board_id: Int) {boards (ids: [$board_id]) {items {id name column_values(ids: \"status\") {text}}}}"
       :variables {:board_id board-id}})
    (:boards)
    (first)
    (:items)
    ; TODO with schema
    (mapv res->item)))

(defn get-items-by-filter [board-id {:item/keys [title]}]
  (->>
    (send-query!
      {:query
       "query ($board_id: Int!, $item_name: String!) {items_by_column_values (board_id: $board_id, column_id: \"name\", column_value: $item_name) {id}}"
       :variables
       {:board_id board-id
        :item_name title}})
    (:items_by_column_values)))

(def ^:private item-statuses ["Working on it" "Done" "Stuck"])

(defn ^:private current-date-monday-format []
  (let [formatter (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd")
        date-obj (java.time.LocalDateTime/now)]
    {:date (.format formatter date-obj)}))

(defn add-item [board-id {:item/keys [name status]}]
  (let [status-index (.indexOf item-statuses status)
        column-values (json/write-str
                        (cond-> {:date4 (current-date-monday-format)}
                                (< -1 status-index) (assoc :status {:index status-index})))]
    (let
      [{{:keys [id]} :create_item}
       (send-query!
         {:query
          "mutation ($board_id: Int!, $item_name: String, $column_values: JSON) { create_item(board_id: $board_id, item_name: $item_name, column_values: $column_values){ id }}"
          :variables
          {:board_id board-id
           :item_name name
           :column_values column-values}})]
      {:item/id id
       :item/name name
       :item/status status})))

(defn update-item
  [board-id
   {:item/keys [id name status]
    :as item}]
  (let
    [column-values (json/write-str
                     {
                      ;:name name
                      :status {:label status}})
     {{:keys [id name]
       [{status :text}] :column_values}
      :change_multiple_column_values}
     (send-query!
       {:query
        "mutation ($item_id: Int $board_id: Int!, $column_values: JSON!) { change_multiple_column_values(item_id: $item_id, board_id: $board_id, column_values: $column_values ) {id name column_values(ids: \"status\") {text}}}"
        :variables
        {:board_id board-id
         :item_id id
         :column_values column-values}})]
    {:item/id id
     :item/name name
     :item/status status}))

(defn delete-item
  [_
   {:item/keys [id]
    :as item}]
  (let
    [{{:keys [id name]
       [{status :text}] :column_values}
      :delete_item}
     (send-query!
       {:query
        "mutation($item_id: Int) { delete_item(item_id: $item_id) {id name column_values(ids: \"status\") {text}}}"
        :variables {:item_id id}})]
    {:item/id id
     :item/name name
     :item/status status}))

(comment
  (get-item-by-id 3990111892 {:item/id 4408686514})
  (get-items 3990111892)
  (add-item
    3990111892
    {:item/name "from the repl"
     :item/status "Stuck"})
  (update-item
    3990111892
    {:item/id 4255515535
     :item/name "New repl name!!!"
     :item/status "Working on it"})
  (delete-item 3990111892 {:item/id 4254394618}))

(comment
  ; queries for use with eql->gql lib, but no addded value was found in doing this so the decision has been made
  ; to stick with pure gql-queries. These queries are documented here for now in case reasons arise to move to the lib
  ; after all.
  [{:boards [:id :name]}]
  [{(:boards {:ids [~board-id]}) [{:items [:id :name]}]}]
  [{('create_item
      {:board_id ~board-id
       :item_name ~item-name
       :column_values ~column-values})
    [:id]}]
  [{('delete_item {:item_id ~item-id}) [:id]}])
