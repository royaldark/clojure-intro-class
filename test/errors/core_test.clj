(ns errors.core-test
  (:require [expectations :refer :all]
	    [errors.messageobj :refer :all]
      [errors.exceptionobj :refer :all]))

;;1. Functions
;;2. Prebuilt Exceptions
;;3. errors.messageobj
;;4. errors.exceptionobj

;####################
;### 1. Functions ###
;####################

(defn ignore-stacktrace [trace1 trace2] true)


;###############################
;### 2. Prebuilt Exceptions ####
;###############################

(defn- run-and-catch
  "A function that takes quoted code and runs it, attempting to catch any exceptions it may throw. Returns the exeception or nil."
  [code] (try
             (eval code)
             (catch Exception e e)))

(defn- exception->string
  "Converts exceptions to strings, returning a string or the original e if it is not an exception"
  [e] (if (instance? Exception e)
                                (.getMessage e)
                                e))

(def function-call-on-value (run-and-catch '(4 + "pie")))

(def class-cast-exception (run-and-catch '(+ 4 "pie")))



;##################################
;### 3. errors.messageobj tests ###
;##################################

;***Testing make-preobj-hashes***

(expect (make-preobj-hashes) [])

;***Testing make-msg-preobj-hash***

(expect (make-msg-preobj-hash "Hi there")
	{:msg "Hi there" :stylekey :reg :length 8})

(expect (make-msg-preobj-hash "Hi there" :arg)
	{:msg "Hi there" :stylekey :arg :length 8})

(expect (make-msg-preobj-hash "Blue Jeans")
 {:msg "Blue Jeans", :stylekey :reg, :length 10})

(expect (make-msg-preobj-hash "Bootcut" :hippy)
 {:msg "Bootcut", :stylekey :hippy, :length 7})

;***Testing make-preobj-hashes***

(expect (make-preobj-hashes "Hi there")
	[{:msg "Hi there" :stylekey :reg :length 8}])

(expect (make-preobj-hashes "Hi there" :arg)
	[{:msg "Hi there" :stylekey :arg :length 8}])

(expect (make-preobj-hashes "Hi there" "Hello")
	[{:msg "Hi there" :stylekey :reg :length 8}
	 {:msg "Hello" :stylekey :reg :length 5}])

(expect (make-preobj-hashes "Hi there" :arg "Hello" :blah)
	[{:msg "Hi there" :stylekey :arg :length 8}
	 {:msg "Hello" :stylekey :blah :length 5}])

(expect (make-preobj-hashes "Blue Jeans"
                             "Khakis" :business-casual
                             "Bootcut" :hippy
                             "Jeggings" :casual)

 [{:msg "Blue Jeans", :stylekey :reg, :length 10}
  {:msg "Khakis", :stylekey :business-casual, :length 6}
  {:msg "Bootcut", :stylekey :hippy, :length 7}
  {:msg "Jeggings", :stylekey :casual, :length 8}])

;***Testing make-obj***

(expect (make-obj (make-preobj-hashes "Hi there" :arg "Hello"))
	[{:msg "Hi there" :stylekey :arg :length 8 :start 0}
	 {:msg "Hello" :stylekey :reg :length 5 :start 8}])

(expect (make-obj [{:msg "Blue Jeans", :stylekey :reg, :length 10}
                   {:msg "Khakis", :stylekey :business-casual, :length 6}
                   {:msg "Bootcut", :stylekey :hippy, :length 7}
                   {:msg "Jeggings", :stylekey :casual, :length 8}])

 [{:start 0, :msg "Blue Jeans", :stylekey :reg, :length 10}
  {:start 10, :msg "Khakis", :stylekey :business-casual, :length 6}
  {:start 16, :msg "Bootcut", :stylekey :hippy, :length 7}
  {:start 23, :msg "Jeggings", :stylekey :casual, :length 8}])

;***Testing get-all-text***

(expect (get-all-text (make-obj (make-preobj-hashes "Hi there! " :arg "Hello")))
	"Hi there! Hello")

;***Testing make-mock-preobj***

(expect (make-mock-preobj ["anything" "will be ignored"])
-	[{:msg "This is a" :stylekey :reg :length 9}
	 {:msg "test" :stylekey :arg :length 4}])

;####################################
;### 4. errors.exceptionobj tests ###
;####################################

