(ns api.core
  (:gen-class)
  (:require
    [integrant.core :as ig]
    [server.core :as server]
    ;org.httpkit.client
    ;[org.httpkit.sni-client :as sni-client]
    ;[selmer.parser :as selmer]
    [clojure.tools.logging :as l]))

;; this is needed, because of SNI client, otherwise exception Received fatal alert: handshake_failure
;(alter-var-root #'org.httpkit.client/*default-client* (fn [_] sni-client/default-client))

;; no reason to cache labels ?
;(selmer/cache-off!)

(def config
  {::server/server {:port (some-> (System/getenv "PORT") (Integer/parseInt))}})

(defonce system (atom nil))

(defn start-system []
  (when-not @system
    (let [final-config (ig/prep config)]
      (l/debug "integrant config\n" final-config)
      (reset! system (ig/init final-config)))))

(defn -main []
  (l/info "app start K_REVISION:" (System/getenv "K_REVISION"))
  (start-system))