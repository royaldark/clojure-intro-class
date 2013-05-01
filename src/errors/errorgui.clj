(ns errors.errorgui
  (:use [seesaw.core]))

;; Formatting functions

(defn format-stacktrace [e]
  ; Force Java to give us the preformatted stacktrace as a string
  (let [writer (java.io.StringWriter.)]
    (.printStackTrace e (java.io.PrintWriter. writer))
    (.toString writer)))

;; Graphics 
(defn show-error [msg e]
  (try
     (let [errormsg (text :multi-line? true :editable? false :text msg)
	   stacktrace (text :multi-line? true :editable? false :rows 12 :text (format-stacktrace e))
	   ;hide-button (button :text "Hide")
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
      (invoke-now    
      (native!)
      (scroll! errormsg :to :top) ;; Scrollboxes default to being scrolled to the bottom - not what we want
      (scroll! stacktrace :to :top)
      (.setAlwaysOnTop d true) ;; to make errors pop up on top of other programs
      ;(config! d :content hide-button)
      ;; a mouse anywhere in the window resets it's always-on-top to false
      (listen d :mouse-entered (fn [e] (.setAlwaysOnTop d false)))
      (-> d pack! show!)))

    (catch java.lang.reflect.InvocationTargetException e
      (if (instance? java.awt.HeadlessException (.getCause e))
        ; If there is no GUI available on Windows, this throws an InvocationTargetException
        ; wrapping a HeadlessException - print the error instead of showing a window.
        (println msg)
        ; And if the error does not originate from a HeadlessException, throw it again.
        (throw e)))
    (catch java.awt.HeadlessException e
      ; If there is no GUI available on Linux, it simply throws a HeadlessException - print the erorr.
      (println msg))))
