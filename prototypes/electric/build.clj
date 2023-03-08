(ns build
  (:require [clojure.tools.build.api :as b]))

(def target-dir "target/source")
(def uber-file (str target-dir "/electric-prototype.jar"))
(def main 'user.main)

(defn uber-jar [_]
  (println "There's no reliable way to build uber-jars for electrics app yet, we'll be added here once released..."))
