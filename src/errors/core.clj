(ns errors.core
  (:require [clj-stacktrace.core :as stacktrace])
  (:use [errors.dictionaries]
	[errors.messageobj]
	[errors.errorgui]
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
    ;; message according to the dictionary and make a msg-info-obj out of it
    ((:make-msg-info-obj entry) (re-matches (:match entry) message))
    ;; else just make a msg-info-obj out the message itself
    (make-msg-info-hashes message)))

(defn filter-stacktrace [stacktrace]
  (filter #(and (:clojure %) (not (re-matches ignore-nses (:ns %))))
                        stacktrace))

;; All together:
(defn prettify-exception [e]
  (let [e-class (class e)
	m (.getMessage e)
	message  (if m m "") ; converting an empty message from nil to ""
	exc (stacktrace/parse-exception e)
	stacktrace (:trace-elems exc)
        filtered-trace (filter-stacktrace stacktrace)]
    ;; create an exception object and pass it to display-error
    (display-error {:exception-class e-class
		    :msg-info-obj (get-pretty-message e-class message)
		    :stacktrace stacktrace
		    :filtered-stacktrace filtered-trace
		    :hints nil})))
