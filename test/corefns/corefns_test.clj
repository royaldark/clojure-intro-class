(ns corefns.corefns_test
  (:require [expectations :refer :all]
            [corefns.corefns :refer :all]
            [strings.strings :refer :all]
            [errors.messageobj :refer :all]
            [errors.exceptions :refer :all]))

;############################################
;### Testing the functionality of corefns ###
;############################################

;; testing for map
(expect '(2 3 4 5 6)
        (map inc [1 2 3 4 5]))
(expect '(5 7 9)
        (map + [1 2 3] [4 5 6]))
(expect '(2 4 6)
        (map + [1 2 3] (iterate inc 1)))
(expect '("Hello Henry!" "Hello Emma!" "Hello Lemmon!")
        (map #(str "Hello " % "!" ) ["Henry" "Emma" "Lemmon"]))
(expect '([:a :d :g] [:b :e :h] [:c :f :i])
        (apply map vector [[:a :b :c] [:d :e :f] [:g :h :i]]))
(expect (more-of x
                 3 (count x)
                 [:a 2] (in x)
                 [:b 4] (in x)
                 [:c 6] (in x))
        (map #(vector (first %) (* 2 (second %))) {:a 1 :b 2 :c 3}))

;; testing for count
(expect 0
        (count nil))
(expect 0
        (count []))
(expect 3
        (count [4 5 6]))
(expect 2
        (count {:one 1 :two 2}))
(expect 5
        (count [1 \a "string" [1 2] {:foo :bar}]))
(expect 6
        (count "string"))
;;;; Elena: this seems to be missing the function definition,
;;;; commenting out for now
;(expect 7
;        (add-two 5))

;; testing for conj
(expect [1 2 3 4]
        (conj [1 2 3] 4))
(expect '(4 1 2 3)
        (conj '(1 2 3) 4))
(expect ["a" "b" "c" "d"]
        (conj ["a" "b" "c"] "d"))
(expect #{1 2 #{3}}
        (conj #{1 2} #{3}))
(expect '(4 3)
        (conj nil 3 4))
(expect {3 4, 1 2}
        (conj {1 2} {3 4}))

;; testing for into
(expect {1 2, 3 4}
        (into {} [[1 2] [3 4]]))
(expect [[1 2] [3 4]]
        (into [] {1 2, 3 4}))
(expect {:a 1, :b 2, :c 3}
        (into (sorted-map) {:b 2 :c 3 :a 1}))

;; testing for reduce
(expect 15
        (reduce + [1 2 3 4 5]))
(expect 6
        (reduce + 1 [2 3]))
(expect [2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67
         71 73 79 83 89 97 101 103 107 109 113 127 131 137 139
         149 151 157 163 167 173 179 181 191 193 197 199 211
         223 227 229 233 239 241 251 257 263 269 271 277 281
         283 293 307 311 313 317 331 337 347 349 353 359 367
         373 379 383 389 397 401 409 419 421 431 433 439 443
         449 457 461 463 467 479 487 491 499 503 509 521 523
         541 547 557 563 569 571 577 587 593 599 601 607 613
         617 619 631 641 643 647 653 659 661 673 677 683 691
         701 709 719 727 733 739 743 751 757 761 769 773 787
         797 809 811 821 823 827 829 839 853 857 859 863 877
         881 883 887 907 911 919 929 937 941 947 953 967 971
         977 983 991 997]
        (reduce
         (fn [primes number]
           (if (some zero? (map (partial mod number) primes))
             primes
             (conj primes number)))
         [2]
         (take 1000 (iterate inc 3))))
(expect [1 2 3 :a :b :c [4 5] 6]
        (reduce into [[1 2 3] [:a :b :c] '([4 5] 6)]))

;; testing for nth
(expect "a"
        (nth ["a" "b" "c" "d"] 0))
(expect "b"
        (nth ["a" "b" "c" "d"] 1))
(expect "nothing found"
        (nth [] 0 "nothing found"))
(expect 1337
        (nth [0 1 2] 77 1337))

;; testing for filter
(expect '(0 2 4 6 8)
        (filter even? (range 10)))
(expect '("a" "b" "n" "f" "q")
        (filter (fn [x]
                  (= (count x) 1))
                ["a" "aa" "b" "n" "f" "lisp" "clojure" "q" ""]))
(expect '("a" "b" "n" "f" "q")
        (filter #(= (count %) 1)
                ["a" "aa" "b" "n" "f" "lisp" "clojure" "q" ""]))
(expect '([:c 101] [:d 102])
        (filter #(> (second %) 100)
                {:a 1 :b 2 :c 101 :d 102 :e -1}))

;; testing for mapcat
(expect '(0 1 2 3 4 5 6 7 8 9)
        (mapcat reverse [[3 2 1 0] [6 5 4] [9 8 7]]))
(expect '("aa" "bb" "cc" "dd" "ee" "ff")
        (mapcat #(clojure.string/split % #"\d")
                ["aa1bb" "cc2dd" "ee3ff"]))

;; testing for concat
(expect '(1 2 3 4)
        (concat [1 2] [3 4]))
(expect [1 2 3 4]
        (into [] (concat [1 2] [3 4])))
(expect '(:a :b 1 [2 3] 4)
        (concat [:a :b] nil [1 [2 3] 4]))
(expect '(1 2 3 4 5 6 7 8 9 10)
        (concat [1] [2] '(3 4) [5 6 7] [8 9 10]))
(expect '(\a \b \c \d \e \f)
        (concat "abc" "def"))

;; testing for <
(expect true (< 1 2))
(expect false (< 2 1))
(expect true (< 1.5 2))
(expect true (< 2 3 4 5 6))

;; testing for <=
(expect true (<= 8 8))
(expect false (<= 9 1))
(expect true (<= 10 100))
(expect true (<= 0 0 2 4 4 9 11))

;; testing for >
(expect false (> 1 2))
(expect true (> 2 1))
(expect false (> 1.5 2))
(expect true (> 9 8 5 3 1))

;; testing for >=
(expect true (>= 9 8))
(expect false (>= 2 5))
(expect true (>= 100 100))
(expect true (>= 10 10 5 4 4 4 4 4 4 2))

;; testing for add-first
(expect '(0 1 2)
        (add-first '(1 2) 0))
(expect [0 1 2]
        (add-first [1 2] 0))
(expect (more-of x
                 0 (first x)
                 3 (count x))
         (add-first #{2 1} 0))
(expect (more-of x
                 3 (count x)
                 [:a 1] (first x))
        (add-first {:b 2 :c 3} [:a 1]))

;; testing for add-last
(expect '(1 2 0)
        (add-last '(1 2) 0))
(expect [1 2 0]
        (add-last [1 2] 0))
(expect (more-of x
                 0 (last x)
                 3 (count x))
         (add-last #{2 1} 0))
(expect (more-of x
                 3 (count x)
                 [:a 1] (last x))
        (add-last {:b 2 :c 3} [:a 1]))

;; testing for contains-value?
(expect true (contains-value? '(7 8 9) 9))
(expect false (contains-value? '(7 8 9) 2))
(expect true (contains-value? [7 8 9] 9))
(expect false (contains-value? [7 8 9] 2))
(expect true (contains-value? #{7 8 9} 9))
(expect false (contains-value? #{7 8 9} 2))
(expect true (contains-value? {:d 4 :e 5} 4))
(expect false (contains-value? {:d 4 :e 5} 8))
(expect false (contains-value? {:d 4 :e 5} :e))

;; testing for contains-key?
(expect false (contains-key? [7 8 9] 9)) ;with vectors, contains-key? works with indices
(expect true (contains-key? [7 8 9] 2))
(expect true (contains-key? #{7 8 9} 9)) ;with sets, contains-key? works just as contains-value?
(expect false (contains-key? #{7 8 9} 2))
(expect true (contains-key? {:d 4 :e 5} :d))
(expect false (contains-key? {:d 4 :e 5} :z))
(expect false (contains-key? {:d 4 :e 5} 5))

;; testing for any?
(expect true (any? odd? [1 2 3 4 5]))
(expect false (any? even? [1 3 5 7 9 11]))
(expect true (any? #(= :a %) [:a :b :c :d :e]))

;#################################################
;### Testing if the corefns preconditions work ###
;#################################################

;; testing for the first precondition of map
(expect "in function map first argument :not-a-function must be a function but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(doall (map :not-a-function [1 2 3])))))

;; testing for the second precondition of map
(expect "in function map second argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(doall (map + :not-a-collection)))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for the precondition of count
(expect "in function count first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(count :not-a-collection))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for the precondition of conj
(expect "in function conj first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(conj :not-a-collection))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for the first precondition of into
(expect "in function into first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(into :not-a-collection [1 2 3]))))

;; testing for the second precondition of into
(expect "in function into second argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(into #{} :not-a-collection))))

;; testing for the third precondition of into
;(expect "in function into second argument [[1 2] [3]] must be either a hashmap, or a collection of vectors or hashmaps of length 2, but is a vector"
;        (get-all-text
;         (run-and-catch-corefns '(into {} [[1 2] [3]]))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for the first precondition of reduce
(expect "in function reduce first argument :not-a-function must be a function but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(reduce :not-a-function [1 2 3]))))

;; testing for the second precondition of reduce
(expect "in function reduce second argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(reduce + :not-a-collection))))

;; testing for the third precondition of reduce
(expect "in function reduce third argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(reduce + 2 :not-a-collection))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for the first precondition of nth, with two args
(expect "in function nth first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(nth :not-a-collection 10))))

;; testing for the second precondition of nth, with two args
(expect "in function nth second argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(nth [0 1 2 3 4] :not-a-number))))

;; testing for the second precondition of nth, with three args
(expect "in function nth first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(nth :not-a-collection 8 "nothing found"))))

;; testing for the second precondition of nth, with three args
(expect "in function nth second argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(nth [0 1 2 3 4] :not-a-number ""))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for the first precondition of filter
(expect "in function filter first argument :not-a-function must be a function but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(filter :not-a-function [1 2 3]))))

;; testing for the second precondition of filter
(expect "in function filter second argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(filter odd? :not-a-collection))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for the first precondition of mapcat
(expect "in function mapcat first argument :not-a-function must be a function but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(mapcat :not-a-function [1 2 3] [8 9 10]))))

;; testing for the second precondition of mapcat
(expect "in function mapcat second argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(mapcat reverse :not-a-collection [8 9 10]))))

;; testing for the second precondition of mapcat with multiple collections
(expect "in function mapcat third argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(mapcat reverse [1 2 3] :not-a-collection))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for the precondition of concat with one arg
(expect "in function concat first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(concat :not-a-collection))))

;; testing for the precondition of concat with multiple args
(expect "in function concat fourth argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(concat [1 2] [3 4] [18 22] :not-a-collection))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for the precondition of < breaks on first arg
(expect "in function < first argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(< :not-a-number 31))))

;; testing for the precondition of < breaks on second arg
(expect "in function < second argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(< 4 :not-a-number))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for the precondition of > breaks on first arg
(expect "in function > first argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(> :not-a-number 31))))

;; testing for the precondition of > breaks on second arg
(expect "in function > second argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(> 4 :not-a-number))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for the precondition of >= breaks on first arg
(expect "in function >= first argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(>= :not-a-number 31))))

;; testing for the precondition of >= breaks on second arg
(expect "in function >= second argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(>= 4 :not-a-number))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for the precondition of <= breaks on first arg
(expect "in function <= first argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(<= :not-a-number 31))))

;; testing for the precondition of <= breaks on second arg
(expect "in function <= second argument :not-a-number must be a number but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(<= 4 :not-a-number))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for the precondition of add-first
(expect "in function add-first first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(add-first :not-a-collection [1 2 3]))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for the precondition of add-last
(expect "in function add-last first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(add-last :not-a-collection [1 2 3]))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for contains-value?
(expect "in function contains-value? first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(contains-value? :not-a-collection 2))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for contains-key?    (println "matched: "matched)
(expect "in function contains-key? first argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(contains-key? :not-a-collection 2))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

;; testing for any?
(expect "in function any? first argument :not-a-predicate must be a function but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(any? :not-a-predicate [1 2 3]))))

;; testing for any?
(expect "in function any? second argument :not-a-collection must be a sequence but is a keyword"
        (get-all-text
         (run-and-catch-corefns '(any? odd? :not-a-collection))))
