(ns avisi-apps.tech-testing-ground.prototypes.build
  (:require
    [clojure.tools.build.api :as b]))

(def class-dir "target/classes")
(defn uber-file [tech-name] (str "target/" tech-name "-prototype.jar"))
(def basis (b/create-basis {:project "deps.edn"}))

(defn uberjar [{:keys [tech-name main cljs]}]

  (println "\nCleaning previous build...")
  (b/delete {:path "target"})

  (when-let [{:keys [build]} cljs]
    (println "\nCleaning cljs-compiler output")
    (b/delete {:path "resources/public/js"})
    (println (str "\nCompiling front-end...\n"))
    ((requiring-resolve 'shadow.cljs.devtools.api/release) build))

  (println "\nBundling sources...")
  (b/copy-dir
    {:src-dirs ["src" "resources"]
     :target-dir class-dir})

  (println "\nCompiling back-end...\n")
  (b/compile-clj
    {:basis basis
     :src-dirs ["src"]
     :ns-compile [main]
     :class-dir class-dir})

  (println "\nBuilding uberjar...")
  (b/uber
    {:class-dir class-dir
     :uber-file (uber-file tech-name)
     :basis basis
     :main main})

  (println "\nFinished building: " (uber-file tech-name)))
