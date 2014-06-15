(ns errors.errorgui
  (:use [seesaw.core]
	[clojure.string :only [join]]
  	[errors.messageobj]))

(def error-prefix "ERROR: ")

;; Swing must be initialized to prefer native skinning before any elements are created
(invoke-now (native!))

;; Formatting functions

(defn format-stacktrace [e]
  ; Force Java to give us the preformatted stacktrace as a string
  (let [writer (java.io.StringWriter.)]
    (.printStackTrace e (java.io.PrintWriter. writer))
    (.toString writer)))

(defn trace-elem->string [trace-elem]
  "Takes a stack trace element from a parsed exception
   and converts it into a string to be displayed"
  ; might need to change to separate handling for
  ; java and coljure elements and add
  ; handling for anonymous functions
  (let [ns (:ns trace-elem)
	ns-not-nil (if ns ns (:class trace-elem))
	fn (:fn trace-elem)
	fn-or-method (if fn fn (:method trace-elem))
	file (:file trace-elem)
	line (:line trace-elem)]
    (str "\t" ns-not-nil "/" fn-or-method " (" file " line " line ")")))

(defn trace->string [trace-elems]
  "Takes a stack trace from a parsed exception
   and converts it into a string to be displayed"
  (map trace-elem->string trace-elems))

(defn- display-msg-object! [msg-obj msg-area]
  "add text and styles from msg-obj to msg-area"
  (doall (map #(style-text! msg-area
                            (:stylekey %)
                            (:start %)
                            (:length %))
              msg-obj)))



;; Graphics
;; msg-obj will contain parts and styles and lengths
(defn display-error [exc-obj]
  (let [msg-obj  (:message-object exc-obj)]
  (try
    (let ;; styles for formatting various portions of a message
	[
	 trace (:stacktrace exc-obj)
	 styles [[:arg :font "monospace" :bold true] [:reg] [:stack] [:err] [:type] [:causes]]
	 errormsg (styled-text :wrap-lines? true :text (str (get-all-text msg-obj)  (:filtered-trace exc-obj))
			       :styles styles)
	 stacktrace (text :multi-line? true :editable? false :rows 12 :text
			  (join "\n" (trace->string trace)))
	 d (dialog :title "Clojure Error",
                 :content (tabbed-panel :placement :bottom
                                        :overflow :scroll
                                        :tabs [{:title "Error"
                                                :tip "The simplified error message"
                                                :content (do (display-msg-object! msg-obj errormsg)
							     (scrollable (str errormsg)))}
									     
                                               {:title "Stacktrace"
                                                :tip "The full Java stacktrace of the error"
                                                :content (scrollable stacktrace)}])
                 )]
      ;(println exc-obj) ; debug print
      (invoke-now
        (scroll! errormsg :to :top) ;; Scrollboxes default to being scrolled to the bottom - not what we want
        (scroll! stacktrace :to :top)
        (.setAlwaysOnTop d true) ;; to make errors pop up on top of other programs
        ;; a mouse anywhere in the window resets it's always-on-top to false
        (listen d :mouse-entered (fn [e] (.setAlwaysOnTop d false)))
        (-> d pack! show!)))

    (catch java.lang.reflect.InvocationTargetException e
      (if (instance? java.awt.HeadlessException (.getCause e))
        ; If there is no GUI available on Windows, this throws an InvocationTargetException
        ; wrapping a HeadlessException - print the error instead of showing a window.
        (println (get-all-text msg-obj))
        ; And if the error does not originate from a HeadlessException, throw it again.
        (throw e)))
    (catch java.awt.HeadlessException e
      ; If there is no GUI available on Linux, it simply throws a HeadlessException - print the erorr.
      (println (get-all-text msg-obj))))))

