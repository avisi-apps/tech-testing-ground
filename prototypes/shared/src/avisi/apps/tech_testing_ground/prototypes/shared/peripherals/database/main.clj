(ns avisi.apps.tech-testing-ground.prototypes.shared.peripherals.database.main
  (:require
    [firestore-clj.core :as f]))

;TODO: make configurable for prod
(defonce db (f/emulator-client "tech-testing-ground" "localhost:8080"))
