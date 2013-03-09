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

(defn format-stacktrace [e]
  ; Force Java to give us the preformatted stacktrace as a string
  (let [writer (java.io.StringWriter.)]
    (.printStackTrace e (java.io.PrintWriter. writer))
    (.toString writer)))

;; Graphics 
(defn- show-error [msg e]
  (try
    (invoke-now
      (native!)
      (let [errormsg (text :multi-line? true :editable? false :text msg)
            stacktrace (text :multi-line? true :editable? false :rows 12 :text (format-stacktrace e))
            d (dialog :title "Clojure Error",
                      :content (tabbed-panel :placement :bottom
                                             :overflow :scroll
                                             :tabs [{:title "Error"
                                                     :tip "The simplified error message"
                                                     :content (scrollable errormsg)}
                                                    {:title "Stacktrace"
                                                     :tip "The full Java stacktrace of the error"
                                                     :content (scrollable stacktrace)}])
                      )]
        (scroll! errormsg :to :top)   ;; Scrollboxes default to being scrolled to the bottom - not what we want
        (scroll! stacktrace :to :top)
        (-> d pack! show!))) ;; adding request-focus! here doesn't work -- Elena
                             ;; request-focus! asks for focus to a certain item WITHIN a window, not focus for an entire window -- Joe
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
    (show-error
      (str "ERROR: " (get-pretty-message e) "\nPossible causes:\n" (join "\n" errstrs))
      e)))
