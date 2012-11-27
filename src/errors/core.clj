(ns errors.core
  (:require [clj-stacktrace.core :as stacktrace])
  (:use [clojure.string :only [join]]
        [errors.dictionaries]
        [seesaw.core]))

(defn- get-pretty-message [e]
  (if-let [entry (some #(when (instance? (:class %) e) %) error-dictionary)]
    (if-let [pretty-message (clojure.string/replace (.getMessage e) (:match entry) (:replace entry))]
      pretty-message
    (.getMessage e))
  (.getMessage e)))

(defn- show-error [msg]
  (invoke-later
    (native!)
    (let [d (dialog :title "Clojure Error",
                   :content (text :multi-line? true :editable? false :text msg))]
      (-> d pack! show!))))

(defn prettify-exception [e]
  (let [info (stacktrace/parse-exception e)
        cljerrs (filter #(and (:clojure %)
                              (not (re-matches #"(clojure|java)\..*" (:ns %)))) (:trace-elems info))
        errstrs (map #(str "\t" (:ns %) "/" (:fn %) " (" (:file %) " line " (:line %) ")") cljerrs)]
    (show-error (str "ERROR: " (get-pretty-message e) "\nPossible causes:\n" (join "\n" errstrs)))))
