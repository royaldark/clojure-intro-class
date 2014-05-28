(ns errors.core
  (:require [clj-stacktrace.core :as stacktrace])
  (:use [clojure.string :only [join]]
        [errors.dictionaries]
	      [errors.errorgui]
	      [errors.messageobj]
        [seesaw.core]))

;;(def ignore-nses #"(clojure|java)\..*")
(def ignore-nses #"(user|clojure|java)\..*")

(defn- first-match [e message]
	(println (str (class e) " " message)) ; debugging print
	(first (filter #(and (instance? (:class %) e) (re-matches (:match %) message))
			error-dictionary)))

;; Putting together a message (perhaps should be moved to errors.dictionaries? )
(defn- get-pretty-message [e]
  (let [m (.getMessage e)
  	message (if m m "")] ; converting an empty message from nil to ""
  	  (if-let [entry (first-match e message)]
  	  	  ((:make-preobj entry) (re-matches  (:match entry) message)) 
  	  	  (make-preobj-hashes message))))

(defn filter-stacktrace [trace]
  (filter #(and (:clojure %) (not (re-matches ignore-nses (:ns %))))
                        (:trace-elems trace)))


;; All together:
(defn prettify-exception [e]
  (let [trace (stacktrace/parse-exception e)
        filtered-trace (filter-stacktrace trace) 
        errstrs (map #(str "\t" (:ns %) "/" (:fn %) " (" (:file %) " line " (:line %) ")") filtered-trace)]
    (show-error (make-obj (concat (make-preobj-hashes error-prefix :err) (get-pretty-message e) 
    		    (make-preobj-hashes (str "\nSequence of function calls:\n" (join "\n" errstrs)) :causes)))
		e)))
 