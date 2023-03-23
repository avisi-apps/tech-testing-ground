(ns avisi.apps.tech-testing-ground.prototypes.shared.monday-routes
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.current-user :as current-user]
    [clj-http.client :as http]
    [graphql-query.core :refer [graphql-query]]
    [clojure.data.json :as json]))

(defn routes [{:keys [item-view-handler]}]
  [["/monday-item-view"
    {:get
     {:middleware [(current-user/identify-current-user-middleware {:platform "monday"
                                                                   :path-to-jwt ["sessionToken"]
                                                                   :path-to-user-id ["dat" "user_id"]})]
      :handler item-view-handler}}]])
