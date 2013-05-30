(ns errors.core
  (:require [clj-stacktrace.core :as stacktrace])
  (:use [clojure.string :only [join]]
        [errors.dictionaries]
	[errors.errorgui]
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
  	  	  ;; this needs to change
  	  	  ((:make-preobj entry) (re-matches  (:match entry) message)) 
  	  	  (make-preobj-hashes [[message]])))) 

(defn make-obj [pre-obj] ; pre-obj is a vector of hashmaps
  "fills in the starting points of objects in the hash maps"
  (loop [hashes pre-obj start 0 res []]
    (if (empty? hashes) res
      (recur (rest hashes) 
      	     (+ start (:length (first hashes)))
      	     (conj res (assoc (first hashes) :start start))))))

;; All together:
(defn prettify-exception [e]
  (let [info (stacktrace/parse-exception e)
        cljerrs (filter #(and (:clojure %) (not (re-matches ignore-nses (:ns %))))
                        (:trace-elems info))
        errstrs (map #(str "\t" (:ns %) "/" (:fn %) " (" (:file %) " line " (:line %) ")") cljerrs)]
        ;; this needs to change, too
    (show-error (make-obj (concat (make-preobj-hashes [["ERROR: " :err]]) (get-pretty-message e) 
    		    (make-preobj-hashes [[(str "\nPossible causes:\n" (join "\n" errstrs)) :causes]])))
		e)))
