(defproject my-web-app "0.1.0-SNAPSHOT"
  :description "A simple Compojure web app"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.11.1"]
               [compojure "1.6.2"]
               [ring/ring-defaults "0.3.2"]
               [ring/ring-jetty-adapter "1.9.2"]
               [clj-http "3.12.3"]
               [cheshire "5.10.1"]
               [hiccup "1.0.5"]
               [ring/ring-core "1.9.4"]
               [cprop "0.1.17"]
               [environ "1.2.0"]
               ]
  :plugins [[lein-ring "0.12.5"]
            [lein-environ "1.2.0"]]
  :ring {:handler my-web-app.handler/app}
  :main my-web-app.handler
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}
   :env {:API_KEY "9e90df74f2924ad7bf4a568d"}})