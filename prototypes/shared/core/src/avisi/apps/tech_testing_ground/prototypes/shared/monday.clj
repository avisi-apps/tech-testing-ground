(ns avisi.apps.tech-testing-ground.prototypes.shared.monday
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.jwt :as jwt]
    [clojure.string :as str]
    [clj-http.client :as http]
    [graphql-query.core :refer [graphql-query]]
    [clojure.data.json :as json]
    [edn-query-language.eql-graphql :as eql-gql]
    [hyperfiddle.rcf :refer [tests tap %]]))

(hyperfiddle.rcf/enable!)

(def current-user 36052059)
(def api-url "https://api.monday.com/v2/")

(def item-statuses ["Working on it" "Done" "Stuck"])
(defn current-date-monday-format []
  (let [formatter (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd")
        date-obj (java.time.LocalDateTime/now)]
    {:date (.format formatter date-obj)}))

#_(defn auth-middleware [next-handler]
  (fn [req]
    (->
      (assoc-in req [:headers "Authorization"] api-token)
      (next-handler))))

(defn sent-query [query]
  (http/with-middleware (conj
                          clj-http.client/default-middleware
                          jwt/auth-middleware)
    (->
      (http/post
        api-url
        {:headers
         {"Content-Type" "application/json"}
         :body (json/write-str query)})
      (:body)
      (json/read-str :key-fn keyword)
      (:data))))

(defn get-boards-of-user [] (sent-query {:query "query {boards {id name}}"}))

(defn get-items-of-board [board-id]
  (->>
    (sent-query {:query "query ($board_id: Int) {boards (ids: [$board_id]) {items {id name}}}"
                 :variables {:board_id board-id}})
    (:boards)
    (first)
    (:items)))

(defn add-item-to-board [board-id item-name]
  (let [column-values (json/write-str
                        {:date4 (current-date-monday-format)
                         :person
                         {:personsAndTeams
                          [{:id current-user
                            :kind "person"}]}
                         :status {:index 0}})]
    (sent-query
      {:query "mutation ($board_id: Int!, $item_name: String, $column_values: JSON) { create_item(board_id: $board_id, item_name: $item_name, column_values: $column_values){ id }}"
       :variables {:board_id board-id
                   :item_name item-name
                   :column_values column-values}})))

(defn delete-item [item-id]
  (sent-query
    {:query "mutation($item_id: Int) { delete_item(item_id: $item_id) { id }}"
     :variables {:item_id item-id}}))

(defn update-item [board-id {:item/keys [id name status]}]
  (let [column-values (json/write-str {:name name
                                       :status {:label status}})]
    (sent-query
      {:query "mutation ($item_id: Int $board_id: Int!, $column_values: JSON!) { change_multiple_column_values(item_id: $item_id, board_id: $board_id, column_values: $column_values ) { id }}"
       :variables {:board_id board-id
                   :item_id id
                   :column_values column-values}})))
(comment

  [{:boards [:id :name]}]
  [{(:boards {:ids [~board-id]}) [{:items [:id :name]}]}]
  [{('create_item
      {:board_id ~board-id
       :item_name ~item-name
       :column_values ~column-values})
    [:id]}]
  [{('delete_item {:item_id ~item-id}) [:id]}]

  (get-boards-of-user)
  (get-items-of-board 3990111892)
  (add-item-to-board 3990111892 "from the repl")
  (delete-item 4180594831)
  (update-item 3990111892
    {:item/id 4182971841
     :item/name "New repl name!!"
     :item/status "Stuck"}))
