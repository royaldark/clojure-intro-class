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

;; Putting together a message (perhaps should be moved to errors.dictionaries? 
(defn- get-pretty-message [e]
  (let [m (.getMessage e)
  	message (if m m "")] ; converting an empty message from nil to ""
    (if-let [entry (first-match e message)]
      ;; if there's a match for the exception and the message, replace the
      ;; message according to the dictionary and make a message
      ;; pre-obj out of it
      ((:make-preobj entry) (re-matches  (:match entry) message))
      ;; else just make a message pre-obj out the message itself
      (make-preobj-hashes message))))

(defn filter-stacktrace [exc]
  (filter #(and (:clojure %) (not (re-matches ignore-nses (:ns %))))
                        (:trace-elems exc)))

(defn trace-elem->string [trace-elem]
  (#(str "\t" (:ns %) "/" (:fn %) " (" (:file %) " line " (:line %) ")") trace-elem))

;; All together:
(defn prettify-exception [e]
  ;; create an exception object
  (let [exc (stacktrace/parse-exception e)
        filtered-trace (filter-stacktrace exc) 
        errstrs (map trace-elem->string filtered-trace)]
    ;; pass it to display-error
    (show-error (make-obj (concat (make-preobj-hashes error-prefix :err) (get-pretty-message e) 
    		    (make-preobj-hashes (str "\nSequence of function calls:\n" (join "\n" errstrs)) :causes)))
		e)))
 