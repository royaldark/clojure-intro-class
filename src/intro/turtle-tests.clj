(ns intro.turtle-test
  :use [turtle.core])

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

(defn -main [& args]
  (try
    ;(basic-seesaw-frame)
    ;(test-turtle)
    (catch Throwable e (println (errors/prettify-exception e)))))

