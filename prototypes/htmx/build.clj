(ns build
  (:require
    [clojure.tools.build.api :as b]))

#_(def builds { })
(def class-dir "target/classes")
(def uber-file "target/htmx-prototype.jar")
(def basis (b/create-basis {:project "deps.edn"}))
(def main 'htmx-prototype.main)

(defn append-prototype-root [prototype dir]
  (str "/prototypes/" prototype "/" dir))

(defn uberjar [{:keys [build]}]
  (println "\nCleaning previous build...")
  (b/delete {:path (str "target/" build)})
  (println "\nBundling sources...")
  (b/copy-dir
    {:src-dirs ["src" "resources"]
     :target-dir class-dir})
  (println "\nCompiling back-end...\n")
  (b/compile-clj
    {:basis basis
     :src-dirs ["src"]
     :ns-compile '[htmx-prototype.main]
     :class-dir class-dir})
  (println "\nBuilding uberjar...")
  (b/uber
    {:class-dir class-dir
     :uber-file uber-file
     :basis basis
     :main main})
  (println "\nFinished building: " uber-file))
