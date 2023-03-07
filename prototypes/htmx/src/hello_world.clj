(ns hello-world
  (:require
    [ctmx.core :as ctmx]
    [formats]
    [shared.core :as shared]))

(ctmx/defcomponent ^:endpoint hello [{:keys [form-params]} name]
  [:div#hello  (shared/hello-from (or (get form-params "name") name))])

(defn routes []
  (ctmx/make-routes
    "/demo"
    (fn [req]
      (formats/page
        [:div
         [:label "From where are you greeting?"]
         [:input {:name "name" :hx-patch "hello" :hx-target "#hello"}]
         (hello req "HTMX")]))))
