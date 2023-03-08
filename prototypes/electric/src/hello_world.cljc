(ns hello-world
  (:require
    #?(:clj [shared.core :as shared])
    [hyperfiddle.electric :as e]
    [hyperfiddle.electric-dom2 :as dom]
    [hyperfiddle.electric-ui4 :as ui]))

(e/defn HelloWorld []
  (e/client
    (dom/text
      (e/server (shared/hello-from "Electrics server")))))