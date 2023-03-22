(ns avisi.apps.tech-testing-ground.prototypes.shared.database
  (:require
    [firestore-clj.core :as f]))

;TODO: make configurable for prod
(def db (f/emulator-client "tech-testing-ground" "localhost:8080"))

(defn get-current-user [user-id]
  (->
    (f/coll db "users")
    (f/filter= "monday-user-id" user-id)
    (f/pull)
    (vals)
    (first)))

(comment

  (get-current-user 36052059)



  )
