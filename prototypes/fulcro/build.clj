(ns build
  (:require
    [clojure.tools.build.api :as b]
    [shadow.cljs.devtools.api :as shadow]))

(def class-dir "target/classes")
(def uber-file "target/fulcro-prototype.jar")
(def basis (b/create-basis {:project "deps.edn"}))
(def main 'fulcro-prototype.server.main)

(defn uberjar [_]
  (println "\nCleaning previous build...")
  (b/delete {:path "target"})
  (println "\nCleaning cljs-compiler output")
  (b/delete {:path "resources/public/js"})
  (println (str "\nCompiling front-end...\n"))
  (shadow/release :frontend)
  (println "\nBundling sources")
  (b/copy-dir
    {:src-dirs ["src" "resources"]
     :target-dir class-dir})
  (println "\nCompiling back-end...\n")
  (b/compile-clj
    {:basis basis
     :src-dirs ["src"]
     :ns-compile '[fulcro-prototype.server.main]
     :class-dir class-dir})
  (println "\nBuilding uberjar...")
  (b/uber
    {:class-dir class-dir
     :uber-file uber-file
     :basis basis
     :main main})
  (println "\nFinished building: " uber-file))
