(ns templator.routes.home
  (:require [clj-http.client :as client]
            [clojure.string :as str]
            [compojure.core :refer [defroutes GET POST]]
            [templator.layout :as layout]))

(defn raw-md-url [wiki-url]
  (let [[scheme _ _ team repo _ page] (str/split wiki-url #"/")]
    (str scheme "//raw.githubusercontent.com/wiki/" team "/" repo "/" page ".md")))

(defn home-page []
  (layout/render
    "home.html"))

(defn fill-in-page [wiki-link placeholders]
  (layout/render
   "fill-in.html" {:wiki-link wiki-link :placeholders placeholders}))

(defn result-page [md]
  (layout/render
   "result.html" {:md md}))

(defn fetch-md [wiki-link]
  (-> wiki-link
      raw-md-url
      client/get
      :body))

(defn find-placeholders [md]
  (sort (distinct (map last (re-seq #"\{\{([^\}]*)\}\}" md)))))


(defroutes home-routes
  (GET "/" [] (home-page))
  (POST "/fill-in" [wiki-link]
    (let [placeholders (-> wiki-link fetch-md find-placeholders)]
      (fill-in-page wiki-link placeholders)))
  (POST "/result" {{:keys [wiki-link] :as params} :params}
    (let [md (fetch-md wiki-link)
          placeholders (find-placeholders md)]
      (result-page
       (reduce (fn [md ph]
                 (str/replace md (str "{{" ph "}}") (get params (keyword ph))))
               md placeholders)))))
