(ns errors.errorgui
  (:use [seesaw.core]))

;; Formatting functions

(defn format-stacktrace [e]
  ; Force Java to give us the preformatted stacktrace as a string
  (let [writer (java.io.StringWriter.)]
    (.printStackTrace e (java.io.PrintWriter. writer))
    (.toString writer)))

(defn- display-msg-object! [msg-obj msg-area] 
  "adding text and styles from msg-obj to msg-area"
  (println "AND THE MESSAGE IS:" msg-obj)
  (doall (map #(style-text! msg-area 
  			  (:stylekey %)
  		          (:start %) 
  		          (:length %)) 
  		  msg-obj)))

(defn- get-all-text [msg-obj]
   (reduce #(str (:msg %1) %2) msg-obj))

;; Graphics 
;; msg-obj will contain parts and styles and lengths 
(defn show-error [msg-obj e]
  (try
     (let ;; styles for formatting various portions of a message
     	  [styles [[:arg :italic true] [:reg] [:stack] [:err] [:type] [:causes]]
           errormsg (styled-text :wrap-lines? true :text (get-all-text msg-obj) :styles styles)
	   stacktrace (text :multi-line? true :editable? false :rows 12 :text (format-stacktrace e))
           d (dialog :title "Clojure Error",
                     :content (tabbed-panel :placement :bottom
                                             :overflow :scroll
                                             :tabs [{:title "Error"
                                                     :tip "The simplified error message"
                                                     :content (do (display-msg-object! msg-obj errormsg) errormsg)}
                                                    {:title "Stacktrace"
                                                     :tip "The full Java stacktrace of the error"
                                                     :content (scrollable stacktrace)}])
                      )]
      (invoke-now    
      (native!)
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
      (println (get-all-text msg-obj)))))
