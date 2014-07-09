(ns errors.error_hints)

(def hints
     {:class-cast-exception "Attempted to use a <type1>, but a <type2> was expected.\n
The error happens when a function's argument is not of the type for which the function is defined.\n
For instance, (+ 1 \"2\") results in this error because \"2\" is not a number, but a string.\n
Likewise, (< 1 :two) would cause this error because :two is a keyword, and not a number. \n"
					;This error may happen if you were using a variable and had a wrong value in the variable
      ; or if you have switched the order of arguments in a function. 
      
    ;                      Error example for (+ 1 :two):\n
    ;                      \"The + function on line 7 was expecting a number but was given a keyword.\"\n
    ;                      Error example for(< 8 \"12\"):\n
    ;                      \"The < function on line 52 was expecting a number but was given a string.\"\n
    ;                      Error example for (num \"ten\"):\n
    ;                      \"The num function on line 42 was expecting a number but was given a string.\""
   :illegal-argument-cannot-convert-type "The ___ function on line ___ was expecting ___ but was given ___.\n
                                          Error example for (cons 1 2):\n
                                          \"The cons function on line 4 was expecting a sequence but was given a number.\"\n
                                          Error example for (into {} [1 2 3]):\n
                                          \"The into function on line 7 was expecting a vector of vectors but was given a vector.\""
   :index-out-of-bounds-index-not-provided "Trying to look at a certain location that doesn’t exist in a specific collection.\n
                                            Error example for (nth [1 2 3] 5):\n
                                            \"Trying to look at a certain location that doesn’t exist in a specific collection.\""
   :arity-exception-wrong-number-of-arguments "Make sure you have the correct number of arguments.\n
                                               Error example for (first [2 1] [3 6]):\n
                                               \"Make sure you have the correct number of arguments.\"\n
                                               Error example for (conj 2):\n
                                               \"Make sure you have the correct number of arguments.\""
   :compiler-exception-cannot-resolve-symbol "____ is undefined; the compiler doesn’t know what it means. See below for possible mistakes.\n
                                              Cases:\n
	                                            \t -you wanted it to be a string:\n
                                              \t \t If you want ___ to be a string (words), put quotes around it:\n
                                              \t \t \"hello\" instead of hello\n
	                                            \t -you wanted it to be a variable\n
                                              \t \t If you want ___ to be a variable (name for a value), define the value:\n
                                              \t \t (def hello 8) instead of hello\n
	                                            \t -you wanted it to be a keyword\n
                                              \t \t If you want ___ to be a keyword, put a colon in front of it:\n
                                              \t \t :hello instead of hello\n
	                                            \t -you mistyped a higher-order function\n
                                              \t \t Look back at your code for spelling errors:(first [2 3]) instead of (firST [2 3])"

   })

