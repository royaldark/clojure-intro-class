(ns errors.core
  (:require [clj-stacktrace.core :as stacktrace])
  (:use [clojure.string :only [join]]
        [errors.dictionaries]
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

;; Graphics 
(defn- show-error [msg]
  (try
    (invoke-now
      (native!)
      (let [d (dialog :title "Clojure Error",
                      :content (text :multi-line? true :editable? false :text msg))]
        (-> d pack! show!))) ;; adding request-focus! here doesn't work -- Elena
    (catch java.lang.reflect.InvocationTargetException e
      (if (instance? java.awt.HeadlessException (.getCause e))
        ; If there is no GUI available, this throws an InvocationTargetException
        ; wrapping a HeadlessException - print the error instead of showing a window.
        (println msg)
        ; And if the error does not originate from a HeadlessException, throw it again.
        (throw e)))))

;; All together:
(defn prettify-exception [e]
  (let [info (stacktrace/parse-exception e)
        cljerrs (filter #(and (:clojure %) (not (re-matches ignore-nses (:ns %))))
                        (:trace-elems info))
        errstrs (map #(str "\t" (:ns %) "/" (:fn %) " (" (:file %) " line " (:line %) ")") cljerrs)]
    (show-error (str "ERROR: " (get-pretty-message e) "\nPossible causes:\n" (join "\n" errstrs)))))
