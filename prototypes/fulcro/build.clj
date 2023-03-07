(ns build
  (:require [clojure.tools.build.api :as b]
            [shadow.cljs.devtools.api :as shadow]))

(def target-dir "target")
(def uber-file (str target-dir "/fulcro-prototype.jar"))
(def main 'server.main)

(defn uber-jar [_]
  (println "\nCleaning previous build...")
  (b/delete {:path "target"})

  (println (str "\nCompiling front-end..."))
  (shadow/release :frontend)

  (println "\nCopying static-resources...")
  (b/copy-dir {:src-dirs ["resources"] :target-dir target-dir})

  (println "\nCopying back-end files...")
  (b/copy-dir {:src-dirs ["src/server"] :target-dir (str target-dir "/server")})

  #_(println (str "\nCompiling back-end..."))
  #_(b/compile-clj opts)

  (println "\nBuilding JAR...")
  (b/uber {:uber-file uber-file
           :basis (b/create-basis {})
           :class-dir target-dir})

  (println "\nFinished building: " uber-file)

  (println (format "\nRun with: java -cp target/fulcro-prototype.jar clojure.main -m %s" main)) )
