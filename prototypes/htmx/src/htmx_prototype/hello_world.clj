(ns htmx-prototype.hello-world
  (:require
    [ctmx.core :as ctmx]
    [htmx-prototype.formats :as formats]
    [shared.core :as shared]))

(ctmx/defcomponent ^:endpoint hello-world [_ _]
  [:div#hello  (shared/hello-from "htmx")])

(defn routes []
  (ctmx/make-routes
    "/hello-world"
    (fn [req]
      (formats/page
        (hello-world req)))))
