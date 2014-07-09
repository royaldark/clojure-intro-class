Introduction-to-LightTable
==========================

A guide to setting up and using the features of LightTable.

Our cloning url is to the right, or you can just click Clone in Desktop to get a copy of this repository on your personal computer. (If you have git installed, go ahead and clone it in the command line).

#Set Up of Java

Firstly, you should have Java, and the Java Developers Kit, already downloaded on your computer. If that isn't installed already, go to http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html and click the JDK option for your operating system. Follow the given directions for completing this download.

#Set Up of Leiningen

You can download Leiningen from http://leiningen.org/. Downloading Leiningen can be tricky, so be patient. Start by downloading the lein script. If when trying to download the lein script, the script just opens (instead of downloading), first try right-clicking and saving it as lein to a place such as the desktop. If that doesn't work, then try making a new text file and copy-pasting the whole script into the new file on your computer and saving it as lein to a location such as the desktop. Then cd into the place you've saved the file, such as desktop. Then move it using the terminal by typing

```bash
sudo mv lein /usr/bin/lein
```

Then, make it executable by typing

```bash
chmod a+x usr/bin/lein
```

into the command line. After this, then type

```bash
lein
```

into the command line, and watch it self-install a bunch of things. Then, type lein once more, and when a list of leiningen commands appear, you know that leiningen is fully installed!

#Set Up of LightTable

You can download LightTable from http://www.lighttable.com/. Downloading LightTable is pretty easy. Follow the steps on the LightTable download site, move the folder that appears to the location you want on your computer, and you're set to go!

#Set Up of Autoexpect

For expectations, there is a little bit of formatting needed to make sure everything works correctly. You can make a new project by cd-ing into the desired directory and then typing:

``` bash
lein new your-project-name
```

into the command line. We've named our project: test-project. It should say that a new project has been created. Open LightTable and open the Workspace by clicking View -> Workspace. Right click on the workspace area and click Add Folder. Find where you've put your project and click Upload. Open the file project.clj by single-clicking on the name of your project and then single-clicking on project.clj. What's currently in the file should look something like this:

```bash
(defproject test-project "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.3.0"]])
```

Change your project.clj file so it looks like this:

```bash
(defproject test-project "1.0.0-SNAPSHOT"
  :description "A project for showing basic uses of LightTable and Autoexpect"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [expectations "2.0.6"]]
  :plugins [[lein-autoexpect "1.0"]])
```

making sure that you add the expectations dependency and the lein-autoexpect plugin; the description can be whatever you like. Save the file. Next, open up your test file by clicking test/  and clicking through as many nested folders as needed to get to core_test.clj. Once open, your project should look something like this:

```bash
(ns test-project.core-test
  (:require [clojure.test :refer :all]
            [test-project.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
```

Replace the phrase clojure.test to the word expectations. It should look something like this:

```bash
(ns test-project.core-test
  (:require [expectations :refer :all]
            [test-project.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
```

Now, you can erase the call to deftest, replacing it with something along the lines of (expect 5 (+ 2 3)). This is the most basic form of testing using autoexpect. Now, be sure you save the file and then go to the command line, cd into the project directory, and type

```bash
lein autoexpect
```

and then hit enter. Some extra things might print; you can ignore those. At the end, it should say something like:

```bash
Ran 1 tests containing 1 assertions in 14 msecs
0 failures, 0 errors.
```

It might even use pretty colors!

Now you can happily code in LightTable and every time you save, your tests will run automatically and show the results in the terminal. Note, if you want autoexpect to stop running, hit Ctrl+c (or Ctrl+z on Mac).

Happy coding!

For more information on Autoexpect, take a look at this link: http://jayfields.com/expectations/installing.html

#Uninstalling the Programs Listed Above

If you ever would like to uninstall any of the software explained above, then feel free to follow these steps.

Java: If you would like to uninstall Java (although we have no idea why you would feel the need to), you can go to this site:

```bash
java.com/remove
```
and follow the directions to uninstall Java.

Leiningen: First, you must make sure that you delete both the .lein hidden file. This would probably be somewhere like your home, but it could be other places. If needed, use the command

```bash
ls -a
```

(on mac and linux) or

```bash
dir /a
```
(on windows). Then, assuming that lein is in your /usr/bin, you can go ahead and do

```bash
sudo rm /usr/bin/lein
```
to delete the lein folder from your /usr/bin. Check that leiningen is fully uninstalled by typing

```bash
lein
```

into your command line. No leiningen commands should appear.

LightTable: On windows, you can use the uninstall tool, otherwise, your best bet would be to just drag it to trash and then empty your trash.

Autoexpect: Autoexpect isn't a thing we really "installed," so therefore, there is no uninstalling. If you would like to stop using Autoexpect, just stop using it. Technically, Expectations and Autoexpect are just part of Leiningen.

Any comments and/or confusions? We'd love to hear from you! Email saxxx027@morris.umn.edu if you have any concerns!
