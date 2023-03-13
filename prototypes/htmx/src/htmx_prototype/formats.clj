(ns htmx-prototype.formats
  (:require
    [ctmx.render :as render]
    [hiccup.page :as page]))

(defn page [content]
  {:status 200
   :headers {"content-type" "text/html"}
   :body
     (page/html5
       [:head [:title "htmx-prototype"]]
       [:body (render/walk-attrs content) [:script {:src "https://unpkg.com/htmx.org@1.2.0"}]])})
