(ns experimental.core)
;;;; A space for prototypes, examples, and experimental features.
;; NEVER refer to this file in other files.


;*** examples of Clojure's try/catch system. ***

;the clojuredocs canon example.

(try
     (/ 1 0)
     (catch Exception e (str "caught exception: " (.getMessage e)))
  )

;My example.

(try
  (conj 'a 3)
  (catch Exception e (.getMessage e)))


(defn run-and-catch
  "A function that takes quoted code and runs it, attempting to catch any exceptions it may throw. Returns the exeception or nil. "
  [code] (try
             (doall (eval code))
             (catch Exception e (.getMessage e))))

(run-and-catch '(1 3))
