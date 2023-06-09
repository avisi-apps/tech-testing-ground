(ns avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.routes
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.peripherals.authentication :as auth]
    [avisi.apps.tech-testing-ground.prototypes.shared.platforms.monday.webhooks :as monday-webhooks]))

(defn routes [{:keys [item-view-handler create-item-link-modal-handler]}]
  [["/monday-item-view"
    {:get
       {:middleware
          [(auth/identify-current-user-middleware
             {:platform "monday"
              :path-to-jwt [:query-params "sessionToken"]
              :path-to-user-id ["dat" "user_id"]})]
        :handler item-view-handler}}]
   ["/monday-create-item-link-modal" {:get {:handler create-item-link-modal-handler}}]
   ["/monday"
    ["/webhooks"
     {:middleware
        [(auth/identify-current-user-middleware
           {:platform "monday"
            :path-to-user-id [:body-params :event :userId]})]
      :post {:handler monday-webhooks/webhook-handler}}]]])
