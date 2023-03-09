(ns user
  (:require
    #_[clojure.tools.namespace.repl :as tn]
    [mount.core :as mount]
    [server.server]
    ))                                                      ;; <<<< replace this your "app" namespace(s) you want to be available at REPL time

(defn start []
  (mount/start #'server.server/server))                     ;; example on how to start app with certain states

(defn stop []
  (mount/stop))

(start)




;(defn refresh []
;  (stop)
;  (tn/refresh))
;
;(defn refresh-all []
;  (stop)
;  (tn/refresh-all))
;
;(defn go
;  "starts all states defined by defstate"
;  []
;  (start)
;  :ready)
;
;(defn reset
;  "stops all states defined by defstate, reloads modified source files, and restarts the states"
;  []
;  (stop)
;  (tn/refresh :after 'dev/go)


