(ns petrol-test.client.view
  (:require
   [ajax.core :refer [POST]]
   [ajax.edn]
   [cljs.core.async :as async]
   [petrol.core :as p]
   [petrol-test.client.messages :as m]
   [petrol-test.client.processing :as pr]
   [rum.core :as rum]))

(def unsubscribe
  {:will-unmount (fn [{[ui-channel _] :rum/args :as s}]
                   (async/put! ui-channel (m/->Unmount)))})

(rum/defc odd < unsubscribe
  [ui-channel n]
  [:div "Odd: " n])

(rum/defc even < unsubscribe
  [ui-channel n]
  [:div "Even: " n])

(rum/defc text-input
  [ui-channel {:keys [value]}]
  (let [class "text-field", name "Text field"]
    [:div.text-field
     [:label {:for class} name]
     [:input {:class class :type :text :name name :defaultValue value :value value
              :on-change (p/send-value! ui-channel m/->InputText)}]]))

(defn subscribe!
  [_]
  ;; Post a transit format message
  (POST "/subscribe"
        {:params {:name :users :params {}}
         ;; :format :edn ; why deprecated?
         ;; because: https://github.com/JulianBirch/cljs-ajax/issues/107 
         :format (ajax.edn/edn-request-format)
         :response-format (ajax.edn/edn-response-format)}))

(rum/defc root
  [ui-channel app]
  [:div
   (text-input ui-channel (:text-field app))

   [:div [:button {:style #js {:cursor "pointer"}
                   :on-click subscribe!}
          "Subscribe"]]

   [:div.users
    (for [[id u] (get-in app [[:users {}] :users])]
      [:div.user [:span.name (:name u)]])]

#_   [:div "Hello world: " (:n app)

    (if (odd? (:n app))
      (odd ui-channel (:n app))
      (even ui-channel (:n app)))

    [:div
     [:button {:style #js {:cursor "pointer"}
               :on-click #(.send pr/ws1 (pr-str {:ws-client-id @pr/ws-client-id}))}
      "Test WS"]]

    [:div [:button {:style #js {:cursor "pointer"} :on-click (p/send! ui-channel (m/->Increment))} "Increment"]]

    [:div [:button {:style #js {:cursor "pointer"} :on-click (p/send! ui-channel (m/->Decrement))} "Decrement"]]]])
