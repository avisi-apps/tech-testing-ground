(ns avisi.apps.tech-testing-ground.prototypes.shared.platforms.jira.api-wrapper
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.http-client :as http-client]))

; TODO: make configurable
(def ^:private base-url "https://yabmas.atlassian.net")

(def ^:private perform-request (http-client/perform-request-fn "jira"))

(defn res->item
  [{:keys [key]
    {:keys [summary description]
     {:keys [name]} :status}
      :fields}]
  {:issue/key key
   :issue/summary summary
   :issue/description description
   :issue/status name})

(defn get-item-by-id [board-id {:issue/keys [key]}]
  (->>
    (perform-request
      {:method :get
       :url (str base-url "/rest/api/2/issue/" key)})
    (res->item)))

(defn get-items [board-id]
  ; The type of jira-project we're using always has a single board. Issues are found via the project-id instead of the
  ; id of the board they belong to. In our domain we work with the board-concept though and a project doesn't have any
  ; meaning, so we choose to expose the board-id in our function-signature. So the board-id in our application-domain
  ; corresponds to the project-id in the jira-domain.
  (->>
    (perform-request
      {:method :get
       :url (str base-url "/rest/api/2/search?jql=project=" board-id)})
    (:issues)
    (mapv res->item)))

(defn add-item
  [board-id
   {:issue/keys [summary description]
    :as issue}]
  ; As with get-items the board-id in the function signature corresponds to the project-id in the jira-domain.
  (let [body {:fields
                {:summary summary
                 :description description
                 :project {:id board-id}
                 :issuetype {:name "Task"}}}
        {:keys [key]} (perform-request
                        {:method :post
                         :url (str base-url "/rest/api/2/issue")
                         :body body})]
    {:issue/key key
     :issue/summary summary
     :issue/description description}))

(def ^:private transitions
  {"To Do" "11"
   "In Progress" "21"
   "Done" "31"})

(defn ^:private transition-status-of-item [{:issue/keys [key status]}]
  (let [body {:transition {:id (get transitions status "11")}}]
    (perform-request
      {:method :post
       :url (str base-url "/rest/api/2/issue/" key "/transitions")
       :body body})))

(defn ^:private update-fields-of-item [{:issue/keys [key summary description]}]
  (let [body {:fields
                {:summary summary
                 :description description}}]
    (perform-request
      {:method :put
       :url (str base-url "/rest/api/2/issue/" key)
       :body body})))

(defn update-item
  [_
   {:issue/keys [key summary description status]
    :as issue}]
  (when status (transition-status-of-item issue))
  {:issue/key key
   :issue/summary summary
   :issue/description description
   :issue/status status})

(defn delete-item [_ {:issue/keys [key]}]
  (perform-request
    {:method :delete
     :url (str base-url "/rest/api/2/issue/" key)})
  {:issue/key key})

(comment
  (get-item-by-id 10002 {:issue/key "ME-126"})
  (get-items 10002)
  (add-item
    10002
    {:issue/summary "An item"
     :issue/description "Something that's already done"
     :issue/status "Done"})
  (update-item
    10002
    {:issue/key "EX-237"
     :issue/summary "New title"
     :issue/description "My description"
     :issue/status "Done"})
  (delete-item 10002 {:issue/key "EX-235"}))
