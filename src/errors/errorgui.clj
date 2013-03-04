(ns errors.errorgui
  (:use [seesaw.core]))

;; Graphics 
(defn show-error [msg]
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