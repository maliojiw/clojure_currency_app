(ns my-web-app.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hiccup.page :refer [html5]]
            [clj-http.client :as client]
            [cheshire.core :as cheshire]
            [ring.util.response :as response]
            [environ.core :refer [env]]))  
 
(def api-key (env :API_KEY))
(def api-key (System/getenv "API_KEY"))
(def base-url "https://v6.exchangerate-api.com/v6/")


(defn fetch-exchange-rates []
  (let [url (str base-url api-key "/latest/USD")] 
    (println "Fetching exchange rates from URL: " url)
    (let [response (try
                     (client/get url)
                     (catch Exception e
                       (println "Error fetching data: " (.getMessage e))
                       {:status 500 :body "Error fetching exchange rates"}))]
      (println "Response status: " (:status response))
      (if (= 200 (:status response))
        (let [body (cheshire/parse-string (:body response) true)
              rates (:conversion_rates body)]
          (let [cleaned-rates (into {} (map (fn [[k v]] [(name k) v]) rates))]
            (select-keys cleaned-rates ["EUR" "GBP" "JPY" "AUD" "CAD"])))
        (do
          (println "Error in fetching exchange rates: " (:body response))
          {:error "Failed to fetch exchange rates"})))))



(defn exchange-rates-page []
  (let [rates (fetch-exchange-rates)]
    (html5
     [:html
      [:head
       [:title "Currency Exchange Rates"]
       [:script
        "function fetchExchangeRates() {
           var xhr = new XMLHttpRequest();
           xhr.open('GET', '/exchange-rates', true);
           xhr.onload = function() {
               if (xhr.status === 200) {
                   var rates = JSON.parse(xhr.responseText);
                   var list = document.getElementById('rates-list');
                   list.innerHTML = '';  
                   for (var currency in rates) {
                       var li = document.createElement('li');
                       li.textContent = currency + ': ' + rates[currency];
                       list.appendChild(li);
                   }
               }
           };
           xhr.send();
        }

        setInterval(fetchExchangeRates, 30000);
        fetchExchangeRates();"]]
      [:body
       [:h1 "Currency Exchange Rates"]
       [:ul {:id "rates-list"}
        (for [[currency rate] rates]
          [:li (str currency ": " rate)])]]])))

;; Define the routes for the app
(defroutes app-routes
  (GET "/" [] (exchange-rates-page))
  (GET "/exchange-rates" [] (response/content-type
                             (response/response (cheshire/generate-string (fetch-exchange-rates)))
                             "application/json"))
  (route/not-found "Page not found"))

(def app
  (wrap-defaults app-routes site-defaults))

(defn -main [& args]
  (run-jetty app {:port 8000 :join? false}))
