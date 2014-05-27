(ns experimental.core_test
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
(defn- exeception->string
  "Converts execeptions to strings, returning a string or the original "
  [e] (if (instance? Exception e)
                                (.getMessage e)
                                e))

(expect "java.lang.Long cannot be cast to clojure.lang.IFn"
        (exeception->string (run-and-catch '(1 3))))

(expect 3
        (exeception->string (run-and-catch '(+ 1 2))))

;*************************************
;*** Using the above with errors.core ***.


;!!!!DANGER!!!!
;if opened in a repl, it will launch infinite windows.

;(comment
(expect :success
        (prettify-exception (run-and-catch '(2))))

;  )

;holy ****!
;that's annoying.
;we really need a version of prettify-exception that's not throwing windows at my face.

;temporary workspace
;*************************************
;Testing the message object stuff.


;Tests for make-msg-preobj-hash

(expect {:msg "Blue Jeans", :stylekey :reg, :length 10}
        (make-msg-preobj-hash "Blue Jeans"))

(expect {:msg "Bootcut", :stylekey :hippy, :length 7}
        (make-msg-preobj-hash "Bootcut" :hippy))


;Tests for make-preobj-hashes

(expect [{:msg "Blue Jeans", :stylekey :reg, :length 10}
         {:msg "Khakis", :stylekey :business-casual, :length 6}
         {:msg "Bootcut", :stylekey :hippy, :length 7}
         {:msg "Jeggings", :stylekey :casual, :length 8}]
        (make-preobj-hashes "Blue Jeans"
                            "Khakis" :business-casual
                            "Bootcut" :hippy
                            "Jeggings" :casual))

;Tests for make-obj

(expect [{:start 0, :msg "Blue Jeans", :stylekey :reg, :length 10}
         {:start 10, :msg "Khakis", :stylekey :business-casual, :length 6}
         {:start 16, :msg "Bootcut", :stylekey :hippy, :length 7}
         {:start 23, :msg "Jeggings", :stylekey :casual, :length 8}]
        (make-obj [{:msg "Blue Jeans", :stylekey :reg, :length 10}
                   {:msg "Khakis", :stylekey :business-casual, :length 6}
                   {:msg "Bootcut", :stylekey :hippy, :length 7}
                   {:msg "Jeggings", :stylekey :casual, :length 8}]))













