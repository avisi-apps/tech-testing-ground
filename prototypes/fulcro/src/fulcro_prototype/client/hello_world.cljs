(ns fulcro-prototype.client.hello-world
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))

(defsc HelloWorld [this {:keys [message]}]
  {:query [:message]}
  (dom/div message))

(def ui-hello-world (comp/factory HelloWorld))

(defsc Root [_ {:keys [hello-world] :as props}]
  {:query [{:hello-world (comp/get-query HelloWorld)}]}
  (ui-hello-world hello-world))