(ns avisi.apps.tech-testing-ground.prototypes.shared.monday
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.jwt :as jwt]
    [clj-http.client :as http]
    [graphql-query.core :refer [graphql-query]]
    [clojure.data.json :as json]))

(defn routes [{:keys [item-view-handler]}]
  [["/monday-item-view"
    {:get
     {:middleware [jwt/monday-user-middleware]
      :handler item-view-handler}}]])
