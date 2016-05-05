(ns templator.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [templator.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[templator started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[templator has shutdown successfully]=-"))
   :middleware wrap-dev})
