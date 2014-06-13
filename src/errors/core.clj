(ns errors.core
  (:require [clj-stacktrace.core :as stacktrace])
  (:use [errors.dictionaries]
	      [errors.errorgui]
	      [errors.messageobj]
        [seesaw.core]))

;;(def ignore-nses #"(clojure|java)\..*")
(def ignore-nses #"(user|clojure|java)\..*")

(defn first-match [e-class message]
	;(println (str e-class " " message)) ; debugging print
	(first (filter #(and (= (:class %) e-class) (re-matches (:match %) message))
			error-dictionary)))

;; Putting together a message (perhaps should be moved to errors.dictionaries?
(defn get-pretty-message [e-class message]
  (if-let [entry (first-match e-class message)]
    ;; if there's a match for the exception and the message, replace the
    ;; message according to the dictionary and make a message
    ;; pre-obj out of it
    ((:make-preobj entry) (re-matches (:match entry) message))
    ;; else just make a message pre-obj out the message itself
    (make-preobj-hashes message)))

(defn filter-stacktrace [exc]
  (filter #(and (:clojure %) (not (re-matches ignore-nses (:ns %))))
                        (:trace-elems exc)))

;; All together:
(defn prettify-exception [e]
  (let [e-class (class e)
	      m (.getMessage e)
	      message  (if m m "") ; converting an empty message from nil to ""
	      exc (stacktrace/parse-exception e)
	      stack-trace (:trace-elems exc)
        filtered-trace (filter-stacktrace exc)
        errstrs (map trace-elem->string filtered-trace)]
    ;; create an exception object and pass it to display-error
    (display-error {:exception-class e-class
		                :message-object (make-obj (concat (make-preobj-hashes error-prefix :err)
						                                          (get-pretty-message e-class message)))
		                :stacktrace stack-trace
		                :filtered-stacktrace filtered-trace
		                :hints nil})))
    ;(show-error (make-obj (concat (make-preobj-hashes error-prefix :err) (get-pretty-message e)
    ;		    (make-preobj-hashes (str "\nSequence of function calls:\n" (join "\n" errstrs)) :causes)))
    ;e)
