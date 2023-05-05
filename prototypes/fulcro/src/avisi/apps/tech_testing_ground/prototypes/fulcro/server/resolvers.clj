(ns avisi.apps.tech-testing-ground.prototypes.fulcro.server.resolvers
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.core.boards :as boards]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.board-links :as board-links]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.hello-world :as hello-world]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]
    [com.wsscode.pathom3.connect.operation :as pco]))

(pco/defresolver
  hello-world-resolver
  [_ _]
  {::pco/output [{:hello-world [:message]}]}
  {:hello-world {:message (hello-world/hello-from "fulcro")}})

(pco/defresolver
  x-resolver
  [_ _]
  {::pco/output [{:x [:message]}]}
  {:x {:message (hello-world/hello-from "fulcro")}})

(pco/defresolver
  item-by-id-resolver
  [env _]
  {::pco/output [:item]}
  (def _env env)
  (let [{:keys [board item]} (pco/params env)]
    {:item (boards/get-item-by-id board item)}))

(pco/defresolver
  item-link-resolver
  [env _]
  {::pco/output [:item-link]}
  (def _env env)
  {:item-link (item-links/get-item-link (pco/params env))})

(comment

  (item-links/get-item-link (pco/params _env))

  )

(pco/defresolver
  unlinked-items-resolver
  [env _]
  {::pco/output [:unlinked-items]}
  (def _env env)
  {:unlinked-items (item-links/get-unlinked-items (pco/params env))})

(pco/defresolver
  available-items-resolver
  [env _]
  {::pco/output [:available-items]}
  (def _a-env env)
  {:available-items (->> (pco/params env)
                         (board-links/get-connected-board)
                         (item-links/get-unlinked-items))})

(def resolvers [x-resolver hello-world-resolver item-by-id-resolver item-link-resolver available-items-resolver])
