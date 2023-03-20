(ns avisi.apps.tech-testing-ground.prototypes.shared.database
  (:require
    [firestore-clj.core :as f]))

;TODO: make configurable for prod
(def db (f/emulator-client "tech-testing-ground" "localhost:8080"))

(comment

  (->
    (f/coll db "users")
    (f/add! {"username" "YabMas"
             "monday-api-token" "eyJhbGciOiJIUzI1NiJ9.eyJ0aWQiOjE5OTQ2OTcyNCwidWlkIjozNjA1MjA1OSwiaWFkIjoiMjAyMi0xMS0yNFQwODoyNjo1Mi4wMDBaIiwicGVyIjoibWU6d3JpdGUiLCJhY3RpZCI6MTM5NDk5MzAsInJnbiI6InVzZTEifQ.hQ9mnaVHAiG_JYApoCEepijM-3rob10K6T_Oru_4bRc"
             "jira-api-token" "ATATT3xFfGF0nSRJAcKXdeCWlcW7LZQr3Q72vfqLW381Fe74OrKDE5HddEBZGzh2X-ASC6_3MOH3ROmjW1WTxl0E5M-1X_9Hl8CAGnrgb8xWAXnmRrTcG0pmK0Q_KhYCdqFI6aAndjwAdDcaD469nJg84DzaDlvm9qymAXBGkTUa9z0nvlLzVYw=2C57E263"}))

  )
