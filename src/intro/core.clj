(ns intro.core
  (:require [errors.core :as errors])
  (:use [corefns.core]))

(defn -main [& args]
  (try
    (map 2 [1 2 3])
    (catch Throwable e (println (errors/prettify-exception e)))))
