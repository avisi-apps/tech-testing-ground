(ns avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.routes
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.webhooks :as monday-webhooks]
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.authentication :as auth]))

(defn routes [{:keys [item-view-handler]}]
  [["/monday-item-view"
    {:get
       {:middleware
          [(auth/identify-current-user-middleware
             {:platform "monday"
              :path-to-jwt [:query-params "sessionToken"]
              :path-to-user-id ["dat" "user_id"]})]
        :handler item-view-handler}}]
   ["/monday"
    ["/webhooks"
     {:middleware
        [(auth/identify-current-user-middleware
           {:platform "monday"
            :path-to-user-id [:body-params :event :userId]})]
      :post {:handler monday-webhooks/webhook-handler}}]]])
