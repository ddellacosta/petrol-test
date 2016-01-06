(ns petrol-test.client.processing
  (:require
   [cljs.reader :as r]
   [petrol.core :refer [Message EventSource]]
   [petrol-test.client.messages :as m]))

(def initial-state
  (atom {:n 1 #_#_:text-field {:value ""}}))

(def ws-client-id (atom ""))

(defn init-ws-client-id!
  []
  (reset! ws-client-id (.get (goog.net.Cookies. js/document) "ws-client-id")))

(def url "ws://localhost:10000/init-ws")

(defn ws-listen!
  [fn]
  (let [ws (js/WebSocket. url)]
    (set! (.-onmessage ws) fn)
    ws))

(def ws1
  (ws-listen!
   (fn [in]
     (let [[view-sig data] (r/read-string (.-data in))]
       (swap! initial-state assoc view-sig data)
       ))))

(extend-protocol Message
  m/Increment
  (process-message [_ app]
    (update app :n inc)))

(extend-protocol Message
  m/Decrement
  (process-message [_ app]
    (update app :n dec)))

(extend-protocol Message
  m/InputText
  (process-message [{:keys [value]} app]
    (assoc-in app [:text-field :value] value)))

(extend-protocol Message
  m/Unmount
  (process-message [_ app]
    (println "Unmounting!")
    app))
