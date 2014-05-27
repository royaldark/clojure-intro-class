(ns experimental.core_test
  (:require [clj-stacktrace.core :as stacktrace])
  (:require [expectations :refer :all]
            [errors.messageobj :refer :all]
            [errors.core :refer :all]))

;;;; A space for prototypes, examples, and experimental features.
;; NEVER refer to this file in other files.

;***********************************************
;*** examples of Clojure's try/catch system. ***

;the clojuredocs canon example.

(expect "caught exception: Divide by zero"
        (try
          (/ 1 0)
          (catch Exception e (str "caught exception: " (.getMessage e)))))

;My example.

(expect "clojure.lang.Symbol cannot be cast to clojure.lang.IPersistentCollection"
        (try
          (conj 'a 3)
          (catch Exception e (.getMessage e))))

;************************************
;*** try/catch function prototype ***.

(defn run-and-catch
  "A function that takes quoted code and runs it, attempting to catch any exceptions it may throw. Returns the exeception or nil."
  [code] (try
             (eval code)
             (catch Exception e e)))

;a helper function to cleanly test the above.
(defn- exception->string
  "Converts exceptions to strings, returning a string or the original e (if it is not an exception)"
  [e] (if (instance? Exception e)
                                (.getMessage e)
                                e))

(expect "java.lang.Long cannot be cast to clojure.lang.IFn"
        (exception->string (run-and-catch '(1 3))))

(expect 3
        (exception->string (run-and-catch '(+ 1 2))))

;************************************

(def st1 (stacktrace/parse-exception (run-and-catch '(1))))

(last (stacktrace/parse-exception (run-and-catch '(1))))


(def st2 (stacktrace/parse-exception (run-and-catch '(+ 2 "pie"))))

(def st3 (stacktrace/parse-exception (run-and-catch '(5))))

(def q 2)

(def fst1 (map #(str "\t" (:ns %) "/" (:fn %) " (" (:file %) " line " (:line %) ")")(filter #(and (:clojure %) (not (re-matches ignore-nses (:ns %)))) (:trace-elems st1))))

(def fst2 (map #(str "\t" (:ns %) "/" (:fn %) " (" (:file %) " line " (:line %) ")")(filter #(and (:clojure %) (not (re-matches ignore-nses (:ns %)))) (:trace-elems st2))))

(def fst3 (map #(str "\t" (:ns %) "/" (:fn %) " (" (:file %) " line " (:line %) ")")(filter #(and (:clojure %) (not (re-matches ignore-nses (:ns %)))) (:trace-elems st3))))












