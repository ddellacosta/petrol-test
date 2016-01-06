(ns petrol-test.server.core
  (:require
   [aleph.http :as http]
   [compojure.core :as compojure :refer [GET POST PUT]]
   [compojure.route :as route]
   [manifold.deferred :as d]
   [manifold.stream :as s]
   [petrol-test.server.views :as pv]
   [ring.middleware.edn :as edn]
   [ring.middleware.params :as params]
   [ring.middleware.resource :as resource]
   [ring.middleware.session :as session]
   [ring.util.response :refer [resource-response]]
   [views.core :as views]
   ))


(defn edn-response
  [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/edn"}
   :body (pr-str data)})

(defn reverse-str
  [sink m] (s/put! sink (reduce #(str %2 %1) "" m)))

(def sub-cons (atom {}))

(defn init-ws-handler
  [req]
  (let [s    @(http/websocket-connection req)
        uuid (get-in req [:session :ws-client-id])]
    (swap! sub-cons assoc uuid s)
    ))
    ;; (s/connect-via s (partial reverse-str s) s)))

(compojure/defroutes routes
  (GET "/" req
       (let [uuid (.toString (java.util.UUID/randomUUID))]
         (-> (resource-response "index.html" {:root "public"})
             (assoc-in [:session :ws-client-id] uuid)
             (assoc-in [:cookies :ws-client-id] uuid))))
  (GET "/init-ws" req init-ws-handler)
  (GET "/test" [] "Test!")
  (POST "/subscribe" {{:keys [name params]} :params :as req}
        (let [ws-client-id (get-in req [:session :ws-client-id])]
          (views/subscribe! pv/memory-store :default name params ws-client-id)
         ;; (println "view sub " params)
         ;; (println "REQ: " req)
         ;; (println "\n\n")
         ;; (println "views: " @pv/memory-store)
         ;; (println "\n\n")
          (edn-response {:yes "yes"})))
  (route/not-found "No such page."))

(def handler
  (-> routes
      ;; params/wrap-params
      edn/wrap-edn-params
      session/wrap-session
      (resource/wrap-resource "public")))

(defn send-to-subscriber
  [sub-cons]
  (fn [subscriber-key data]
    (d/let-flow [ws-sink (get @sub-cons subscriber-key)
                 dd (d/deferrable? data)
                 p (println "DD? " dd)
                 p (println "WS-? " ws-sink)
                 put?    @(s/put! ws-sink (pr-str data))]
      (println "ws-sink? " ws-sink)
      (println "success? " put?)
      (println (str "sending to:" subscriber-key
                    ", data:" data
                    ", via " (get @sub-cons subscriber-key))))))

(defn start
  []
  (swap! pv/memory-store assoc :send-fn (send-to-subscriber sub-cons))
  (views/update-watcher! pv/memory-store 1000 1)
  (http/start-server #'handler {:port 10000}))
