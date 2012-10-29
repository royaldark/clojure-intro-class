(ns errors.core
  (:require [clj-stacktrace.core :as stacktrace])
  (:use [clojure.string :only [join]]))

(defn prettify-exception [e]
  (let [info (stacktrace/parse-exception e)
        cljerrs (filter #(and (:clojure %)
                              (not (re-matches #"(clojure|java)\..*" (:ns %)))) (:trace-elems info))
        errstrs (map #(str "\t" (:ns %) "/" (:fn %) " (" (:file %) " line " (:line %) ")") cljerrs)]
    (str "ERROR: " (:message info) "\nPossible causes:\n" (join "\n" errstrs))))
