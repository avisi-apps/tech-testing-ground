(ns avisi.apps.tech-testing-ground.prototypes.shared.jira
  (:require
    [clj-http.client :as http]
    [clojure.data.json :as json]
    [avisi.apps.tech-testing-ground.prototypes.shared.jwt :as jwt]))

(def ^:private base-url "https://yabmas.atlassian.net")

(def ^:private middleware (conj clj-http.client/default-middleware jwt/jira-auth-middleware))

(def ^:private http-methods
  {:get http/get
   :post http/post
   :put http/put
   :delete http/delete})

(defn ^:private perform-request [{:keys [method url body]}]
  (let [request-fn (get http-methods method)
        opts (cond-> {:headers {"Content-Type" "application/json"}} body (assoc :body (json/write-str body)))]
    (http/with-middleware
      middleware
      (some->
        (request-fn url opts)
        (:body)
        (json/read-str :key-fn keyword)))))

(defn get-boards-of-user []
  (->
    (perform-request
      {:method :get
       :url (str base-url "/rest/agile/1.0/board")})
    (:values)))

(defn get-items-of-board [board-id]
  (->
    (perform-request
      {:method :get
       :url (str base-url "/rest/agile/1.0/board/" board-id "/issue")})
    (:issues)))

(defn ^:private find-corresponding-project-id [board-id]
  (->
    (perform-request
      {:method :get
       :url (str base-url "/rest/agile/1.0/board/" board-id)})
    (get-in [:location :projectId])))

(defn add-item-to-board [board-id {:item/keys [summary description]}]
  ; For the type of jira-project we're using an issue has to be added to a project which always has one board, so it's
  ; automatically added to this board without further specifying it by id. In our domain
  ; we work with the board-concept though and a project doesn't have any meaning, so we choose to expose the board-id in
  ; our function-signature and have to translate it to a
  ; project-id in the line below.
  (let [project-id (find-corresponding-project-id board-id)
        body {:fields
              {:summary summary
               :description description
               :project {:id project-id}
               :issuetype {:name "Task"}}
              :transition {:id "31"}}]
    (perform-request
      {:method :post
       :url (str base-url "/rest/api/2/issue")
       :body body})))

(def ^:private transitions
  {"To Do" "11"
   "In Progress" "21"
   "Done" "31"})

(defn ^:private transition-status-of-item [{:item/keys [key status]}]
  (let [body {:transition {:id (get transitions status)}}]
    (perform-request
      {:method :post
       :url (str base-url "/rest/api/2/issue/" key "/transitions")
       :body body})))

(defn ^:private update-fields-of-item [{:item/keys [key summary description]}]
  (let [body {:fields
              {:summary summary
               :description description}}]
    (perform-request
      {:method :put
       :url (str base-url "/rest/api/2/issue/" key)
       :body body})))
(defn update-item
  [{:item/keys [key summary description status]
    :as item}]
  (update-fields-of-item item)
  (transition-status-of-item item))

(defn delete-item [{:item/keys [key]}]
  (perform-request
    {:method :delete
     :url (str base-url "/rest/api/2/issue/" key)}))

(comment
  (get-boards-of-user)
  (get-items-of-board 2)
  (add-item-to-board
    2
    {:item/summary "An item"
     :item/description "Something that's already done"
     :item/status "Done"})
  (update-item
    {:item/key "EX-14"
     :item/summary "New title"
     :item/description "My description"
     :item/status "Done"})
  (delete-item {:item/key "EX-21"}))
