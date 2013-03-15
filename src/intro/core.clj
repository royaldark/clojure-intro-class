(ns intro.core ;; Doesn't know the source file for this?
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

;; Make turtle
(defn make-turtle [size]
	(let [turtle (turtle size size)]))


;;Draw spiral crazy time
(defn draw-spiral3 [number]
	(let [turtle (turtle 500 500)]
	(let [coll (take number (cycle [number]))]
	(let [junk (concat (map (fn [x] (* -1 (last x))) (partition 3 coll)))]
	(pen-down turtle)
	(loop [j junk]
		(if (empty? j)
			(show turtle)
			(do (go turtle (first j) (second j)) (recur (drop 2 j)))
		)
	)  
	))))
;; We got an unmatched delimiter compiler error. I'm not sure that beginning students would understand that, or these:
;; clojure.lang.Long cannot be cast to clojure.lang.IDeref
;; Wrong number of args passed to: core$draw-spiral13$fn
;; Mappable? Filter

;; Draw spiral part 2
(defn draw-spiral2 [revolutions starting-point size]
	(let [turtle (turtle 600 600)]
	(pen-up turtle)
	(go turtle (first starting-point) (second starting-point))
	(pen-down turtle)
	(+ size (second starting-point))
	(let [p1 (first (:point @turtle))]
	(let [p2 (second (:point @turtle))]
	(loop [n (* revolutions 4) s size]
		(let [dec-s (- s 10)]
		(if (or (zero? n) (zero? s))
			(show turtle)
			(do (if (zero? (mod n 2)) 
				(if (zero? (mod n 4))
					(do (go turtle p1 (+ s p2)))
					(do dec-s (go turtle  p1 (- s p2)))
				)
				(if (= 3 (mod n 4))
					(do (go turtle (+ s p1) p2))
					(do dec-s (go turtle p1 p2))
				)
			    )
	(recur (dec n) s)) )) ) ))))

;;Draws a spiral with the number of revolutions given
(defn draw-spiral [revolutions]
	(let [turtle (turtle 600 600)]
	(pen-up turtle)
	(go turtle -80 -80)
	(pen-down turtle)
	(loop [n (* revolutions 4)]
		(if (zero? n)
			(show turtle)
			(do (if (zero? (mod n 2)) 
				(if (zero? (mod n 4))
					(do (go turtle (+ 20 (* -1 (first (:point @turtle)))) (second (:point @turtle))))
					(do (go turtle (* -1 (first (:point @turtle))) (second (:point @turtle))))
				)
				(if (= 3 (mod n 4))
					(do (go turtle (first (:point @turtle)) (+ 20 (* -1 (second (:point @turtle))))))
					(do (go turtle (first (:point @turtle)) (* -1 (second (:point @turtle)))))
				)
			    )
			
		(recur (dec n))) )) ))
		

;; draws a polygon, given a collection of points (x y)
(defn draw-polygon [coll]
	(let [turtle (turtle 400 400)]
	(pen-up turtle)
	(conj coll (first coll))
	(conj coll (second coll))
	(go turtle (first coll) (second coll))
	(pen-down turtle)
	(loop [s coll] 
	(if (= (count s) 2) (go turtle (first s) (second s))
		(do (go turtle (first s) (second s))
		(recur  (drop 2 s) ))))
	(go turtle (first coll) (second coll))
	(show turtle)))

(defn fib [turtle depth]
  (forward turtle 30)
  (if (> depth 2)
    (do 
      (left turtle 15)
      (fib turtle (- depth 1))
      (right turtle 30)
      (fib turtle (- depth 2))
      (left turtle 15)))
  (back turtle 30))


(defn turtle-demo []
  (let [turtle (turtle 400 400)]
  (pen-up turtle)
  ;(go turtle 0 -100)
    ;Oops! Forgot an argument.
    ;Good thing we have prettify-exception
  ;(go turtle 0) Does not like the turtle to do this, not sure why
  (pen-down turtle)
  (fib turtle 10)
  (show turtle)))

(defn test-and-continue [quoted-exp]
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
			(nth #{1 2 3} 1))))



(defn test-turtle []
  (test-all-and-continue '((in-ns 'turtle.core) ; needed because otherwise eval operates in its own namespace and can't access turtle stuff
			   (pen-up turtle) ; using an unitialized turtle
					; strangely the exception is that a function cannot be converted into a ref
			   )))

(defn test-exceptions []
  (test-all-and-continue '((throw (new IndexOutOfBoundsException))
			   (throw (new IndexOutOfBoundsException "10"))
			   (throw (new NullPointerException))
			   (throw (new NullPointerException "some message")))))

(defn -main [& args]
  (try
    ;(basic-seesaw-frame)
    ;(turtle-demo)
    ;(map 2 [1 2 3]) ; Doesn't realize this is a function from the error message.
    ; (filter is-odd? [1 2 3 4]) ; throws a gigantic exception, lots of compiler errors or lines that aren't there.
    ;(filter 1 [1 2 3 4]) ; does something somewhat reasonable, also doesn't realize this is a function from the error message
    ;(filter #(+ % 2) [1 2 3 4]) ; apparently it's not an error, but perhaps should be for new students? How do we deal with that?
					;(< 'a 8) ;now gives a reasonable message
    ;(defn myfunc [x] (+ x 2))
					;(< myfunc +)
    ;(test-arithmetic-expressions)
    ;(test-sequences)
    ;(draw-polygon [100 100  100 -100  -100 -100  -100 100])
    
    ;(draw-spiral 16)
    ;(draw-spiral2 6 [100 100] 10)
    ;(draw-spiral3 50)
    ;(test-turtle)
					;(test-exceptions)
    (test-nth)
    (catch Throwable e (println (errors/prettify-exception e)))))
