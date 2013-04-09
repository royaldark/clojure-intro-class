(ns intro.core 
  (:require [errors.core :as errors])
  (:use [corefns.core]
        [seesaw.core]
        [turtle.core]))

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
    (eval quoted-exp)
    (catch Throwable e (println (errors/prettify-exception e)))))

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

;; try re-writing using reduce
;; (reduce f coll) ---> user=> (reduce + [1 2 3 4 5]) == 15
;(defn duplicate-seq [coll] 
;	(loop [s coll result []] ;; result can work with vector or a list
;		(if (empty? s) result
;			(recur (rest s) (add-last (add-last result (first s)) (first s))))))



(defn duplicate-seq [coll] 
	(interleave (reduce add-last '() coll) coll))

(defn flatten-seq [coll]
	(loop [s coll result '()] 
		(if (empty? s) result
		(recur (rest s)(do (if (coll? (first s)) (concat result (first s))
					 (add-last result (first s))))))))

;; Maybe try to work without loop-recur? If possible that is.
(defn interleave-seq [c1 c2]
	(loop [s1 c1 s2 c2 result []] 
		(if (or (empty? s1) (empty? s2) ) result 
			(recur (rest s1) (rest s2) 
				(add-last (add-last result (first s1)) (first s2) ) ) ) ) )


;(defn flatten-seq [coll]
;	(loop [s coll result '()] 
;		(if (empty? s) result
;		(recur (rest s)(do (if (coll? (first s)) (concat result (flatten-seq [first s]))
;					 (add-last result (first s))))))))

(defn rotate-left [rotations coll]
	(loop [r rotations s coll]
		(if (zero? r) s
			(recur (dec r) (drop 1 (add-last s (first s))) ) )))

(defn rotate-right [rotations coll]
	(loop [r rotations s coll]
		(if (zero? r) s
			(recur (inc r) (drop-last (add-first s (last s))) ) )))
	
(defn rotate-seq [rotations coll]
		(if (pos? rotations) (rotate-left rotations coll) 
			             (rotate-right rotations coll)))

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

(defn test-turtle []
  (test-all-and-continue '(
			   (pen-up turtle) ; using an unitialized turtle
					; strangely the exception is that a function cannot be converted into a ref
			   )))

(defn -main [& args]
  (try
    ;(basic-seesaw-frame)
    ;(test-turtle)
    ;(test-exceptions)
    ;(test-nth)
    ;(duplicate-seq [1 2 3])
    ;(flatten-seq '((1 2) 3 [4 [5 6]]))
    ;(interleave-seq [1 2 3] [:a :b :c])
     ;(rotate-seq -6 [1 2 3 4 5])
					;(reduce + +)
    ;(test-concat)
    (catch Throwable e (println (errors/prettify-exception e)))))
