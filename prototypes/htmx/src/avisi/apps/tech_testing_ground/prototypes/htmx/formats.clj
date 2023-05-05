(ns avisi.apps.tech-testing-ground.prototypes.htmx.formats
  (:require
    [ctmx.render :as render]
    [hiccup.page :as page]))

(defn page ([content] (page content {:scripts []}))
  ([content {:keys [scripts]}]
   {:status 200
    :headers {"content-type" "text/html"}
    :body
      (page/html5
        [:head
         [:title "htmx-prototype"]
         [:link
          {:rel "stylesheet"
           :href "https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.4.0/dist/themes/light.css"}]
         [:link
          {:rel "stylesheet"
           :href "/css/base-styles.css"}]
         [:link
          {:rel "stylesheet"
           :href "/css/atlas.css"}]]
        (into
          [:body
           {:hx-ext "shoelace"}
           (render/walk-attrs content)
           [:script {:src "https://unpkg.com/htmx.org@1.2.0"}]
           [:script {:src "https://unpkg.com/hyperscript.org@0.9.8"}]
           [:script
            {:type "module"
             :src "https://cdn.jsdelivr.net/npm/@shoelace-style/shoelace@2.4.0/dist/shoelace-autoloader.js"}]
           [:script {:src "/js/htmx.ext.shoelace.js"}]]
          scripts))}))


(defn page-platform-wrapper [platform-scripts]
  (fn page-fn
    ([content] (page-fn content {:scripts []}))
    ([content
      {:keys [scripts]
       :as opts}]
     (->>
       (update
         opts
         :scripts
         (fn [scrs]
           (->
             (into scrs platform-scripts)
             ; platform sdks should be loaded before any scripts that possibly rely on it
             (reverse))))
       (page content)))))

(def atlas-script [[:script {:src "https://connect-cdn.atl-paas.net/all.js"}]])
(def atlas-page (page-platform-wrapper atlas-script))

(def monday-script
  [[:script "const monday = window.mondaySdk()"]
   [:script {:src "https://cdn.jsdelivr.net/npm/monday-sdk-js/dist/main.js"}]])
(def monday-page (page-platform-wrapper monday-script))
