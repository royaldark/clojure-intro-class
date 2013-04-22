(ns intro.core 
  (:require [errors.core :as errors])
  (:use [corefns.core]
        [seesaw.core]
	[turtle.core]))
        ;[turtle.extended-turtle]))

(defn basic-seesaw-frame []
  (invoke-later
    (native!)
    (def f (frame :title "Hello",
                  :content (button :text "Seesaw Rocks!"),
                  :on-close :exit))
    (-> f pack! show!))) 


(defn test-and-continue [quoted-exp]
  (in-ns 'intro.core) ; eval by default evaluates in its own namespace
  (try
    (println quoted-exp)
    (eval quoted-exp)
    (catch Throwable e (println (errors/prettify-exception e)))
    (finally (println "Are there any exceptions left?"))))

(defn test-all-and-continue [quoted-exps]
  ;; doall is needed because map is lazy 
  (doall (map test-and-continue quoted-exps))) 

(defn test-arithmetic-expressions []
  ;; 3 exceptions thrown:
  (test-all-and-continue '((+ \a 2) (< 'a 8) (/ 5 0))))

;; Some of the error messages below would change once we create enough preconditions
(defn test-sequences []
  (test-all-and-continue '( ;(nth 0 [1 2 3]) ;; attempted to use collection but number was expected 
			    ;(nth '(1 2 3) 7) ;; An index in a vector or a list is out of bounds
			    (into 6 [1 2]) ;; attempted to use number but collection was expected
			    (into {} [1 2 3]) ;; don't know how to create sequence from number. into on a hashmap requires a collection of pairs.
			    (into {} [1 2 3 4]) ;; same as above. A correct one would be (into {} [[1 2] [3 4]])
			    (conj "a" 2) ;; Attempted to use string, but collection was expected.
			    )))

(defn test-nth []
  (test-all-and-continue '( (nth 3 [1 2 3]) ;; ERROR: Attempted to use a collection, but a number was expected.
			(nth [1 2 3] 3)
			(nth [1 2 3] [8 9])
			(nth #{1 2 3} 1 )))) 

(defn test-exceptions []
  (test-all-and-continue '((throw (new IndexOutOfBoundsException))
			   (throw (new IndexOutOfBoundsException "10"))
			   (throw (new NullPointerException))
			   (throw (new NullPointerException "some message")))))

(defn test-concat []
  (test-all-and-continue '((doall (concat [1 2] :banana)) ; need doall because concat is lazy
			   (doall (concat [:banana] +))
			   (doall (concat [:banana] [:banana] [] 4))
			   )))

(defn test-first-rest []
  (test-all-and-continue '((first 1) ; Don't know how to create a sequence from a number
			   (rest 1) ; Don't know how to create a sequence from a number
			   (first []) ;; returns nil
			   (rest []) ;; returns an empty sequence
			   (empty? 1)))) ;Don't know how to create a sequence from a number

(defn test-conj-into []
  (test-all-and-continue '((conj 1 []) ; Attempted to use a number, but a collection was expected.
			   (into [2] 3) ; Don't know how to create a sequence from a number
			   (conj + 1) ; Attempted to use a function, but a collection was expected.
			   (into '() *)))) ; Don't know how to create a sequence from a function

(defn test-add-first-last []
  (test-all-and-continue '((add-first 1 []) ; currently failing asserts, this may change later
			   (add-last 1 [])
			   (add-first + 1)
			   (add-first \s "pie")
			   (add-last \s "ies")
			   (add-first :k [:a])
			   (add-last :k [:a])
			   (add-last + 1))))

(defn test-forgetting-a-quote []
  (test-all-and-continue '((1 2 3) ; Attempted to use a number, but a function was expected.
			   ([] 1 2) ; Wrong number of args (2) passed to: PersistentVector - hmmm
			   ([2 4] 1) ; Not an error, returns 4
			   ([] 1)))) ; An index in a vector or a list is out of bounds

(defn test-turtle []
  (test-all-and-continue '(
			   (pen-up turtle) ; using an unitialized turtle
					; strangely the exception is that a function cannot be converted into a ref
			   )))

(defn our-reverse [coll]
  (reduce add-first '() coll))

(defn our-map [f coll]
  (reduce (fn [res x] (add-last res (f x))) '() coll))
  

(defn add-first-last-examples []
  (test-all-and-continue '((our-reverse [1 2 3])
			   (our-reverse '(1 2 3))
			   (our-map inc [1 2 3])
			   (our-map inc '(1 2 3)))
			 
			 ))

(defn test-seq []
  (test-all-and-continue '((doall (concat [1 2] 5))
			   (seq 2)
			   (seq true)
			   (seq map)
			   )))
(defn test-any-contains []
	(test-all-and-continue '((any? 6 :k)
				 (any? [1 2 3] odd?)
				 (any? odd? {2 3 4 5}) ; ERROR: Argument must be an integer: [2 3] 
				 (any? #(+ % 2) [1 2 5])
				 (some? #(+ % 2) [:k :v]) ; the point is that it's a wrong type of argument
				 (some? [1 2] [3 4])
				 (contains-value? {:a :b :c :d} :a)
				 (contains-value? {:a :b :c :d} :b)
				 (contains-value? [1 2 3 4] +)
				 (contains-value? + 1)
				 (contains-value? 1 [1 2 3]))))

(defn test-wrong-arg-type [] 
	(test-all-and-continue '( (+ 6 :k)
		                  (+ 6 +)
		                  (* 6 "hello")
		                  (odd? "banana")
		                  (inc :k)
		                  (dec "orange")
		                  (< 8 "lemon") ; doesn't work on strings
		                  (< "apple" "orange"))))
				 

(defn third [coll]
  "Returns the third element in a collection,
   or nil if the collection has fewer than three elements"
  (first (rest (rest coll))))

(defn -main [& args] 
  (try
    ;(basic-seesaw-frame)
    ;(test-turtle)
    ;(test-exceptions)
    ;(test-nth)
					;(reduce + +)
					;(test-concat)
					;(test-first-rest)
					;(test-conj-into)
					;(test-add-first-last)
					;(test-forgetting-a-quote)
					;(add-first-last-examples)
    (def the-turtle (turtle 200 300))
    ;(change-background the-turtle)
    ;(pen-down the-turtle)
    ;(go the-turtle 100 100)
					;(show the-turtle)
    ;(third [1 2 3 4])
					;(test-seq)
    ;(def t (add-last 4 [2 3]))
					;t
    ;(test-add-first-last)
    ;(test-any-contains)
    (test-wrong-arg-type)
    (catch Throwable e (println (errors/prettify-exception e)))))
