(ns avisi.apps.tech-testing-ground.prototypes.electric.react-interop
  #?(:cljs (:require-macros [avisi.apps.tech-testing-ground.prototypes.electric.react-interop :refer [with-reagent]]))
  #?(:clj
       (:require
         [hyperfiddle.electric-dom2 :as dom]))
  #?(:cljs
       (:require
         [hyperfiddle.electric :as e]
         [hyperfiddle.electric-dom2 :as dom]
         [reagent.core :as r]
         [missionary.core :as m]
         ["react-dom/client" :as ReactDom])))

#?(:cljs
     (def ReactRootWrapper
       (r/create-class
         {:component-did-mount (fn [this] (js/console.log "mounted"))
          :render (fn [this] (let [[_ Component & args] (r/argv this)] (into [Component] args)))})))

(def react-root-hook
  "See `e/with`"
  #?(:clj dom/unsupported
     :cljs (fn ([x] (.unmount x)) ([x y] (.insertBefore (.-parentNode x) x y)))))

#?(:cljs
     (defn create-root "See https://reactjs.org/docs/react-dom-client.html#createroot"
       ([node] (create-root node (str (gensym))))
       ([node id-prefix] (ReactDom/createRoot node #js {:identifierPrefix id-prefix}))))

#?(:cljs (defn render [root & args] (.render root (r/as-element (into [ReactRootWrapper] args)))))

(defmacro with-reagent
  [& args]
  `(dom/div                                                 ; React will hijack this element and empty it.
     (binding [dom/node (create-root dom/node)]
       (new (e/hook react-root-hook dom/node (e/fn [] dom/keepalive (render dom/node ~@args)))))))
