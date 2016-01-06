(defproject petrol-test "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"

  :url "http://example.com/FIXME"

  :dependencies
  [
   [aleph "0.4.1-beta2"]
   [cljs-ajax "0.5.2"]
   [compojure "1.4.0"]
   [environ "1.0.1"] ; need this even though it should be included w/views?
   [fogus/ring-edn "0.4.0-SNAPSHOT"]
   [org.clojure/clojure "1.7.0"]
   [org.clojure/clojurescript "1.7.170"]
   [org.clojure/core.async "0.2.374"]
   [petrol "0.1.0"]
   [ring "1.4.0"]
   [rum "0.6.0"]
   [views "1.4.4"]
   ]

  :plugins
  [[lein-cljsbuild "1.1.1"]]

  :cljsbuild
  {:builds
   {:dev
    {;; The path to the top-level ClojureScript source directory:
     :source-paths ["src/petrol_test/client"]

     :compiler
     ;; https://github.com/clojure/clojurescript/wiki/Compiler-Options
     {:output-to "resources/public/cljs/cljsbuild-main.js"  ; default: target/cljsbuild-main.js
      :output-dir "resources/public/cljs" ; this determines the base for compiled dependencies
      :optimizations :none
      :pretty-print true}}}}

  ;; default
  ;;  :source-paths ["src"]

  ;; Determines what `lein clean` is going to wipe.
  :clean-targets ^{:protect false}
  ["resources/public/cljs" "target"])
