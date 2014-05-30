  (ns strings.core_test
  (:require [expectations :refer :all]
            [strings.core :refer :all]))

;Testing for our String Library

;Author: Emma Sax

; testing for make-string
(expect "hello world"
  (make-string '(\h \e \l \l \o \space \w \o \r \l \d)))
(expect ""
  (make-string '()))

; testing for index-of
(expect 5
  (index-of "emmahenryaaronelena" "e" 4))
(expect 0
  (index-of "emmahenryaaronelena" "e"))
(expect -1
  (index-of "emmahenryaaronelena" "z"))
(expect -1
  (index-of "meep" "e" 4))
(expect 3
  (index-of "meep" "p" -3))

; testing for last-index-of
(expect 16
  (last-index-of "emmahenryaaronelena" "e"))
(expect 5
  (last-index-of "emmahenryaaronelena" "e" 7))
(expect 2
  (last-index-of "meep" "e" 10))
(expect -1
  (last-index-of "urgh" "z"))
(expect -1
  (last-index-of "negative" "g" -2))

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

; testing for glyph-at
(expect "p"
  (glyph-at "happy" 2))
(expect "e"
  (glyph-at "me" 1))
(expect ""
  (glyph-at "touch the sky" -1000))
(expect ""
  (glyph-at "touch the sky" 1000))

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

; testing for rest-string
(expect "e"
  (rest-string "me"))
(expect "reat Wall of China"
  (rest-string "Great Wall of China"))

; testing for second-string
(expect "a"
  (second-string "Aaron"))
(expect "e"
  (second-string "testing!"))

; testing for contains-string?
(expect true
  (contains-string? "Moonrise" "on"))
(expect false
  (contains-string? "Moo|nrise" "on"))
(expect true
  (contains-string? "Moonrise" "M"))

; testing for reverse-string
(expect "amme"
  (reverse-string "emma"))
(expect "eromhsuR tnuoM"
  (reverse-string "Mount Rushmore"))

; testing for to-upper-case
(expect "HELLO WORLD"
  (to-upper-case "Hello World"))
(expect "HIIIIYAH!"
  (to-upper-case "hiiiiyah!"))

; testing for to-lower-case
(expect "hello world"
  (to-lower-case "Hello World"))
(expect "statue of liberty"
  (to-lower-case "Statue of Liberty"))

; testing for capitalize
(expect "Hello"
  (capitalize "hEllO"))
(expect "Eiffel tower"
  (capitalize "EiFfeL TowEr"))

; testing for drop-string
(expect "Lake City"
  (drop-string 5 "Salt Lake City"))
(expect ""
  (drop-string 10 "meep"))
(expect "prophet"
  (drop-string -10 "prophet"))

; testing for take-string
(expect "Salt"
  (take-string 4 "Salt Lake City"))
(expect "meep"
  (take-string 10 "meep"))
(expect ""
  (take-string -10 "prophet"))

; testing for join-string
(expect "mesh"
  (join-string "mesh"))
(expect "h.e.l.l.o. .w.o.r.l.d"
  (join-string "." "hello world"))

; testing for replace-string
(expect "The color is blue."
  (replace-string "The color is red." "red" "blue"))
(expect "argh"
  (replace-string "urgh" "u" "a"))

; testing for split-string
(expect ["q" "w" "e" "r" "t" "y" "u" "i" "o" "p"]
  (split-string "q1w2e3r4t5y6u7i8o9p0" #"\d+"))
(expect ["q" "w" "e" "r" "t5y6u7i8o9p0"]
  (split-string "q1w2e3r4t5y6u7i8o9p0" #"\d+" 5))

; testing for split-lines-string
(expect ["test " " string"]
  (split-lines-string "test \n string"))

; testing for replace-first-string
(expect "hello"
  (replace-first-string "hello" "z" "p"))
(expect "Sudan"
  (replace-first-string "Sodan" "o" "u"))

; testing for trim-string
(expect "k"
  (trim-string "     k   "))
(expect ""
  (trim-string "   "))
(expect "d   s  i"
  (trim-string "d   s  i   "))

; testing for string-blank?
(expect true
  (string-blank? nil))
(expect true
  (string-blank? false))
(expect true
  (string-blank? "   "))
(expect false
  (string-blank? "  a "))
