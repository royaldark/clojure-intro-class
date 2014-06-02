  (ns strings.core_test
  (:require [expectations :refer :all]
            [strings.core :refer :all]))

;Testing for our String Library

;Author: Emma Sax

; testing for make-string
(expect "hello world" (make-string '(\h \e \l \l \o \space \w \o \r \l \d)))
(expect ""(make-string '()))

; testing for index-of
(expect 5 (index-of "emmahenryaaronelena" "e" 4))
(expect 0 (index-of "emmahenryaaronelena" "e"))
(expect -1 (index-of "emmahenryaaronelena" "z"))
(expect -1 (index-of "meep" "e" 4))
(expect 3 (index-of "meep" "p" -3))

; testing for last-index-of
(expect 16 (last-index-of "emmahenryaaronelena" "e"))
(expect 5 (last-index-of "emmahenryaaronelena" "e" 7))
(expect 2 (last-index-of "meep" "e" 10))
(expect -1 (last-index-of "urgh" "z"))
(expect -1 (last-index-of "negative" "g" -2))

; testing for append
(expect "" (append nil))
(expect "" (append))
(expect "abc" (append "abc"))
(expect "abcdef" (append "abc" "def"))
(expect "abcdefghi" (append "abc" "def" "ghi"))
(expect "" (append ""))
(expect "abcdef"(append "abc" "def" nil))

; testing for glyph-at
(expect "p" (glyph-at "happy" 2))
(expect "e" (glyph-at "me" 1))
(expect "" (glyph-at "touch the sky" -1000))
(expect "" (glyph-at "touch the sky" 1000))
(expect "" (glyph-at "" 6))

; testing for empty-string?
(expect true (empty-string? ""))
(expect false (empty-string? "meep"))

; testing for first-of-string
(expect "p" (first-of-string "principle"))
(expect "l" (first-of-string "lazy"))

; testing for last-of-string
(expect "d" (last-of-string "decorated"))
(expect "y" (last-of-string "freely"))

; testing for rest-of-string
(expect "e" (rest-of-string "me"))
(expect "reat Wall of China" (rest-of-string "Great Wall of China"))

; testing for second-of-string
(expect "a" (second-of-string "Aaron"))
(expect "e" (second-of-string "testing!"))

; testing for contains-string?
(expect true (contains-string? "Moonrise" "on"))
(expect false (contains-string? "Moo|nrise" "on"))
(expect true (contains-string? "Moonrise" "M"))

; testing for reverse-string
(expect "amme" (reverse-string "emma"))
(expect "eromhsuR tnuoM" (reverse-string "Mount Rushmore"))

; testing for to-upper-case
(expect "HELLO WORLD" (to-upper-case "Hello World"))
(expect "HIIIIYAH!" (to-upper-case "hiiIiyAh!"))

; testing for to-lower-case
(expect "hello world" (to-lower-case "Hello World"))
(expect "statue of liberty" (to-lower-case "StatUe oF LiBeRty"))

; testing for capitalize
(expect "Hello" (capitalize "hEllO"))
(expect "Eiffel tower" (capitalize "EiFfeL TowEr"))

; testing for drop-from-string
(expect "Lake City" (drop-from-string 5 "Salt Lake City"))
(expect "" (drop-from-string 10 "meep"))
(expect "prophet" (drop-from-string -10 "prophet"))

; testing for take-from-string
(expect "Salt" (take-from-string 4 "Salt Lake City"))
(expect "meep" (take-from-string 10 "meep"))
(expect "" (take-from-string -10 "prophet"))

; testing for join-string
(expect "mesh" (join-string "mesh"))
(expect "h.e.l.l.o. .w.o.r.l.d" (join-string "." "hello world"))

; testing for replace-string
(expect "The color is blue." (replace-string "The color is red." "red" "blue"))
(expect "argh" (replace-string "urgh" "u" "a"))

; testing for split-string
(expect ["q" "w" "e" "r" "t" "y" "u" "i" "o" "p"] (split-string "q1w2e3r4t5y6u7i8o9p0" #"\d+"))
(expect ["q" "w" "e" "r" "t5y6u7i8o9p0"] (split-string "q1w2e3r4t5y6u7i8o9p0" #"\d+" 5))

; testing for split-lines-string
(expect ["test " " string"] (split-lines-string "test \n string"))

; testing for replace-first-of-string
(expect "hello" (replace-first-of-string "hello" "z" "p"))
(expect "heylo" (replace-first-of-string "hello" "l" "y"))

; testing for trim-string
(expect "k" (trim-string "     k   "))
(expect "" (trim-string "   "))
(expect "d   s  i" (trim-string "d   s  i   "))

; testing for triml-string
(expect "k   " (triml-string "     k   "))
(expect "" (triml-string "   "))
(expect "d   s  i   " (triml-string "d   s  i   "))

; testing for trimr-string
(expect "     k" (trimr-string "     k   "))
(expect "" (trimr-string "   "))
(expect "d   s  i" (trimr-string "d   s  i   "))

; testing for trim-newline-string
(expect "testing " (trim-newline-string "testing \n"))
(expect "testing again..." (trim-newline-string "testing again...\r"))

; testing for string-blank?
(expect true (string-blank? nil))
(expect true (string-blank? false))
(expect true (string-blank? "   "))
(expect false (string-blank? "  a "))
