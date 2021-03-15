(ns app.api
  (:require [taoensso.timbre :as log]
            [compojure.core :refer [GET routes]]
            [org.httpkit.server :as server]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.util.http-response :refer [ok]])
  (:gen-class))

(def ^:private quotes
  [{:author/quote "Simplicity matters"
    :author/name "Rich Hickey"}
   {:author/quote "Never trust a computer you can't throw out a window"
    :author/name "Steve Wozniak"}
   {:author/quote "I love deadlines. I love the whooshing noise they make as they go by."
    :author/name "Douglas Adams"}
   {:author/quote "Fools ignore complexity. Pragmatists suffer it. Some can avoid it. Geniuses remove it."
    :author/name "Alan Perlis"}
   {:author/quote "Good design is not about making grand plans, but about taking things apart."
    :author/name "Rich Hickey"}
   {:author/quote "Object-oriented programming is an exceptionally bad idea which could only have originated in California."
    :author/name "Edsger Dijkstra"}
   {:author/quote "Simplicity does not precede complexity, but follows it."
    :author/name "Alan Perlis"}
   {:author/quote "A language that doesn't affect the way you think about programming, is not worth knowing."
    :author/name "Alan Perlis"}])

(defn- ping
  "Route to ping the API. Used in our monitoring system."
  [_]
  (ok {:text "🧙‍♂️"}))

(defn- random-quote
  [_]
  (ok (rand-nth quotes)))

(def ^:private app-routes
  "Common routes for all modes."
  (routes
   (GET "/ping" [] ping)
   (GET "/quote/random" [] random-quote)))







;; ----------------------------------------------------------------------------

(defonce current-server (atom nil))

(defn- stop-server []
  (when-not (nil? @current-server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@current-server :timeout 100)
    (reset! current-server nil)))

(defn -main
  "This is our main entry point for the REST API Server."
  [& _args]
  (reset! current-server
          (server/run-server
           (-> #'app-routes
               (wrap-cors :access-control-allow-origin [#".*"]
                          :access-control-allow-methods [:get :put :post :delete])
               (wrap-restful-format :formats [:transit-json :transit-msgpack :json-kw :edn :msgpack-kw :yaml-kw :yaml-in-html])
               (wrap-defaults api-defaults))
           {:port 3000}))
  (log/info "Server started: http://localhost:3000/ping"))

(comment
  "Start the server from here"
  (-main)
  (stop-server)
  :end)
