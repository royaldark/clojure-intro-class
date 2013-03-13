(ns errors.errorgui
  (:use [seesaw.core]))

;; Graphics 
(defn show-error [msg]
  (try
    (invoke-now
     (let [d (dialog :title "Clojure Error",
		     :content (text :multi-line? true :editable? false :text msg))]
      (native!)     
      (.setAlwaysOnTop d true) ;; to make errors pop up on top of other programs
      (-> d pack! show!)))
    (catch java.lang.reflect.InvocationTargetException e
      (if (instance? java.awt.HeadlessException (.getCause e))
        ; If there is no GUI available, this throws an InvocationTargetException
        ; wrapping a HeadlessException - print the error instead of showing a window.
        (println msg)
        ; And if the error does not originate from a HeadlessException, throw it again.
        (throw e)))))
