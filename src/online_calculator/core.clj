(ns online-calculator.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as resp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [online-calculator.parse :refer [parse solve]]
            [clojure.data.json :as json])
  (:import java.util.Base64)
  (:gen-class))

(defn decode [s]
  (String. (.decode (Base64/getDecoder) s)))

(defn response-422 [message]
  {:status 422
   :headers {"Content-Type" "application/json"}
   :body (json/write-str {:error true
                          :message message})})

(defn result-response [query]
  (let [result (solve (parse query))
        error (and (map? result) (:error result))]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/write-str (merge {:error (not (not error))}
                                  (if error
                                    {:message error}
                                    {:result result})))}))

(defn handler [{:keys [query-params] :as x}]
  (let [query (query-params "query")
        [valid? decoded-query] (try [true (decode query)]
                                    (catch Exception e [false nil]))]
    (if query
      (if valid?
        (result-response decoded-query)
        (response-422 "invalid base64"))
      (response-422 "missing query param"))))

(defroutes app-routes
  (GET "/calculus" [] handler)
  (route/not-found "Not Found"))

(def app (wrap-defaults app-routes site-defaults))

(def server nil)

(defn- start! [& [port]]
  (let [port (Integer. (or port
                           (System/getenv "PORT")
                           8080))]
    (def server (jetty/run-jetty #'app {:port  port
                                        :join? false}))))

(defn- stop! []
  (.stop server))

(defn -main
  [& [port]]
  (start! port))
