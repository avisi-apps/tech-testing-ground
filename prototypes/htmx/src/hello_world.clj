(ns hello-world
  (:require
    [ctmx.core :as ctmx]
    [formats]
    [shared.core :as shared]))

(ctmx/defcomponent ^:endpoint hello-world [_ name]
  [:div#hello  (shared/hello-from name)])

(defn routes []
  (ctmx/make-routes
    "/hello-world"
    (fn [req]
      (formats/page
        (hello-world req "htmx")))))
