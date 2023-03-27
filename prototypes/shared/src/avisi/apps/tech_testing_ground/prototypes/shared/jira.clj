(ns avisi.apps.tech-testing-ground.prototypes.shared.jira
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.http-client :as http-client]
    [clj-http.client :as http]
    [clojure.data.json :as json]))

(def ^:private base-url "https://yabmas.atlassian.net")

(def ^:private perform-request (http-client/perform-request-fn "jira"))

(defn res->item [res]
  (let [{:keys [key] {:keys [summary description] {:keys [name]} :status} :fields} res]
    {:item/key key
     :item/summary summary
     :item/description description
     :item/status name}))

(defn get-items-of-board [board-id]
  ; The type of jira-project we're using always has a single board. Issues are found via the project-id instead of the
  ; id of the board they belong to. In our domain we work with the board-concept though and a project doesn't have any
  ; meaning, so we choose to expose the board-id in our function-signature. So the board-id in our application-domain
  ; corresponds to the project-id in the jira-domain.
  (->
    (perform-request
      {:method :get
       :url (str base-url "/rest/api/2/search?jql=project=" board-id)})
    (:issues)))

(defn get-item [{:item/keys [key]}]
  (-> (perform-request
        {:method :get
         :url (str base-url "/rest/api/2/issue/" key)})
    (res->item)))

(defn add-item-to-board [board-id {:item/keys [summary description]}]
  ; As with get-items the board-id in the function signature corresponds to the project-id in the jira-domain.
  (let [body {:fields
              {:summary summary
               :description description
               :project {:id board-id}
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
  (when (not= item (get-item item))
    (update-fields-of-item item)
    (transition-status-of-item item)))

(defn delete-item [{:item/keys [key]}]
  (perform-request
    {:method :delete
     :url (str base-url "/rest/api/2/issue/" key)}))

(comment
  (get-items-of-board 10001)
  (get-item {:item/key "EX-76"})
  (add-item-to-board
    10001
    {:item/summary "An item"
     :item/description "Something that's already done"
     :item/status "Done"})
  (update-item
    {:item/key "EX-76"
     :item/summary "New title"
     :item/description "My description"
     :item/status "Done"})
  (delete-item {:item/key "EX-14"}))
