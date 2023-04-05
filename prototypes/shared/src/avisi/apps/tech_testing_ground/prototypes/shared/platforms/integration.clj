(ns avisi.apps.tech-testing-ground.prototypes.shared.platforms.integration
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.jira.integration :as jira]
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.integration :as monday]))

;(defmulti get-integration-props name)
;
;(defmethod get-integration-props "jira" ([_] jira/integration-props))
;
;(defmethod get-integration-props "monday" ([_] monday/integration-props))

(def get-integration-props
  {"jira" jira/integration-props
   "monday" monday/integration-props})

(def opposite-platform
  {"jira"   "monday"
   "monday" "jira"})

(defn get-board-identifier [platform]
  (-> platform
      (get-integration-props)
      (get-in [:identifiers :board-identifier])))

(defn get-item-identifier [platform]
  (-> platform
      (get-integration-props)
      (get-in [:identifiers :item-identifier])))
