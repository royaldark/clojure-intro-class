(ns strings.core_test
  (:require [expectations :refer :all]
            [strings.core :refer :all]))

; testing for make-string
(expect "hello world"
  (make-string
   '(\h \e \l \l \o \space \w \o \r \l \d)))

; testing for index-of
(expect 5
        (index-of "emmahenryaaronelena" "e" 4))
(expect 0
  (index-of "emmahenryaaronelena" "e"))
(expect -1
  (index-of "emmahenryaaronelena" "z"))
(expect -1
  (index-of "meep" "e" 4))

; testing for last-index-of
(expect 16
  (last-index-of "emmahenryaaronelena" "e"))
(expect 5
  (last-index-of "emmahenryaaronelena" "e" 7))
(expect 2
  (last-index-of "meep" "e" 10))
(expect -1
  (last-index-of "urgh" "z"))

; testing for append
(expect ""
  (append nil))
(expect ""
  (append))
(expect "abc"
  (append "abc"))
(expect "abcdef"
  (append "abc" "def"))
(expect "abcdefghi"
  (append "abc" "def" "ghi"))

; testing for substring
(expect "happy"
  (substring "unhappy" 2))
(expect "happy"
  (substring "happy" 0 5))
(expect ""
  (substring "" 0))

; testing for glyph-at
(expect "p"
  (glyph-at "happy" 2))
(expect "e"
  (glyph-at "me" 1))

; testing for string-length
(expect 5
  (string-length "aaron"))
(expect 0
  (string-length ""))
(expect 1
  (string-length "q"))

; testing for empty-string?
(expect true
  (empty-string? ""))
(expect false
  (empty-string? "meep"))

; testing for first-string
(expect "p"
  (first-string "principle"))
(expect "l"
  (first-string "lazy"))

; testing for last-string
(expect "d"
  (last-string "decorated"))
(expect "y"
  (last-string "freely"))

; testing for contains-string
(expect true
  (contains-string "Alexandria" "an"))
(expect false
  (contains-string "Alexa|ndria" "an"))
(expect true
  (contains-string "Alexandria" "A"))

; testing for compare-to
(expect -7
  (compare-to "aaron" "henry"))
(expect 4
  (compare-to "emma" "aaron"))
(expect 0
  (compare-to "elena" "elena"))

; testing for reverse-string
(expect "amme"
  (reverse-string "emma"))
(expect "eromhsuR tnuoM"
  (reverse-string "Mount Rushmore"))
