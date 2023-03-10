(ns htmx.formats
  (:require
    [ctmx.render :as render]
    [hiccup.page :as page]))

(defn page [content]
  {:status 200
   :headers {"content-type" "text/html"}
   :body
   (page/html5
     [:head
      [:title "htmx-prototype"]
      #_[:link {:rel "icon" :type "image/x-icon" :href "favicon.ico"}]
      #_[:link {:rel "stylesheet"
              :href "https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.5.2/css/bootstrap.min.css"}]]
     [:body
      (render/walk-attrs content)
      [:script {:src "https://unpkg.com/htmx.org@1.2.0"}]])})