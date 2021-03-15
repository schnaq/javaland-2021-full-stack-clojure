(ns app.interface.quotes
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]))

(defn random-quote
  "Display random quote."
  []
  (when-let [quote @(rf/subscribe [:quote/random])]
    [:blockquote.blockquote.jumbotron.mt-5
     [:p.mb-0 (:author/quote quote)]
     [:footer.blockquote-footer
      [:cite (:author/name quote)]]]))

(defn get-quote
  "Query random quote from our backend."
  []
  [:button.btn.btn-outline-primary
   {:on-click #(rf/dispatch [:quote/get])}
   "Quote laden"])







;; ---------------------------------------------------------------------------- 

(rf/reg-event-fx
 :quote/get
 (fn [_ [_]]
   {:fx [[:http-xhrio {:method :get
                       :uri "http://localhost:3000/quote/random"
                       :format (ajax/transit-request-format)
                       :response-format (ajax/transit-response-format)
                       :on-success [:quote/save]
                       :on-failure [:console/error]}]]}))

(rf/reg-event-db
 :quote/save
 (fn [db [_ quote]]
   (assoc-in db [:quote :random] quote)))

(rf/reg-sub
 :quote/random
 (fn [db _]
   (get-in db [:quote :random])))

