(ns petrol-test.client.core
  (:require
   [goog.net.Cookies]
   [petrol.core :as p]
   [petrol-test.client.processing :as pr]
   [petrol-test.client.view :as view]
   [rum.core :as rum]))

(def root-el (.getElementById js/document "app"))

(defn render-fn
  [ui-channel app]
  (rum/mount (view/root ui-channel app) root-el))

(defn ^:export main
  []
  (enable-console-print!)
  (pr/init-ws-client-id!)
  (p/start-message-loop! pr/initial-state render-fn)
  )

(set! (.-onload js/window) main)
