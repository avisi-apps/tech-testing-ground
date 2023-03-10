(ns build
  (:require [clojure.tools.build.api :as b]))

(def target-dir "target/source")
(def uber-file (str target-dir "/htmx-prototype.jar"))
(def main 'htmx-prototype.main)

(defn uberjar [_]

  (println "\nCleaning previous build...")
  (b/delete {:path "target"})

  (println "\nCopying static-resources...")
  (b/copy-dir {:src-dirs ["resources"] :target-dir target-dir})

  (println "\nCopying back-end files...")
  (b/copy-dir {:src-dirs ["src"] :target-dir target-dir})

  #_(println (str "\nCompiling back-end..."))
  #_(b/compile-clj opts)

  (println "\nBuilding JAR...")
  (b/uber {:uber-file uber-file
           :basis (b/create-basis {})
           :class-dir target-dir})

  (println "\nFinished building: " uber-file)

  (println (format "\nRun with: java -cp %s clojure.main -m %s" uber-file main)))
