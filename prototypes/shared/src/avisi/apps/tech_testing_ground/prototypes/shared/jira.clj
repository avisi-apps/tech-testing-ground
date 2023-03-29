(ns avisi.apps.tech-testing-ground.prototypes.shared.jira
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.domain :as domain]
    [avisi.apps.tech-testing-ground.prototypes.shared.http-client :as http-client]
    [clj-http.client :as http]
    [clojure.data.json :as json]
    [ring.util.codec :as codec]))

(defonce last-created (atom nil))
(defonce last-updated (atom nil))
(defonce last-deleted (atom nil))

(defn set-last-created [item]
  (reset! last-created item))

(defn set-last-updated [item]
  (reset! last-updated item))
(defn set-last-deleted [item]
  (reset! last-deleted item))
(defn last-created? [board-id {:item/keys [title] :as item}]
  (= (:item/title @last-created) title))

(defn last-updated? [board-id {:item/keys [title status] :as item}]
  (and @last-updated
    (= (select-keys @last-updated [:item/title :item/status]) (select-keys item [:item/title :item/status]))))

(comment

  @last-created
  @last-updated
  @last-deleted

  )
(defn last-deleted? [board-id {:item/keys [id] :as item}]
  (= (select-keys @last-deleted [:id]) (select-keys item [:id])))
(comment

  (last-created? 10001 {:item/title "another item"})

  @last-created
  @last-updated

  (reset! last-created nil)
  )

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

(defn ^:private get-item-by-id [{:item/keys [key]}]
  (-> (perform-request
        {:method :get
         :url (str base-url "/rest/api/2/issue/" key)})
    (res->item)))

(defn ^:private get-item-by-filters [board-id {:item/keys [title]}]
  (let [jql-string (codec/url-encode (str "project = " board-id " AND summary ~ " (str "\"" title "" \")))]
    (-> (perform-request
          {:method :get
           :url (str base-url (str "/rest/api/2/search?jql=" jql-string))})
      #_(res->item))))


(defn add-item-to-board [board-id {:item/keys [title description] :as item}]
  (reset! last-created {:item/title title})
  ; As with get-items the board-id in the function signature corresponds to the project-id in the jira-domain.
  (let [body {:fields
              {:summary title
               :description description
               :project {:id board-id}
               :issuetype {:name "Task"}}}]
    (perform-request
      {:method :post
       :url (str base-url "/rest/api/2/issue")
       :body body})))

(def ^:private transitions
  {"To Do" "11"
   "In Progress" "21"
   "Done" "31"})

(defn ^:private transition-status-of-item [{:item/keys [id status]}]
  (let [body {:transition {:id (get transitions status "11")}}]
    (perform-request
      {:method :post
       :url (str base-url "/rest/api/2/issue/" id "/transitions")
       :body body})))

(defn ^:private update-fields-of-item [{:item/keys [id title description]}]
  (let [body {:fields {:summary title
                       :description description}}]
    (perform-request
      {:method :put
       :url (str base-url "/rest/api/2/issue/" id)
       :body body})))

(defn update-item
  [{:item/keys [id title description status]
    :as item}]
  (when title
    (update-fields-of-item item))
  (when status
    (transition-status-of-item item)))

(defn delete-item [{:item/keys [id] :as item}]
  (let [{:issue/keys [key]} (domain/domain-item->jira-issue item)]
    (prn key)
    (perform-request
      {:method :delete
       :url (str base-url "/rest/api/2/issue/" key)})))

(comment
  (get-items-of-board 10001)
  (get-item-by-id {:item/key "EX-76"})
  (add-item-to-board
    10001
    {:item/title "An item"
     :item/description "Something that's already done"
     :item/status "Done"})
  (update-item
    {:item/key "EX-76"
     :item/summary "New title"
     :item/description "My description"
     :item/status "Done"})
  (delete-item {:item/key "EX-14"}))
