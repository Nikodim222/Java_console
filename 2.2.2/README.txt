A Changelog




----------------------------------------------------------------------------
Java_console, ver. 2.2.2 (2019-08-13)

1) Using a codepage in the input has been fixed.




----------------------------------------------------------------------------
Java_console, ver. 2.2.1 (2019-07-25)

1) The error with Windows paths has been fixed. As it turned out, the app couldn't read them formerly.
2) The OJDBC6 driver has been upgraded to be able to work with the newest Oracle database.
P.S.:
So far, there has been an insoluble issue related to Cyrillic names in a Windows path when using the "new File(path).listFiles()" instruction (whilst Linux is perfectly okay). And I don't figure out how to fix it yet. :-( Follow https://ru.stackoverflow.com/questions/500668/Ошибка-открытия-файла-из-за-русских-букв-в-пути




----------------------------------------------------------------------------
Java_console, ver. 2.1.2

1) A new class of "SocketOverProxyFactory" is added.




----------------------------------------------------------------------------
Java_console, ver. 2.1.1

1) A new command of rcfdf is added.
2) Some errors are fixed.
3) Code refactoring took place.




----------------------------------------------------------------------------
Java_console, ver. 2.1.0

1) The NEWNYM command has been added for controlling Tor (http://gitweb.torproject.org/torspec.git/tree/control-spec.txt).
2) Some errors are fixed.




----------------------------------------------------------------------------
Java_console, ver. 2.0.9

1) Some errors are fixed.




----------------------------------------------------------------------------
Java_console, ver. 2.0.8

1) An HTTP server is added.




----------------------------------------------------------------------------
Java_console, ver. 2.0.7

1) Multiple grepping ("mgrep") is added.
2) MList is added.




----------------------------------------------------------------------------
Java_console, ver. 2.0.6

1) The support of the DroneBL DNSBL service has been added.




----------------------------------------------------------------------------
Java_console, ver. 2.0.5

1) A bug fixed related to overriding a class.




----------------------------------------------------------------------------
Java_console, ver. 2.0.4

1) "whois" has been added (http://whois-client.sourceforge.net).




----------------------------------------------------------------------------
Java_console, ver. 2.0.3

1) "grep" has been added.
2) "mem" has been added. 
3) "md", "mkdir" have been added.
4) "rd", "rmdir" have been added.
5) "ftp" have been added which implements a Java full-features FTP client. It is based on the ftp4j library (http://www.sauronsoftware.it/projects/ftp4j/).




----------------------------------------------------------------------------
Java_console, ver. 2.0.2

1) Now it also supports Base64 in mim, mim2file.




----------------------------------------------------------------------------
Java_console, ver. 2.0.1

1) "mim" has been added.
2) "mim2file" has been added.




----------------------------------------------------------------------------
Java_console, ver. 1.9.4

1) "printenv" has been added.
2) "exec" has been added.




----------------------------------------------------------------------------
Java_console, ver. 1.8.9

1) The "ff" / "find" command is available now to recursively find files and directories by a pathname.




----------------------------------------------------------------------------
Java_console, ver. 1.8.6

1) Code optimization.
2) A datasheet has been added (see the file of ./create_datasheet.sh, the directory of ./datasheet).




----------------------------------------------------------------------------
Java_console, ver. 1.8.4

1) A "netcalc" command has been added which returns a range for an IPv4 subnet by its CIDR notation. See "hint netcalc" for usage.
2) Some improvements inside the source code.




----------------------------------------------------------------------------
Java_console, ver. 1.8.3

1) The unsupported-in-Java-1.7 Byte.toUnsignedLong() function has been removed from the private class of NoProxyClass, because it can only work since Java 1.8.




----------------------------------------------------------------------------
Java_console, ver. 1.8.2

1) The noproxy environment variable is now available. Type "hint noproxy" to see how it is used.




----------------------------------------------------------------------------
Java_console, ver. 1.8.1

1) The "fc", "diff", and "size" commands have been added.





----------------------------------------------------------------------------
Java_console, ver. 1.7.8

1) The "charsets" command has been added.
2) The "iconv" command has been added.
3) A few bugs have been fixed.




----------------------------------------------------------------------------
Java_console, ver. 1.7.4

1) Now text files are able to be read in different codepages. Use the "codepage" command beforehand to define what codepage is going to be used to read the text file.
