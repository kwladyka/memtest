(ns server.core
  (:require [integrant.core :as ig]
            [bidi.ring :as bidi]
            [ring.util.response :as ring-response]
            [ring.middleware
             [params :refer [wrap-params]]
             [format :refer [wrap-restful-format]]
             [keyword-params :refer [wrap-keyword-params]]
             [json :refer [wrap-json-body wrap-json-response]]]
            [org.httpkit.server :as httpkit]))

(def resources
  {:not-found (constantly (ring-response/not-found (System/getenv "K_REVISION")))})

(def handler
  (bidi/make-handler ["/" {true :not-found}] resources))

(def app-stateless
  (-> handler
      (wrap-keyword-params {:parse-namespaces? true})
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (wrap-params)
      (wrap-restful-format)))

(defmethod ig/prep-key ::server [_ config]
  (merge {:port 80}
         (remove (comp nil? val) config)))

(defmethod ig/init-key ::server [_ opts]
  (httpkit/run-server app-stateless opts))

(defmethod ig/halt-key! ::server [_ server]
  (when server
    (server :timeout 1000)))