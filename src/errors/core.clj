(ns errors.core
  (:require [clj-stacktrace.core :as stacktrace])
  (:use [clojure.string :only [join]]
        [errors.dictionaries]
	[errors.errorgui]
        [seesaw.core]))

;;(def ignore-nses #"(clojure|java)\..*")
(def ignore-nses #"(user|clojure|java)\..*")

;; Putting together a message (perhaps should be moved to errors.dictionaries? )
(defn- get-pretty-message [e]
  (let [message (.getMessage e)]
    (if-let [entry (some #(when (instance? (:class %) e) %) error-dictionary)]
      (if-let [pretty-message (if message
				(clojure.string/replace message (:match entry) (:replace entry))
				(:emptyMessage entry))]
      pretty-message
      message)
    message)))


;; All together:
(defn prettify-exception [e]
  (let [info (stacktrace/parse-exception e)
        cljerrs (filter #(and (:clojure %) (not (re-matches ignore-nses (:ns %))))
                        (:trace-elems info))
        errstrs (map #(str "\t" (:ns %) "/" (:fn %) " (" (:file %) " line " (:line %) ")") cljerrs)]
    (show-error (str "ERROR: " (get-pretty-message e) "\nPossible causes:\n" (join "\n" errstrs))
		e)))
