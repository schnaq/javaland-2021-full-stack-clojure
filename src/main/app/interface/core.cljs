(ns app.interface.core
  (:require [app.interface.quotes :as quotes]
            [day8.re-frame.http-fx]
            [goog.dom :as gdom]
            [reagent.dom]))

(defn base []
  [:div.container.pt-5
   [:h1 "Jatumba!"]
   [:p.lead "Hallo JavaLand!"]
   [quotes/get-quote]
   [quotes/random-quote]])







;; -- Entry Point -------------------------------------------------------------

(defn render
  []
  (reagent.dom/render [base]
                      (gdom/getElement "app")))

(defn ^:dev/after-load clear-cache-and-render!
  []
  (render))

(defn init
  "Entrypoint into the application."
  []
  (render))