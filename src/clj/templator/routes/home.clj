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

(defn fill-in-page [wiki-link placeholders md]
  (layout/render
   "fill-in.html" {:wiki-link wiki-link :placeholders placeholders :md md}))

(defn result-page [md]
  (layout/render
   "result.html" {:md md}))

(defn fetch-md [wiki-link]
  (-> wiki-link
      raw-md-url
      client/get
      :body))

(defn find-placeholders [md]
  (map #(str/split % #"\|")
       (-> last
           (map (re-seq #"\{\{([^\}]*)\}\}" md))
           distinct
           sort
           )))


(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/fill-in" req
    (str/join "<br>"
              (map prn-str [req
                            (:headers req)
                            ((:headers req) "referer")])))
  (POST "/fill-in" [wiki-link]
    (let [md (fetch-md wiki-link)
          placeholders (find-placeholders md)]
      (fill-in-page wiki-link placeholders md)))
  (POST "/result" {{:keys [wiki-link] :as params} :params}
    (let [md (fetch-md wiki-link)
          placeholders (map first (find-placeholders md))]
      (result-page
       (reduce (fn [md ph]
                 (str/replace md (str "{{" ph "}}") (get params (keyword ph))))
               md placeholders)))))
