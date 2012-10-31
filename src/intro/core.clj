(ns intro.core
  (:require [errors.core :as errors])
  (:use [corefns.core]
        [seesaw.core]))

(defn basic-seesaw-frame []
  (invoke-later
    (def f (frame :title "Hello",
                  :content (button :text "Seesaw Rocks!"),
                  :on-close :exit))
    (-> f pack! show!)))  

(defn -main [& args]
  (try
    (basic-seesaw-frame)
    (map 2 [1 2 3])
    (catch Throwable e (println (errors/prettify-exception e)))))
