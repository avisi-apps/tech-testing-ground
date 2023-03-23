(ns avisi.apps.tech-testing-ground.prototypes.shared.monday-routes
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.current-user :as current-user]
    [clj-http.client :as http]
    [graphql-query.core :refer [graphql-query]]
    [clojure.data.json :as json]))

(defn routes [{:keys [item-view-handler]}]
  [["/monday-item-view"
    {:get
       {:middleware [current-user/monday-user-middleware]
        :handler item-view-handler}}]])
