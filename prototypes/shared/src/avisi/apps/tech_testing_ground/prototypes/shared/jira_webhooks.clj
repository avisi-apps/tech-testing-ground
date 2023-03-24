(ns avisi.apps.tech-testing-ground.prototypes.shared.jira-webhooks
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.database :as db]
    [avisi.apps.tech-testing-ground.prototypes.shared.monday :as monday]
    [clojure.edn :as edn]
    ))

(def path-to-project-id [:body-params :issue :fields :project :id])
(def path-to-issue-key [:body-params :issue :key])
(def path-to-account-id [:body-params :issue :creator :accountId])
(def path-to-name [:body-params :issue :fields :summary])
(def path-to-status [:body-params :issue :fields :status :name])

(def translate-status
  {"To Do" -1
   "In Progress" "Working on it"
   "Done" "Done"})
(defn issue-created-handler [req]
  (def _ic-req req)

  (let [webhook-item-key (get-in _ic-req path-to-issue-key)
        board-id (get-in _ic-req path-to-project-id)
        board-link (db/get-board-link {:platform "jira"
                                       :board-id (edn/read-string board-id)})
        item-name (get-in _ic-req path-to-name)
        item-status (get-in _ic-req path-to-status)]

    (if-let [{{:keys [id]} :create_item}
             (some-> (get-in board-link [:monday :board-id])
               (monday/add-item-to-board {:item/name item-name :item/status (get translate-status item-status)}))]
      (->
        board-link
        (update :item-links (fn [item-links] (conj item-links {:jira-item-id webhook-item-key :monday-item-id id})))
        (db/update-board-link))))

  {:status 200})

(comment

  (let [webhook-item-key (get-in _ic-req path-to-issue-key)
        board-id (get-in _ic-req path-to-project-id)
        board-link (db/get-board-link {:platform "jira"
                                       :board-id (edn/read-string board-id)})
        item-name (get-in _ic-req path-to-name)
        item-status (get-in _ic-req path-to-status)]

    (if-let [{{:keys [id]} :create_item}
             (some-> (get-in board-link [:monday :board-id])
               (monday/add-item-to-board {:item/name item-name :item/status (get translate-status item-status)}))]
      (->
        board-link
        (update :item-links (fn [item-links] (conj item-links {:jira-item-id webhook-item-key :monday-item-id id})))
        (db/update-board-link))))

  )


(defn issue-updated-handler [req]
  (def _iu-req req)

  ; WIP
  (let [webhook-item-key (get-in _iu-req path-to-issue-key)
        board-id (get-in _iu-req path-to-project-id)
        board-link (db/get-board-link {:platform "jira"
                                       :board-id (edn/read-string board-id)})
        monday-board-id (get-in board-link [:monday :board-id])
        item-name (get-in _iu-req path-to-name)
        item-status (get-in _iu-req path-to-status)
        monday-item-id (some->> board-link
                         (:item-links)
                         (filter (fn [{:keys [jira-item-id]}] (= jira-item-id webhook-item-key)))
                         (first)
                         (:monday-item-id)
                         ; todo: fix in db
                         (edn/read-string))]


    (monday/update-item monday-board-id {:item/id monday-item-id :item/name item-name :item/status (get translate-status item-status)}))

  {:status 200})

(comment


  ; WIP
  (let [webhook-item-key (get-in _iu-req path-to-issue-key)
        board-id (get-in _iu-req path-to-project-id)
        board-link (db/get-board-link {:platform "jira"
                                       :board-id (edn/read-string board-id)})
        monday-board-id (get-in board-link [:monday :board-id])
        item-name (get _iu-req path-to-name)
        item-status (get-in _iu-req path-to-status)
        monday-item-id (some->> board-link
                         (:item-links)
                         (filter (fn [{:keys [jira-item-id]}] (= jira-item-id webhook-item-key)))
                         (first)
                         (:monday-item-id)
                         ; todo: fix in db
                         (edn/read-string))]


    (monday/update-item monday-board-id {:item/id monday-item-id :item/name item-name :item/status (get translate-status item-status)}))

  )
(defn issue-deleted-handler [req]
  (def _id-req req)
  {:status 200})
