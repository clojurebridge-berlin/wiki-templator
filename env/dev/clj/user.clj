(ns user
  (:require [mount.core :as mount]
            templator.core))

(defn start []
  (mount/start-without #'templator.core/repl-server))

(defn stop []
  (mount/stop-except #'templator.core/repl-server))

(defn restart []
  (stop)
  (start))


