(ns avisi.apps.tech-testing-ground.prototypes.htmx.formats
  (:require
    [ctmx.render :as render]
    [hiccup.core :as h]
    [hiccup.page :as page]))

(defn partial-update [content]
  {:status 200
   :headers {"content-type" "text/html"}
   :body (h/html content)})
(defn page [content]
  (def _c content)
  {:status 200
   :headers {"content-type" "text/html"}
   :body
   (page/html5
     [:head
      [:title "htmx-prototype"]
      #_[:meta {:http-equiv "refresh" :content "1"}]
      [:link {:rel "stylesheet" :href "custom.css"}]
      [:link {:rel "stylesheet" :href "https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.4.0/dist/themes/light.css"}]]
     [:body
      {:style
       {:width "100%"
        :height "100%"}}
      (render/walk-attrs content)
      [:script {:type "module" :src "https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.4.0/dist/shoelace-autoloader.js"}]
      [:script {:src "https://unpkg.com/htmx.org@1.2.0"}]
      [:script {:src "https://connect-cdn.atl-paas.net/all.js"}]
      [:script {:src "js/main.js"}]
      [:script {:type "module"} "await Promise.allSettled([
      customElements.whenDefined('sl-dropdown'),
      customElements.whenDefined('sl-icon-button')
      ]);

  document.body.classList.add('ready');"]

      ])})

(comment

  (page/html5 (render/walk-attrs _c))

  )
