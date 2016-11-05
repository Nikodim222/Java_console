The World's Java Console
Copyleft 2016 Artem V. Efremov (a.k.a. "Nikodim")
http://javaconsole222.sourceforge.net

This has been made in Eclipse 4.4.1 "Luna" (http://www.eclipse.org).
Feel free to extend the code with a newer functionality if required.
I won't charge any duty from you.
Also, be able to get in touch with me at nspu@list.ru (no spam please to not be blacklisted by me).
Have fun!

Redistribution and use in source and binary forms, with or without modification, are permitted.


--- System Requirements ---

Java: JDK 1.7+
Operating system: Any


--- Installation ---

1. Unpack the archive.
2. Go to the directory where the file of "compile.sh" exists.
3. Open it.
4. Find the line as follows: export jdkbin=.......
5. Ensure, so that JDK's executable binaries exist as applied to the path. If your JDK has another path, correct the meaning for the jdkbin variable.
6. Run the file "compile.sh".


--- Getting Started ---

Now that you've built the application from source, you are able to run it.
1. Go to the directory where the file of "Java_console.sh" exists.
2. Open it.
3. Find the line as follows: export jdkbin=.......
4. Ensure, so that JDK's executable binaries exist as applied to the path. If your JDK has another path, correct the meaning for the jdkbin variable.
5. Run the file "Java_console.sh".
For the future, to not repeat all these actions over and over, take heed to create a batch file (such as a *.bat for Windows, a shell script for Linux).


--- Brief Overview ---

This should ease the job for an end-user if (s)he works with an operating system, does some test suites, works with the Net.
The application can be seen as a kind of a command-line interpreter.
Type "exit" to quit, "help" to get the list of commands. "hint command_name" returns brief information on the given command.
