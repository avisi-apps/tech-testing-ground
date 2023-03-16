(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.hello-world
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))

(defsc HelloWorld [this {:keys [message]}] {:query [:message]} (dom/div message (dom/p "and some extra!")))

(def ui-hello-world (comp/factory HelloWorld))
