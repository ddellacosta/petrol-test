(ns petrol-test.server.views
  (:require
   [views.core :as views]
   [views.protocols :refer [IView]]))

(def memory-store
  (atom
   {:default {:users {1 {:user_id 1 :name "Dave"}
                      2 {:user_id 2 :name "Yoko"}
                      3 {:user_id 3 :name "Harvey"}}}
    :views {}
    ;; :send-fn ; set in core
    }))

(defrecord MemoryView [id ks]
  IView
  (id [_] id)
  (data [_ namespace parameters]
    (println "? w " (-> [namespace] (into ks) (into parameters)))
    (get-in @memory-store (-> [namespace] (into ks) (into parameters))))
  (relevant? [_ namespace parameters hints]
    (some #(and (= namespace (:namespace %)) (= ks (:hint %))) hints)))

;; Add "all users" view
(swap! memory-store assoc-in [:views :users] (MemoryView. :users []))
