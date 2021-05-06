(defproject online-calculator "0.1.0-SNAPSHOT"
  :description "An online calculator"
  :url "https://github.com/phalphalak/online-calculator"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [instaparse "1.4.10"]
                 [org.clojure/data.json "2.2.2"]
                 [org.clojure/test.check "0.10.0"]
                 [ring/ring-core "1.9.2"]
                 [ring/ring-jetty-adapter "1.9.2"]
                 [ring/ring-defaults "0.3.2"]
                 [compojure "1.6.2"]]
  :uberjar-name "online-calculator-standalone.jar"
  :min-lein-version "2.9.6"
  :main online-calculator.core
  :profiles {:uberjar {:aot :all}}
  :repl-options {:init-ns online-calculator.core})
