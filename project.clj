(defproject intro "0.1"
            :description "A pilot project to use Clojure for introductory computer science courses at the University of Minnesota - Morris"
            :dependencies [
               [org.clojure/clojure "1.5.1"]
<<<<<<< HEAD
			      [clj-stacktrace "0.2.5"]
			      [org.clojure/core.incubator "0.1.2"]
			   ;[trammel "0.7.0"]
			   ;[org.clojure/core.contracts "0.0.4"]
			      [seesaw "1.4.3"]]
=======
	       [clj-stacktrace "0.2.5"]
	       [org.clojure/core.incubator "0.1.2"]   
	       [seesaw "1.4.3"]
	       [expectations "2.0.6"]]
	    :plugins [[lein-autoexpect "1.0"]]
>>>>>>> 6734ca08e59f9526030180ce6692d90865929ef3
            :main intro.core)
