(ns electric-prototype.hello-world
  (:require
    #?(:clj [shared.core :as shared])
    [hyperfiddle.electric :as e]
    [hyperfiddle.electric-dom2 :as dom]))

(e/defn HelloWorld [] (e/client (dom/text (e/server (shared/hello-from "electric")))))