(ns avisi.apps.tech-testing-ground.prototypes.electric.components.jira-item-view
  (:import [hyperfiddle.electric Pending])
  ;#?(:cljs (:require-macros [avisi.apps.tech-testing-ground.prototypes.electric.components.jira-item-view :refer [with-reagent]]))
  #?(:clj
     (:require
       [avisi.apps.tech-testing-ground.prototypes.shared.core.hello-world :as shared-core]
       [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-links]
       [avisi.apps.tech-testing-ground.prototypes.shared.core.item-links :as item-link]
       [hyperfiddle.electric :as e]
       [hyperfiddle.electric-dom2 :as dom]
       ))
  #?(:cljs
     (:require
       ["@atlaskit/dropdown-menu" :default DropdownMenu :refer [DropdownItemGroup DropdownItem]]
       [hyperfiddle.electric :as e]
       [hyperfiddle.electric-dom2 :as dom]
       [avisi.apps.tech-testing-ground.prototypes.electric.react-interop :as interop]
       [missionary.core :as m]
       [avisi.apps.tech-testing-ground.prototypes.electric.components.atlas-kit.dropdown-menu :as dropdown-menu])))


(def jira-light "#FFFFFF")
(def jira-dark-blue "#172B4D")
(def jira-light-blue "#0052CC")


#?(:cljs
   (defn DropdownMenuComp [on-delete]
     (let [monday-item-id 1]
       [:> DropdownMenu {}
        [:> DropdownItemGroup {}
         (if monday-item-id
           [:> DropdownItem
            #_{:onClick #(prn "delete")}
            "Delete ItemLink"]
           [:> DropdownItem {}
            #_{:onClick #(prn "Create")}
            "Create ItemLink"])]])))

#?(:clj (defonce !item-link (atom nil)))

(e/def item-link (e/server (e/watch !item-link)))

(e/defn HelloWorld [board-id item-id]
  (e/server
    (->>
      (item-links/get-item-link {:platform "jira" :board-id board-id :item-id item-id})
      (reset! !item-link)))

  (e/client
    ;(interop/with-reagent DropdownMenuComp {})
    (try
      (let [
            ; >click is a discrete flow of events
            >click (m/observe (fn mount [emit!]
                                (prn "mounting click")
                                (prn "emit!: " emit!)
                                (let [f (fn [e] (prn "emitting") (emit! e))]
                                  (.addEventListener dom/node "click" f)
                                  (fn unmount []
                                    (prn "unmounting!!")
                                    (.removeEventListener dom/node "click" f)))))

            ; add initial value electric reactive values must never be undefined
            click (new (m/reductions {} nil >click))
            ]
        (when click
          (prn "should delete")
          (prn click)
          (def _x click)
          (e/server (do
                      (item-link/delete-item-link {:board-id board-id :platform "jira" :item {:id item-id}})
                      (reset! !item-link nil))))

        (let [{:keys [monday-item-id jira-item-id]} item-link]
          (prn "item-link: " item-link)
          (dom/div
            (dom/style
              {:height "100%"
               :background-color jira-dark-blue})
            (dom/div
              (dom/style {:color "#FFFFFF"
                          :display "flex"
                          :padding "5px"
                          :justify-content "flex-end"})
              (interop/with-reagent DropdownMenuComp))
            (dom/div
              (dom/style
                {
                 :color jira-light
                 :font-family "sans-serif"
                 :text-align "center"
                 :height "100%"
                 :padding-top "30%"})
              (if monday-item-id
                (dom/text (str "This item is linked to monday-item: " monday-item-id))
                (dom/text "This item hasn't been linked to a monday-item yet."))))))
      (catch Pending e
        (dom/style {:background-color "yellow"})))))
