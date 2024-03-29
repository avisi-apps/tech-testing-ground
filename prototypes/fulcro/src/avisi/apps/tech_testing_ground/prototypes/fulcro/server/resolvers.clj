(ns avisi.apps.tech-testing-ground.prototypes.fulcro.server.resolvers
  (:require
    [avisi.apps.tech-testing-ground.prototypes.shared.core.board-links :as board-links]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.boards :as boards]
    [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]
    [com.wsscode.pathom3.connect.operation :as pco]))

(pco/defresolver
  item-by-id-resolver
  [env _]
  {::pco/output [:item]}
  (let [{:keys [board item]} (pco/params env)] {:item (boards/get-item-by-id board item)}))

(pco/defresolver
  item-link-resolver
  [env _]
  {::pco/output [:item-link]}
  {:item-link (item-links/get-item-link (pco/params env))})

(pco/defresolver
  unlinked-items-resolver
  [env _]
  {::pco/output [:unlinked-items]}
  {:unlinked-items (item-links/get-unlinked-items (pco/params env))})

(pco/defresolver
  available-items-resolver
  [env _]
  {::pco/output [:available-items]}
  {:available-items
     (->>
       (pco/params env)
       (board-links/get-connected-board)
       (item-links/get-unlinked-items))})

(def resolvers [item-by-id-resolver item-link-resolver available-items-resolver])
