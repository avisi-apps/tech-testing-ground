(ns avisi.apps.tech-testing-ground.prototypes.shared.monday
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.current-user :as current-user]
    [avisi.apps.tech-testing-ground.prototypes.shared.http-client :as http-client]
    [clojure.string :as str]
    [clj-http.client :as http]
    [clojure.data.json :as json]))

(def ^:private api-url "https://api.monday.com/v2/")

(def ^:private sent-query
  (let [perform-request (http-client/perform-request-fn "monday")]
    (fn [query]
      (->
        (perform-request
          {:method :post
           :url api-url
           :body query})
        (:data)))))

(defn get-items-of-board [board-id]
  (->>
    (sent-query
      {:query "query ($board_id: Int) {boards (ids: [$board_id]) {items {id name}}}"
       :variables {:board_id board-id}})
    (:boards)
    (first)
    (:items)))

(def ^:private item-statuses ["Working on it" "Done" "Stuck"])

(defn ^:private current-date-monday-format []
  (let [formatter (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd")
        date-obj (java.time.LocalDateTime/now)]
    {:date (.format formatter date-obj)}))

(defn add-item-to-board [board-id {:item/keys [name status]}]
  (let [status-index (.indexOf item-statuses status)
        column-values (json/write-str
                        (cond-> {:date4 (current-date-monday-format)}
                          (< -1 status-index) (assoc :status {:index status-index})))]
    (sent-query
      {:query
       "mutation ($board_id: Int!, $item_name: String, $column_values: JSON) { create_item(board_id: $board_id, item_name: $item_name, column_values: $column_values){ id }}"
       :variables
       {:board_id board-id
        :item_name name
        :column_values column-values}})))

(defn update-item [board-id {:item/keys [id name status] :as item}]
  (let [column-values (json/write-str
                        {:name name
                         :status {:label status}})]
    (sent-query
      {:query
       "mutation ($item_id: Int $board_id: Int!, $column_values: JSON!) { change_multiple_column_values(item_id: $item_id, board_id: $board_id, column_values: $column_values ) { id }}"
       :variables
       {:board_id board-id
        :item_id id
        :column_values column-values}})))

(defn delete-item [{:item/keys [id]}]
  (sent-query
    {:query "mutation($item_id: Int) { delete_item(item_id: $item_id) { id }}"
     :variables {:item_id id}}))

(comment
  (get-items-of-board 3990111892)
  (add-item-to-board 3990111892 {:item/name "from the repl"
                                 :item/status "Stuck"})
  (delete-item 4180594831)
  (update-item
    3990111892
    {:item/id 4182971841
     :item/name "New repl name!!"
     :item/status "Stuck"}))

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
