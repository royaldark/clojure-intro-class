(ns errors.core-test
  (:require [expectations :refer :all]
	    [errors.messageobj :refer :all]))

(expect (make-preobj-hashes) [])

(expect (make-msg-preobj-hash "Hi there")
	{:msg "Hi there" :stylekey :reg :length 8})

(expect (make-msg-preobj-hash "Hi there" :arg)
	{:msg "Hi there" :stylekey :arg :length 8})

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

