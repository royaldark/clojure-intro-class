(ns corefns.assert_handling_test
  (:require [expectations :refer :all]
            [corefns.assert_handling :refer :all]))

;; testing for all-elems-are-map-or-vector?
;(expect true (all-elems-are-map-or-vector? [[:a 1] [:b 2]]))
;(expect true (all-elems-are-map-or-vector? [{:a 1} [:b 2]]))
;(expect true (all-elems-are-map-or-vector? [{:a 1} {:b 2}]))
;(expect true (all-elems-are-map-or-vector? [{:a 1} {:b 2} [:c 3] {:d 4} [:e 5]]))
;(expect false (all-elems-are-map-or-vector? [(:a 1) {:b 2}]))
;(expect false (all-elems-are-map-or-vector? [#{:a 1} #{:b 2}]))
;(expect false (all-elems-are-map-or-vector? [[:a 1] #{:b 2}]))
;
;; testing for all-elems-have-length-two?
;(expect true (all-elems-have-length-two? [[1 2] [1 2]]))
;(expect false (all-elems-have-length-two? [[1 2] [1 2 3]]))
;(expect true (all-elems-have-length-two? [#{1 2} '(2 8)]))
;(expect true (all-elems-have-length-two? [{:one 1 :two 2} {:three 3 :four 4}]))
;(expect false (all-elems-have-length-two? [{:one 1 :two 2 :three 3} {:three 3 :four 4}]))
;(expect false (all-elems-have-length-two? [{:one 1 :two 2} {:three 3 :four 4 :five 5}]))
;(expect true (all-elems-have-length-two? [{:one 1 :two 2} {:three 3 :four 4}, {:eight 8 :nine 9} {:ten 10 :eleven 11}]))
;(expect false (all-elems-have-length-two? [[2 5 8] [3 6 9] [1 4 7] [10 0]]))
;(expect true (all-elems-have-length-two? #{{1 2 3 4} {9 10 11 12} {5 6 7 8}}))
;
;; with hashmaps, this function will ALWAYS return true because it breaks up hashmaps into vectors of 2, so it will only
;; return false if the hashmap is not a valid hashmap, since hashmaps always are built up of pairs of 2
;
;(expect true (all-elems-have-length-two? {{:one 1 :two 2} {:three 3 :four 4}}))
;(expect true (all-elems-have-length-two? {{:one 1 :two 2 :three 3} {:three 3 :four 4}}))
;(expect true (all-elems-have-length-two? {{:one 1 :two 2} {:three 3 :four 4 :five 5}}))
;(expect true (all-elems-have-length-two? {{:one 1 :two 2} {:three 3 :four 4}, {:eight 8 :nine 9} {:ten 10 :eleven 11}}))
;(expect true (all-elems-have-length-two? {{:one 1 :two 2 :three 3} {:four 4 :five 5} :six 6 :seven 7 :eight 8 :nine 9}))
;
;
;; testing for all-elems-are-map-or-vector-with-length-2?
;(expect true (all-elems-are-map-or-vector-with-length-2? "function name" [{1 2 3 4} {5 6 7 8}]))
;(expect false (all-elems-are-map-or-vector-with-length-2? "function name" [#{1 2 3 4} {5 6 7 8}]))
;(expect true (all-elems-are-map-or-vector-with-length-2? "function name" [[1 2] [3 4]]))
;(expect false (all-elems-are-map-or-vector-with-length-2? "function name" [[1 2 3 4] [5 6]]))

;; testing for check-if-can-convert-to-hashmap
;(expect true (check-if-can-convert-to-hashmap "function name" [] :something-else-not-important))
;(expect true (check-if-can-convert-to-hashmap "function name" #{} :something-else-not-important))
;(expect true (check-if-can-convert-to-hashmap "function name" '() :something-else-not-important))
;(expect true (check-if-can-convert-to-hashmap "function name" {} {:one 1 :two 2}))
;(expect true (check-if-can-convert-to-hashmap "function name" {} '()))
;(expect true (check-if-can-convert-to-hashmap "function name" {} #{}))
;(expect true (check-if-can-convert-to-hashmap "function name" {} {}))
;(expect true (check-if-can-convert-to-hashmap "function name" {} []))
;(expect false (check-if-can-convert-to-hashmap "function name" {} ['(1 2) '(3 4)]))
;(expect false (check-if-can-convert-to-hashmap "function name" {} [[1 2 4] [3 4]]))
