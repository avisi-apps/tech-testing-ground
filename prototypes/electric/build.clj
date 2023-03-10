(ns build
  "build electric.jar library artifact and demos"
  (:require [clojure.tools.build.api :as b]
            [shadow.cljs.devtools.api :as shadow-api]       ; so as not to shell out to NPM for shadow
            [shadow.cljs.devtools.server :as shadow-server]
            ))

;(def lib 'com.hyperfiddle/electric)
(def version (b/git-process {:git-args "describe --tags --long --always --dirty"}))
(def basis (b/create-basis {:project "deps.edn"}))

(def class-dir "target/classes")
(def default-jar-name "target/electric-prototype.jar")

(defn clean-cljs [_]
  (b/delete {:path "resources/public/js"}))

(defn build-client [{:keys [optimize debug verbose version]
                     :or {optimize true, debug false, verbose false, version version}}]
  (println "\nBuilding client. Version:" version)
  (shadow-server/start!)
  (shadow-api/release :prod {:debug debug,
                             :verbose verbose,
                             :config-merge [{:compiler-options {:optimizations (if optimize :advanced :simple)}
                                             :closure-defines {'hyperfiddle.electric-client/VERSION version}}]})
  (shadow-server/stop!))

(defn uberjar [{:keys [jar-name version optimize debug verbose]
                :or {version version, optimize true, debug false, verbose false}}]

  (println "\nCleaning previous build...")
  (b/delete {:path "target"})

  (println "\nCleaning cljs compiler output")
  (clean-cljs nil)

  (build-client {:optimize optimize, :debug debug, :verbose verbose, :version version})

  (println "\nBundling sources")
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})

  (println "\nCompiling server. Version:" version)
  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :ns-compile '[main]
                  :class-dir class-dir})

  (println "\nBuilding uberjar")
  (b/uber {:class-dir class-dir
           :uber-file default-jar-name
           :basis basis
           :main 'main}))

(defn noop [_])                                             ; run to preload mvn deps