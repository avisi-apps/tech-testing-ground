(ns build
  (:require
    [clojure.edn :as edn]
    [clojure.tools.build.api :as b]
    [clojure.edn :as edn]
    [clojure.string :as str]))

(def class-dir "target/classes")
(def uber-file "target/htmx-prototype.jar")
(def main 'htmx-prototype.main)
(defn append-prototype-root [prototype dir]
  (println (str "./prototypes/" prototype "/" dir))
  (str "./prototypes/" prototype "/" dir))
(defn relative-to-root [deps]
  (->> deps
    (mapv (fn [[k {:keys [local/root]} :as dep]]
            (if root
              [k {:local/root (str/replace-first root #".." "prototypes")}]
              dep)))
    (into {})))
(defn basis [prototype]

  (prn (->
              (slurp (str "./prototypes/" prototype "/deps.edn"))
              (edn/read-string)
              (update :deps relative-to-root)))

  (b/create-basis
    {:project "deps.edn"
     :extra (->
              (slurp (str "./prototypes/" prototype "/deps.edn"))
              (edn/read-string)
              (update :deps relative-to-root))}))


(defn uberjar [{:keys [build]}]
  (println "\nCleaning previous build...")
  (b/delete {:path "target"})
  (println "\nBundling sources...")
  (b/copy-dir
    {:src-dirs (mapv #(append-prototype-root build %) ["src" "resources"])
     :target-dir class-dir})
  (println "\nCompiling back-end...\n")
  (b/compile-clj
    {:basis (basis build)
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
