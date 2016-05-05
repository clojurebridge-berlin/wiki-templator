(ns templator.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[templator started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[templator has shutdown successfully]=-"))
   :middleware identity})
