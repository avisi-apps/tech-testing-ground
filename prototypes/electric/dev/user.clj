(ns user)
; lazy load dev stuff - for faster REPL startup and cleaner dev classpath
(def shadow-start! (delay @(requiring-resolve 'shadow.cljs.devtools.server/start!)))
(def shadow-watch (delay @(requiring-resolve 'shadow.cljs.devtools.api/watch)))

(defn setup-shadow-cljs-watch [] (@shadow-start!) (@shadow-watch :dev))

; Userland Electric code is lazy loaded by the shadow build due to usage of
; :require-macros in all Electric source files.
; WARNING: make sure your REPL and shadow-cljs are sharing the same JVM!

(comment
  (setup-shadow-cljs-watch)
  (hyperfiddle.rcf/enable!)                                 ; turn on RCF after all transitive deps have loaded
  (shadow.cljs.devtools.api/repl :dev)                      ; shadow server hosts the cljs repl
  ; connect a second REPL instance to it
  ; (DO NOT REUSE JVM REPL it will fail weirdly)
  (type 1))
