(ns avisi.apps.tech-testing-ground.prototypes.htmx.formats
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
      [:link {:rel "stylesheet" :href "https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.4.0/dist/themes/light.css"}]]
     [:body (render/walk-attrs content)
      [:script {:src "https://unpkg.com/htmx.org@1.2.0"}]
      [:script {:src "https://connect-cdn.atl-paas.net/all.js" :async "true"}]
      [:script {:type "module" :src "https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.4.0/dist/shoelace-autoloader.js"}] ]
     )})
