/**
 * The World's Java Console
 * by Artem V. Efremov (a.k.a. "Nikodim") 
 * 
 * This has been made in Eclipse 4.4.1 "Luna" (http://www.eclipse.org).
 * Feel free to extend the code with a newer functionality if required.
 * I won't charge any duty from you.
 * Also, be able to get in touch with me at nspu@list.ru (no spam please to not be blacklisted by me).
 * Have fun!
 */

package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.nio.charset.Charset;
import java.net.Proxy;
import java.net.InetSocketAddress;

import files.BasicOperations; // linking the handy BasicOperations class
import files.External; // linking the handy External class
import coder.Coding; // linking the handy Coding class
import Databases.Oracle_DB; // Oracle DBMS
import Databases.MySQL_DB; // MySQL DBMS
import Databases.Tables; // printing tables to the stdout
import BundleOfNetworkStuff.SocketXT;
import BundleOfNetworkStuff.WhoIs;
import BundleOfNetworkStuff.DroneBL;

public class startIt {
	private static final String sysHome = new String("user.home");
	private static final String fileSeparator = new String("file.separator");

	static {
		System.out.println("--- The World's Java Console ---");
		System.out.println("Type 'exit' to quit, 'help' to see the list of commands.");
	}

	public interface Function<From1, To1> {
		To1 apply(From1 from);
	}

	/**
	 * This Java class contains network settings.
	 * @author Nikodim
	 */
	private static class NetworkSettings {
		private String codepage = new String();
		private String proxyhost = new String();
		private int proxyport = 0;
		private String uName = new String();
		private String uPass = new String();
		private String[] noproxy = new String[] {};
	}

	/**
	 * This Java class contains Oracle settings.
	 * @author Nikodim
	 */
	private static class OracleSettings {
		private String oracle_user = new String();
		private String oracle_password = new String();
		private String oracle_url = new String(); // "jdbc:oracle:thin:@10.242.4.2:1525:REG1"
		private String oracle_ctrl_line = new String(); // "java.sql.Driver"
		private int oracle_col_size = 0;
		private String oracle_query = new String();
	}

	/**
	 * This Java class contains MySQL settings.
	 * @author Nikodim
	 */
	private static class MySQLSettings {
		private String mysql_user = new String();
		private String mysql_password = new String();
		private String mysql_url = new String(); // "jdbc:mysql://localhost:3306/mysql"
		private String mysql_ctrl_line = new String(); // "java.sql.Driver"
		private int mysql_col_size = 0;
		private String mysql_codepage = new String();
		private String mysql_query = new String();
	}

	private static void greppedFile(String fileMGrep, String grepPatternMGrep, NetworkSettings objNetworkSettings, String defaultCodepage) {
		try {
			System.out.println("Looking up the pattern in the file. This may take a while...");
			int x = 0;
			for (String sTemp08 : new BasicOperations(fileMGrep, fileMGrep).readTextFile(((objNetworkSettings.codepage.isEmpty()) ? defaultCodepage : objNetworkSettings.codepage.toString()))) {
				if (sTemp08.toLowerCase().lastIndexOf(grepPatternMGrep.toLowerCase()) > -1) {
					System.out.println("\"" + sTemp08 + "\"");
					x++;
				}
			}
			if (x < 1) {
				System.out.println("None has been found according to the pattern.");
			} else {
				System.out.println("There have been totally " + String.valueOf(x) + " lines found, according to the pattern.");
			}
		}
		catch (Exception e) {
			System.err.println("The file cannot be read out.");
		}
	}

	private static void _changed(String par) {
		StringBuilder obj = new StringBuilder();
		obj.append(par);
		obj.append(" has been changed.");
		System.out.println(obj.toString());
	}

	private static String pathConverted(String path, String defaultDir) {
		return new BasicOperations("", "").pathConverted(path, defaultDir, sysHome, fileSeparator);
	}

	private static class myPrompt {

		private String promptLine;

		public myPrompt(String s) {
			this.promptLine = s;
		}

		private String thisCommand() throws IOException {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.print(this.promptLine + "> ");
			return in.readLine();
		}

	}

	private static final String errorMsg01 = new String("Invalid Input");

	private static String result(boolean foo) {
		final String good = new String("Success.");
		final String bad = new String("Failure.");
		if (foo) {
			return good;
		}
		return bad;
	}

	public static void main(String[] args) {
		NetworkSettings objNetworkSettings = new NetworkSettings();
		OracleSettings objOracleSettings = new OracleSettings();
		MySQLSettings objMySQLSettings = new MySQLSettings();
		final int defaultProxyPort = 3128; // the port by default
		final String defaultCodepage = "UTF-8";
		final int HigherColLim = 79;
		int tempVar01;
		final int ARGLIM = 10; // peak number of arguments
		final String defaultDir = new String(System.getProperty(sysHome));
		final String cantChngDir = new String("The directory cannot be changed.");
		final String[] cmdList = new String[] {
				"help", "Returns a list of commands which are available for usage.\nUsage: help",
				"history", "The program keeps track of what you've been typing via a command-line interface.\nWhen you invoke this command, it prints a list of commands which you've run before.\nUsage: history",
				"clear", "Clears the history of commands. Also, see \"history\".\nUsage: clear",
				"ver", "This returns the version of a Java virtual machine (JVM), you are using.\nUsage: ver",
				"uname", "Synonymic to \"ver\".",
				"resetdir", "The command tries to redirect the user to the home directory if it is possible to do on the current Java virtual machine (JVM).\nUsage: resetdir",
				"more", "Prints a text file onto the screen.\nUsage: more some_text_file.ext",
				"read", "Synonymic to \"more\".",
				"type", "Synonymic to \"more\".",
				"say", "This command displays a line of text.\n Usage: say Hello World!",
				"echo", "Synonymic to \"say\".",
				"copy", "This copies one file to another.\nUsage: copy some_file_1.ext some_file_2.ext",
				"cp", "Synonymic to \"copy\".",
				"fc", "The diff utility is a data comparison tool that returns whether there are differences between two files or not.\nUsage: fc file1.ext file2.ext",
				"diff", "Synonymic to \"fc\".",
				"size", "Returns the file's size (in bytes).\nUsage: size file.ext",
				"mv", "The command moves or renames a file.\nUsage: mv some_file_1.ext some_file_2.ext",
				"rename", "Synonymic to \"mv\".",
				"ren", "Synonymic to \"mv\".",
				"erase", "This erases a file.\nUsage: erase some_file.ext",
				"rm", "Synonymic to \"erase\".",
				"del", "Synonymic to \"erase\".",
				"delete", "Synonymic to \"erase\".",
				"getname", "This command returns the absolute path of a file or a directory.\nUsage: getname ../myFolder/poem.txt",
				"dir", "Prints directory contents.\nUsage: dir some_directory",
				"mlist", "Prints directory contents by a pattern (mask).\nUsage: mlist path pattern\nFor example:\nmlist /home/user/docs .txt\nThis will be looking for all the files and directories, having the '.txt' thing in their names.",
				"ls", "Synonymic to \"dir\".",
				"cd", "The command causes the named directory to become the current working one.\nUsage: cd new_directory",
				"chdir", "Synonymic to \"cd\".",
				"url", "It's a good tool for non-interactive printing of a text file from The World Wide Web (WWW, W3) onto the screen.\nIt can work either over a proxy connection or direct access. It supports HTTP only.\nUsage: url http://someserver.com/some_directory/file.ext\nAlso, see \"network\".",
				"wget", "Synonymic to \"url\".",
				"dump", "Saves the history to a text file.\nUsage: dump some_file.txt\nAlso, see \"history\".",
				"date", "Prints the system date and time.\nUsage: date",
				"time", "Synonymic to \"date\".",
				"codepage", "This specifies the codepage for using when getting some text file on the Internet (if the connection is established) or thru a local path.\nUsage: codepage Windows-1251\nAlso, see \"wget\", \"more\".",
				"proxyhost", "Assigns a proxy host to be used during connection establishment.\nUsage: proxyhost proxy.server.com",
				"proxyport", "Assigns a proxy port to be used during connection establishment.\nUsage: proxyport 3128",
				"proxyuser", "Assigns a proxy username to be used during connection establishment if proxy authentication is required.\nUsage: proxyuser bob",
				"proxypassword", "Assigns a proxy password to be used during connection establishment if proxy authentication is required.\nUsage: proxypassword secret_word",
				"noproxy", "The noproxy environment variable should contain a comma-separated list of domain extensions proxy should not be used for.\nUsage: noproxy 127.0.0.0/8,localhost,172.16.0.0/16,10.242.0.0/16,.local,.server.com,www.myhomepage.org",
				"no-proxy", "Synonymic to \"noproxy\".",
				"no_proxy", "Synonymic to \"noproxy\".",
				"netcalc", "This command returns a range for an IPv4 subnet by its CIDR notation.\nUsage: netcalc 192.168.100.0/22",
				"cidr", "Synonymic to \"netcalc\".",
				"ff", "This command recursively finds files and directories by a pathname.\nUsage: ff pathname",
				"find", "Synonymic to \"ff\".",
				"network", "This command prints connection properties.\nUsage: network",
				"connection", "Synonymic to \"network\".",
				"webpage2file", "This saves a text file from the Net to the local file.\nUsage: webpage2file http://server.com/index.html myfile.htm",
				"binaryurl2file", "This saves a binary file from the Web to the local place on the system.\nUsage: binaryurl2file http://server.com/misc/game.zip game34.zip",
				"oracle_user", "Specifies which username is used for connection establishment to an Oracle DBMS.\nUsage: oracle_user ora_user",
				"oracle_password", "Specifies what password is used for connection establishment to an Oracle DBMS.\nUsage: oracle_password secret_word",
				"oracle_url", "This defines what URL must be used for connection establishment to an Oracle DBMS.\nUsage: oracle_url jdbc:oracle:thin:@10.242.4.2:1525:REG1",
				"oracle_ctrl_line", "This specifies the control line for the Oracle DBMS driver to be invoked while attempting to establish a connection.\nUsage: oracle_ctrl_line java.sql.Driver",
				"oracle_col_size", "Sets the columns' size in tables while retrieving data from an Oracle database.",
				"use_old_oracle", "Crutches for fixing the \"ORA-01882: timezone region not found\" bug for a newer ojdbc driver.\nSee also http://stackoverflow.com/questions/9156379/ora-01882-timezone-region-not-found\nUsage: use_old_oracle true\nuse_old_oracle false",
				"oracle_query", "The command sends an SQL query to the Oracle DBMS, which you've pointed, and waits for some reponse from it, then.\nUsage: oracle_query SELECT * FROM v$version;",
				"oracle_file2query", "This command loads a file with an Oracle SQL query and fetches a result from it.\nUsage: oracle_file2query file_name.sql",
				"oracle_settings", "Prints the Oracle connection settings on the screen.\nUsage: oracle_settings",
				"mysql_user", "Specifies which username is used for connection establishment to a MySQL DBMS.\nUsage: mysql_user mysql_user",
				"mysql_password", "Specifies what password is used for connection establishment to a MySQL DBMS.\nUsage: mysql_password secret_word",
				"mysql_url", "This defines what URL must be used for connection establishment to a MySQL DBMS.\nUsage: mysql_url jdbc:mysql://localhost:3306/mysql",
				"mysql_ctrl_line", "This specifies the control line for the MySQL DBMS driver to be invoked while attempting to establish a connection.\nUsage: mysql_ctrl_line java.sql.Driver",
				"mysql_col_size", "Sets the columns' size in tables while retrieving data from a MySQL database.",
				"mysql_codepage", "Defines what codepage is going to be used when working with a MySQL database.\nUsage: mysql_codepage cp1251",
				"mysql_query", "The command sends an SQL query to the MySQL DBMS, which you've pointed, and waits for some reponse from it, then.\nUsage: mysql_query SELECT VERSION();",
				"mysql_file2query", "This command loads a file with a MySQL SQL query and fetches a result from it.\nUsage: mysql_file2query file_name.sql",
				"mysql_settings", "Prints the MySQL connection settings on the screen.\nUsage: mysql_settings",
				"iconv", "Converts encoding of the given file from one encoding to another.\nUsage: iconv codepage1 _file1 codepage2 _file2",
				"charsets", "This prints a list of all the available codepages.\nUsage: charsets",
				"hint", "This hints at how the given command should be used.\nUsage: hint command_to_be_hinted",
				"exit", "This command causes normal process termination.\nUsage: exit",
				"quit", "Synonymic to \"exit\".",
				"printenv", "This prints all the environment variables.\nUsage: printenv",
				"exec", "This command executes an external command or program under the operating system.\nUsage: exec external_command_or_file",
				"mim", "This parses a file in the MIM (Multipurpose Internet Mail Extensions) format.\nUsage: mim file_name from_codepage to_codepage",
				"mim2file", "This parses a file in the MIM (Multipurpose Internet Mail Extensions) format and writes the output to another file.\nUsage: mim2file from_file from_codepage to_codepage to_file",
				"grep", "\"grep\" is a command-line utility for searching plain-text data sets for lines matching a regular expression.\nUsage: grep file.ext pattern",
				"mem", "Reports how much memory is occupied by this program, and free memory.\nUsage: mem",
				"mkdir", "This creates a new directory.\nUsage: mkdir dir_name",
				"md", "Synonymic to \"mkdir\".",
				"rmdir", "This deletes a new directory.\nUsage: rmdir dir_name",
				"rd", "Synonymic to \"rmdir\".",
				"ftp", "This command invokes an FTP client.\nUsage: ftp",
				"whois", "This command returns a WHOIS information (RFC 954) for the given IP address.\nUsage: whois 8.8.8.8",
				"dnsbl", "This command returns the status of an IP address whether or not it is blacklisted on the network.\nUsage: dnsbl 8.8.8.8\nDomain Name System Blacklists, also known as DNSBL's or DNS Blacklists, are spam blocking lists that allow a website administrator to block messages from specific systems that have a history of sending spam.",
				"mgrep", "Does basically the same as 'grep', but with many files at a time.\nUsage: mgrep path file_pattern text_pattern\nSee also 'grep'."
		};
		final String unavailable = new String("N/A");
		final String undef = new String("The parameter is undefined.");
		ArrayList<String> procArgs = new ArrayList<String>();

		int x;
		String[] argsArray = new String[ARGLIM];
		myPrompt mp_obj = new myPrompt("Waiting for your input");
		ArrayList<String> lst_obj = new ArrayList<String>();
		String s = new String();
		String s_converted = new String();

		for (;;) { // infinite loop
			try {
				s = mp_obj.thisCommand();
			}
			catch (Exception e) {
				System.err.println();
				System.err.println(errorMsg01);
			}
			if (s == null) {
				System.err.println();
				System.err.println(errorMsg01);
				break;
			}
			switch (s) {
			case "":
				System.out.println("You've written none.");
				break;
			default:
				System.out.println("You've written '" + s + "'.");
				break;
			}
			s_converted = s.trim();
			lst_obj.add(s);
			if ((s_converted.equals("exit")) || (s_converted.equals("quit"))) {
				break;
			}

			/**
			 * Commands with no arguments (consisting of a single word in a command line)
			 */
			switch (s_converted.toLowerCase()) {
			case "history":
				for (x = 0; x < lst_obj.size(); x++) {
					System.out.println(lst_obj.get(x));
				}
				break;
			case "mem":
				long freemem = Runtime.getRuntime().freeMemory();
				System.out.println("Occupied: " + String.valueOf(Runtime.getRuntime().totalMemory() - freemem) + " bytes.");
				System.out.println("Free: " + String.valueOf(freemem) + " bytes.");
				break;
			case "ftp":
				new BasicOperations("", "").ftp(defaultDir, sysHome, fileSeparator);
				break;
			case "oracle_settings":
				System.out.println("oracle_user: " + ((objOracleSettings.oracle_user.isEmpty()) ? unavailable : objOracleSettings.oracle_user.toString()));
				System.out.println("oracle_password: " + ((objOracleSettings.oracle_password.isEmpty()) ? unavailable : objOracleSettings.oracle_password.toString()));
				System.out.println("oracle_url: " + ((objOracleSettings.oracle_url.isEmpty()) ? unavailable : objOracleSettings.oracle_url.toString()));
				System.out.println("oracle_ctrl_line: " + ((objOracleSettings.oracle_ctrl_line.isEmpty()) ? unavailable : objOracleSettings.oracle_ctrl_line.toString()));
				System.out.println("oracle_col_size: " + ((objOracleSettings.oracle_col_size < 1) ? unavailable : String.valueOf(objOracleSettings.oracle_col_size)));
				System.out.println("Last used SQL query: " + ((objOracleSettings.oracle_query.isEmpty()) ? unavailable : objOracleSettings.oracle_query.toString()));
				break;
			case "mysql_settings":
				System.out.println("mysql_user: " + ((objMySQLSettings.mysql_user.isEmpty()) ? unavailable : objMySQLSettings.mysql_user.toString()));
				System.out.println("mysql_password: " + ((objMySQLSettings.mysql_password.isEmpty()) ? unavailable : objMySQLSettings.mysql_password.toString()));
				System.out.println("mysql_url: " + ((objMySQLSettings.mysql_url.isEmpty()) ? unavailable : objMySQLSettings.mysql_url.toString()));
				System.out.println("mysql_ctrl_line: " + ((objMySQLSettings.mysql_ctrl_line.isEmpty()) ? unavailable : objMySQLSettings.mysql_ctrl_line.toString()));
				System.out.println("mysql_col_size: " + ((objMySQLSettings.mysql_col_size < 1) ? unavailable : String.valueOf(objMySQLSettings.mysql_col_size)));
				System.out.println("mysql_codepage: " + ((objMySQLSettings.mysql_codepage.isEmpty()) ? unavailable : objMySQLSettings.mysql_codepage.toString()));
				System.out.println("Last used SQL query: " + ((objMySQLSettings.mysql_query.isEmpty()) ? unavailable : objMySQLSettings.mysql_query.toString()));
				break;
			case "clear":
				lst_obj.clear();
				System.out.println("The commands' history has been erased.");
				break;
			case "printenv": // http://spec-zone.ru/RU/Java/Tutorials/essential/environment/env.html
				new External().printEnvs();
				break;
			case "pwd":
				System.out.println(System.getProperty(sysHome)); // http://www.helloworld.ru/texts/comp/lang/java/java/12.htm
				break;
			case "charsets":
				Object[] availCPs = Charset.availableCharsets().values().toArray();
				for (Object sTemp05 : availCPs) {
					System.out.println(sTemp05.toString());
				}
				System.out.println("Found: " + String.valueOf(availCPs.length) + " item(s).");
				break;
			case "ver": case "uname":
				System.out.println(System.getProperty("java.vendor") + ": Java " + System.getProperty("java.version") + "/" + System.getProperty("os.name"));
				break;
			case "resetdir":
				try {
					System.setProperty(sysHome, defaultDir);
					System.out.println("You've been redirected to the default directory.");
					break;
				}
				catch (Exception e) {
					System.err.println(cantChngDir);
					break;
				}
			case "help": case "?":
				String[] cmdListTemp = new String[cmdList.length / 2];
				for (x = 0; x < cmdList.length / 2; x++) {
					cmdListTemp[x] = cmdList[2 * x];
				}
				System.out.println(Arrays.toString(cmdListTemp));
				break;
			case "date": case "time":
				java.util.Calendar today = java.util.Calendar.getInstance();
				System.out.println(today.getTime());
				break;
			case "network": case "connection":
				System.out.println("Codepage: " + ((objNetworkSettings.codepage.isEmpty()) ? unavailable : objNetworkSettings.codepage.toString()));
				System.out.println("Connection: " +  ((objNetworkSettings.proxyhost.isEmpty()) ? "DIRECT" : "PROXY")   );
				if (!(objNetworkSettings.proxyhost.isEmpty())) { // if thru a proxy server
					System.out.println("Host: " + objNetworkSettings.proxyhost.toString());
					System.out.println("Port: " + Integer.toString((objNetworkSettings.proxyport < 1) ? defaultProxyPort : objNetworkSettings.proxyport));
					System.out.println("Proxy user: " + ((objNetworkSettings.uName.isEmpty()) ? unavailable : objNetworkSettings.uName.toString()));
					System.out.println("Proxy password: " + ((objNetworkSettings.uPass.isEmpty()) ? unavailable : objNetworkSettings.uPass.toString()));
					System.out.println( "No-proxy: " + ((objNetworkSettings.noproxy.length == 0) ? unavailable : Arrays.toString(objNetworkSettings.noproxy))      );
				}
				break;
			}
			String[] arrTemp = new String[] {};
			arrTemp = s_converted.split(" ");
			for (x = 0; x < argsArray.length; x++) {
				argsArray[x] = null; // nulling the array's items
			}
			if (arrTemp.length > argsArray.length) {
				for (x = 0; x < argsArray.length; x++) {
					argsArray[x] = arrTemp[x]; 
				}
			} else {
				x = -1;
				for (String sTemp : arrTemp) {
					argsArray[++x] = sTemp;
				}
			}

			/**
			 * Commands with arguments (allowed from 1 through 10 parameters)
			 */		
			switch (argsArray[0].toLowerCase()) {
			case "more": case "read": case "type":
				try {
					System.out.println("Opening the file of '" + pathConverted(argsArray[1], defaultDir) + "' for reading.");
					for (String sTemp02 : new BasicOperations(pathConverted(argsArray[1], defaultDir), pathConverted(argsArray[1], defaultDir)).readTextFile(((objNetworkSettings.codepage.isEmpty()) ? defaultCodepage : objNetworkSettings.codepage.toString()))) {
						System.out.println(sTemp02);
					}
					break;
				}
				catch (Exception e) {
					System.err.println("The file cannot be read out.");
					break;
				}
			case "mkdir": case "md":
				try {
					if ((argsArray[1] != null) && (!argsArray[1].isEmpty())) {
						if (new BasicOperations(pathConverted(argsArray[1], defaultDir), "").mkdir()) {
							System.out.println(result(true));
						} else {
							System.out.println(result(false));
						}
					}
				}
				catch (Exception e) {
					System.err.println(e.toString());
				}
				break;
			case "rmdir": case "rd":
				try {
					if ((argsArray[1] != null) && (!argsArray[1].isEmpty())) {
						BasicOperations objRmDir = new BasicOperations(pathConverted(argsArray[1], defaultDir), "");
						if ((!objRmDir.exists()) || (!objRmDir.isDirectory())) {
							System.out.println(result(false));
						} else {
							if (objRmDir.delete()) {
								System.out.println(result(true));
							} else {
								System.out.println(result(false));
							}
						}
					}
				}
				catch (Exception e) {
					System.err.println(e.toString());
				}
				break;
			case "grep":
				if (((argsArray[1] != null) && (!argsArray[1].isEmpty())) && ((argsArray[2] != null) && (!argsArray[2].isEmpty()))) {
					String fileMGrep = pathConverted(argsArray[1], defaultDir);
					String grepPatternMGrep = s.substring(argsArray[0].length() + argsArray[1].length() + 2);
					greppedFile(fileMGrep, grepPatternMGrep, objNetworkSettings, defaultCodepage);
				}
				break;
			case "mgrep":
				if (((argsArray[1] != null) && (!argsArray[1].isEmpty())) && ((argsArray[2] != null) && (!argsArray[2].isEmpty())) && ((argsArray[3] != null) && (!argsArray[3].isEmpty()))) {
					String fileMGrep2 = pathConverted(argsArray[1], defaultDir);
					String filePatternMGrep2 = argsArray[2];
					String grepPatternMGrep2 = s.substring(argsArray[0].length() + argsArray[1].length() + argsArray[2].length() + 3);
					try {
						String[] getFilesMGrep = new BasicOperations("", "").findFiles(fileMGrep2, filePatternMGrep2, (byte) 1);
						for (String arrTemp2 : getFilesMGrep) {
							java.io.File greppedFileObj = new java.io.File(arrTemp2);
							if ((greppedFileObj.isFile()) && (greppedFileObj.canRead())) {
								System.out.println("Looking for the pattern in the file of " + "\"" + arrTemp2 + "\"" + "...");
								greppedFile(arrTemp2, grepPatternMGrep2, objNetworkSettings, defaultCodepage);
							}
						}
						System.out.println(String.valueOf("\n" + getFilesMGrep.length) + " items have been scanned.\n");
					}
					catch (Exception e) {
						System.err.println(e.toString());
					}
				}
				break;
			case "whois":
				final String whoisHost = "whois.ripe.net"; // WHOIS server's host
				final int whoisPort = 43; // WHOIS server's port
				String ip = s.substring(argsArray[0].length() + 1);
				try {
					SocketXT objSocketXT = new SocketXT(
							(
									((objNetworkSettings.proxyhost == null) || (objNetworkSettings.proxyhost.isEmpty())) ? // direct connection?
											Proxy.NO_PROXY
											: new Proxy(
													Proxy.Type.HTTP,
													new InetSocketAddress(
															objNetworkSettings.proxyhost,
															((objNetworkSettings.proxyport < 1) ? defaultProxyPort : objNetworkSettings.proxyport)
															)
													)
									),
							(
									((objNetworkSettings.proxyhost == null) || (objNetworkSettings.proxyhost.isEmpty()) || (objNetworkSettings.uName == null) || (objNetworkSettings.uName.isEmpty())) ? // direct connection?
											false
											: true
									),
							(
									((objNetworkSettings.uName == null) || (objNetworkSettings.uName.isEmpty())) ?
											""
											: objNetworkSettings.uName
									),
							(
									((objNetworkSettings.uPass == null) || (objNetworkSettings.uPass.isEmpty())) ?
											""
											: objNetworkSettings.uPass
									),
							(
									(objNetworkSettings.noproxy == null) ?
											new String[] {}
											: objNetworkSettings.noproxy
									),
							new InetSocketAddress(whoisHost, whoisPort) // a host to get connected to
							);
					System.out.println("We are trying to get a WHOIS response for " + "\"" + ip + "\"" + " from " + whoisHost + ":" + String.valueOf(whoisPort) + "...");
					String[] resp = new WhoIs(
							objSocketXT,
							(
									((objNetworkSettings.codepage == null) || (objNetworkSettings.codepage.isEmpty())) ?
											defaultCodepage
											: objNetworkSettings.codepage
									),
							ip
							).whoisResponse();
					if (resp.length < 1) {
						System.out.println("Nothing has received from the server.");
					} else {
						System.out.println("The answer has come.\n");
						for (String arrTemp2 : resp) {
							System.out.println(arrTemp2);
						}
					}
				}
				catch (Exception e) {
					System.err.println("\"" + e.getMessage() + "\"" + ": This has happened during executing the request.");
				}
				break;
			case "dnsbl":
				String ip2 = s.substring(argsArray[0].length() + 1);
				if (new DroneBL(ip2)._isBlacklisted()) {
					System.out.println("This IP address seems to be blacklisted.");
				} else {
					System.out.println("This IP address isn't blacklisted.");
				}
				break;
			case "say": case "echo":
				System.out.println(s.substring(argsArray[0].length() + 1));
				break;
			case "oracle_user":
				objOracleSettings.oracle_user = s.substring(argsArray[0].length() + 1);
				_changed(argsArray[0]);
				break;
			case "oracle_password":
				objOracleSettings.oracle_password = s.substring(argsArray[0].length() + 1);
				_changed(argsArray[0]);
				break;
			case "oracle_url":
				objOracleSettings.oracle_url = s.substring(argsArray[0].length() + 1);
				_changed(argsArray[0]);
				break;
			case "oracle_ctrl_line":
				objOracleSettings.oracle_ctrl_line = s.substring(argsArray[0].length() + 1);
				_changed(argsArray[0]);
				break;
			case "oracle_col_size":
				try {
					tempVar01 = Integer.parseInt(s.substring(argsArray[0].length() + 1));
					if (tempVar01 < 1) {
						tempVar01 = 0;
					}
					if (tempVar01 > HigherColLim) {
						tempVar01 = HigherColLim;
					}
					objOracleSettings.oracle_col_size = tempVar01;
					_changed(argsArray[0]);
				}
				catch(Exception e) {
					System.err.println("A natural number is expected.");
				}
				break;
			case "use_old_oracle":
				if ((argsArray[1] == null) || (argsArray[1].isEmpty())) {
					System.err.println("Unknown parameter.");
				} else {
					if (argsArray[1].toLowerCase().equals("true")) { // http://stackoverflow.com/questions/9156379/ora-01882-timezone-region-not-found
						System.setProperty("oracle.jdbc.timezoneAsRegion", "false");
						System.out.println("Emulating the use of an old Oracle DBMS has been activated.");
					} else {
						if (argsArray[1].toLowerCase().equals("false")) {
							System.setProperty("oracle.jdbc.timezoneAsRegion", "true");
							System.out.println("Support of an old Oracle DBMS is disabled.");
						} else {
							System.err.println("Unknown parameter.");
						}
					}
				}
				break;
			case "oracle_query":
				if (
						((objOracleSettings.oracle_user.isEmpty()) || (objOracleSettings.oracle_user == null))
						|| ((objOracleSettings.oracle_password.isEmpty()) || (objOracleSettings.oracle_password == null))
						|| ((objOracleSettings.oracle_url.isEmpty()) || (objOracleSettings.oracle_url == null))
						|| ((objOracleSettings.oracle_ctrl_line.isEmpty()) || (objOracleSettings.oracle_ctrl_line == null))
						) {
					System.out.println("Some parameters are undefined for Oracle.");
					break;
				}
				objOracleSettings.oracle_query = s.substring(argsArray[0].length() + 1);
				System.out.println("Establishing connection to the Oracle database...");

				try {
					new Tables().printTable(
							new Oracle_DB(objOracleSettings.oracle_user, objOracleSettings.oracle_password, objOracleSettings.oracle_url, objOracleSettings.oracle_ctrl_line).executeQuery(
									objOracleSettings.oracle_query
									),
							objOracleSettings.oracle_col_size
							); // printing the table
				}
				catch(Exception e) {
					System.out.println();
					System.err.println(result(false));
					System.err.println("~ " + e.toString());
				}
				System.out.println(result(true));
				break;
			case "oracle_file2query":
				try {
					if (
							((objOracleSettings.oracle_user.isEmpty()) || (objOracleSettings.oracle_user == null))
							|| ((objOracleSettings.oracle_password.isEmpty()) || (objOracleSettings.oracle_password == null))
							|| ((objOracleSettings.oracle_url.isEmpty()) || (objOracleSettings.oracle_url == null))
							|| ((objOracleSettings.oracle_ctrl_line.isEmpty()) || (objOracleSettings.oracle_ctrl_line == null))
							) {
						System.out.println("Some parameters are undefined for Oracle.");
						break;
					}
					Function<String[], String> convertOracleQuery = new Function<String[], String>() {
						@Override
						public String apply(String[] from) {
							int c = 0;
							String s2;
							StringBuilder s = new StringBuilder();
							for (String sTemp02 : from) {
								s.append(sTemp02 + "\n");
							}
							for (int x = s.length() - 1; x >= 0; x--) {
								if (!(String.valueOf(s.charAt(x)).equals("\n"))) {
									break;
								}
								c++;
							}
							s2 = s.substring(0, s.length() - c);
							return s2;
						}
					};
					objOracleSettings.oracle_query = convertOracleQuery.apply(new BasicOperations(pathConverted(s.substring(argsArray[0].length() + 1), defaultDir), pathConverted(argsArray[0], defaultDir)).readTextFile(((objNetworkSettings.codepage.isEmpty()) ? defaultCodepage : objNetworkSettings.codepage.toString())));
					System.out.println("Fetching a result from the SQL query as follows:\n\n" + objOracleSettings.oracle_query + "\n\n");

					System.out.println("Establishing connection to the Oracle database...");
					try {
						new Tables().printTable(
								new Oracle_DB(objOracleSettings.oracle_user, objOracleSettings.oracle_password, objOracleSettings.oracle_url, objOracleSettings.oracle_ctrl_line).executeQuery(
										objOracleSettings.oracle_query
										),
								objOracleSettings.oracle_col_size
								); // printing the table
					}
					catch(Exception e) {
						System.out.println();
						System.err.println(result(false));
						System.err.println("~ " + e.toString());
					}
					System.out.println(result(true));
					break;
				}
				catch (Exception e) {
					System.err.println("The file cannot be read out.");
					break;
				}
			case "mysql_user":
				objMySQLSettings.mysql_user = s.substring(argsArray[0].length() + 1);
				_changed(argsArray[0]);
				break;
			case "mysql_password":
				objMySQLSettings.mysql_password = s.substring(argsArray[0].length() + 1);
				_changed(argsArray[0]);
				break;
			case "mysql_url":
				objMySQLSettings.mysql_url = s.substring(argsArray[0].length() + 1);
				_changed(argsArray[0]);
				break;
			case "mysql_ctrl_line":
				objMySQLSettings.mysql_ctrl_line = s.substring(argsArray[0].length() + 1);
				_changed(argsArray[0]);
				break;
			case "mysql_col_size":
				try {
					tempVar01 = Integer.parseInt(s.substring(argsArray[0].length() + 1));
					if (tempVar01 < 1) {
						tempVar01 = 0;
					}
					if (tempVar01 > HigherColLim) {
						tempVar01 = HigherColLim;
					}
					objMySQLSettings.mysql_col_size = tempVar01;
					_changed(argsArray[0]);
				}
				catch(Exception e) {
					System.err.println("A natural number is expected.");
				}
				break;
			case "mysql_codepage":
				objMySQLSettings.mysql_codepage = s.substring(argsArray[0].length() + 1);
				_changed(argsArray[0]);
				break;
			case "mysql_query":
				if (
						((objMySQLSettings.mysql_user.isEmpty()) || (objMySQLSettings.mysql_user == null))
						|| ((objMySQLSettings.mysql_password.isEmpty()) || (objMySQLSettings.mysql_password == null))
						|| ((objMySQLSettings.mysql_url.isEmpty()) || (objMySQLSettings.mysql_url == null))
						|| ((objMySQLSettings.mysql_ctrl_line.isEmpty()) || (objMySQLSettings.mysql_ctrl_line == null))
						) {
					System.out.println("Some parameters are undefined for MySQL.");
					break;
				}
				objMySQLSettings.mysql_query = s.substring(argsArray[0].length() + 1);
				System.out.println("Establishing connection to the MySQL database...");
				try {
					new Tables().printTable(
							new MySQL_DB(objMySQLSettings.mysql_user, objMySQLSettings.mysql_password, objMySQLSettings.mysql_url, objMySQLSettings.mysql_ctrl_line, objMySQLSettings.mysql_codepage).executeQuery(
									objMySQLSettings.mysql_query
									),
							objMySQLSettings.mysql_col_size
							); // printing the table
				}
				catch(Exception e) {
					System.out.println();
					System.err.println(result(false));
					System.err.println("~ " + e.toString());
				}
				System.out.println(result(true));
				break;
			case "mysql_file2query":
				try {
					if (
							((objMySQLSettings.mysql_user.isEmpty()) || (objMySQLSettings.mysql_user == null))
							|| ((objMySQLSettings.mysql_password.isEmpty()) || (objMySQLSettings.mysql_password == null))
							|| ((objMySQLSettings.mysql_url.isEmpty()) || (objMySQLSettings.mysql_url == null))
							|| ((objMySQLSettings.mysql_ctrl_line.isEmpty()) || (objMySQLSettings.mysql_ctrl_line == null))
							) {
						System.out.println("Some parameters are undefined for MySQL.");
						break;
					}
					Function<String[], String> convertMySQLQuery = new Function<String[], String>() {
						@Override
						public String apply(String[] from) {
							int c = 0;
							String s2;
							StringBuilder s = new StringBuilder();
							for (String sTemp02 : from) {
								s.append(sTemp02 + "\n");
							}
							for (int x = s.length() - 1; x >= 0; x--) {
								if (!(String.valueOf(s.charAt(x)).equals("\n"))) {
									break;
								}
								c++;
							}
							s2 = s.substring(0, s.length() - c);
							return s2;
						}
					};
					objMySQLSettings.mysql_query = convertMySQLQuery.apply(new BasicOperations(pathConverted(s.substring(argsArray[0].length() + 1), defaultDir), pathConverted(argsArray[0], defaultDir)).readTextFile(((objNetworkSettings.codepage.isEmpty()) ? defaultCodepage : objNetworkSettings.codepage.toString())));
					System.out.println("Fetching a result from the SQL query as follows:\n\n" + objMySQLSettings.mysql_query + "\n\n");

					System.out.println("Establishing connection to the MySQL database...");
					try {
						new Tables().printTable(
								new MySQL_DB(objMySQLSettings.mysql_user, objMySQLSettings.mysql_password, objMySQLSettings.mysql_url, objMySQLSettings.mysql_ctrl_line, objMySQLSettings.mysql_codepage).executeQuery(
										objMySQLSettings.mysql_query
										),
								objMySQLSettings.mysql_col_size
								); // printing the table
					}
					catch(Exception e) {
						System.out.println();
						System.err.println(result(false));
						System.err.println("~ " + e.toString());
					}
					System.out.println(result(true));
					break;
				}
				catch (Exception e) {
					System.err.println("The file cannot be read out.");
					break;
				}
			case "hint":
				if ((argsArray[1] == null) || (argsArray[1].isEmpty())) {
					System.out.println("This command has been used incorrectly.\nType \"hint hint\" to find out what went wrong.");
				} else {
					boolean flag01 = false;
					for (x = 0; x < cmdList.length / 2; x++) {
						if (cmdList[2 * x].equals(argsArray[1])) {
							System.out.println(cmdList[2 * x + 1]);
							flag01 = true; //found
							break;
						}
					}
					if (!flag01) {
						System.out.println("This command isn't in the list.");
					}
				}
				break;
			case "copy": case "cp":
				try {
					System.out.println(result(new BasicOperations(pathConverted(argsArray[1], defaultDir), pathConverted(argsArray[2], defaultDir)).binaryCopy()));
					break;
				}
				catch (Exception e) {
					System.err.println("Error on copying.");
					break;
				}
			case "fc": case "diff":
				try {
					System.out.println("Comparing whether the two files are equal: \"" + pathConverted(argsArray[1], defaultDir) + "\" and \"" + pathConverted(argsArray[2], defaultDir) + "\".");
					System.out.println(result(new BasicOperations(pathConverted(argsArray[1], defaultDir), pathConverted(argsArray[2], defaultDir)).binaryFileCompare()));
					break;
				}
				catch (Exception e) {
					System.err.println(e.getMessage());
					break;
				}
			case "size":
				BasicOperations objFileSize = new BasicOperations(pathConverted(argsArray[1], defaultDir), "");
				if (objFileSize.exists()) {
					System.out.println("File's size: " + String.valueOf(objFileSize.length()) + " byte(s).");
				} else {
					System.out.println("This file cannot be found.");
					System.out.println("Try using \".\", \"..\", \"~\", \"" + System.getProperty(fileSeparator) + "\" at the beginning of the path.");
				}
				break;
			case "mv": case "rename": case "ren":
				System.out.println(result(new BasicOperations(pathConverted(argsArray[1], defaultDir), pathConverted(argsArray[2], defaultDir)).renameTo()));
				break;
			// The further Java methods have been inherited from the java.io.File class
			case "erase": case "rm": case "del": case "delete":
				System.out.println(result(new BasicOperations(pathConverted(argsArray[1], defaultDir), pathConverted(argsArray[2], defaultDir)).delete()));
				break;
			case "getname":
				try {
					System.out.println(new BasicOperations(pathConverted(argsArray[1], defaultDir), pathConverted(argsArray[2], defaultDir)).getAbsolutePath());
					break;
				}
				catch (Exception e) {
					System.err.println("No file or directory has been specified.");
					break;
				}
			case "dir": case "ls":
				try {
					for (String sTemp03 : new BasicOperations(pathConverted(argsArray[1], defaultDir), pathConverted(argsArray[2], defaultDir)).dir()) {
						System.out.println(sTemp03);
					}
					System.out.println(System.getProperty(sysHome));
					break;
				}
				catch (Exception e) {
					System.err.println("A non-existent directory has been defined, or you do not have permission to access it.");
					break;
				}
			case "mlist":
				if (((argsArray[1] != null) && (!argsArray[1].isEmpty())) && ((argsArray[2] != null) && (!argsArray[2].isEmpty()))) {
					String fileMList = pathConverted(argsArray[1], defaultDir);
					String grepPatternMList = s.substring(argsArray[0].length() + argsArray[1].length() + 2);
					try {
						for (String arrTemp3 : new BasicOperations(fileMList, null).dirMask(grepPatternMList)  ) {
							System.out.println(arrTemp3);
						}
						System.out.println(System.getProperty(sysHome));
					}
					catch (Exception e) {
						System.err.println("A non-existent directory has been defined, or you do not have permission to access it.");
					}
				}
				break;
			case "cd": case "chdir":
				try {
					System.out.println("Changing to \"" + pathConverted(argsArray[1], defaultDir) + "\".");
					System.setProperty(sysHome, pathConverted(argsArray[1], defaultDir));
					_changed("The directory");
					break;
				}
				catch (Exception e) {
					System.err.println(cantChngDir);
					break;
				}
			case "url": case "wget":
				try {
					System.out.println("Opening the URL of '" + argsArray[1] + "' for reading.");
					for (String sTemp04 : ((
							!(objNetworkSettings.proxyhost.isEmpty()) // if thru a proxy server
							) ? new BasicOperations(argsArray[1], "").getWebpageOverProxy(objNetworkSettings.codepage, objNetworkSettings.proxyhost, ((objNetworkSettings.proxyport < 1) ? defaultProxyPort : objNetworkSettings.proxyport), objNetworkSettings.uName, objNetworkSettings.uPass, objNetworkSettings.noproxy) : new BasicOperations(argsArray[1], "").getWebpage(objNetworkSettings.codepage))) {
						System.out.println(sTemp04);
					}
					break;
				}
				catch (Exception e) {
					System.err.println("The URL cannot be read out.");
					break;
				}
			case "dump":
				try {
					System.out.println("Saving the history to the file...");
					String[] stringDump = new String[] {};
					stringDump = lst_obj.toArray(new String[lst_obj.size()]); // dumping into a normal array
					new BasicOperations(pathConverted(argsArray[1], defaultDir), pathConverted(argsArray[2], defaultDir)).writeTextFile(stringDump, objNetworkSettings.codepage);
					System.out.println("Done.");
					break;
				}
				catch (Exception e) {
					System.err.println("Error opening the file for writing.");
					break;
				}
			case "mim":
				if ((argsArray[1] != null) && (argsArray[2] != null) && (argsArray[3] != null)) {
					if ((java.nio.charset.Charset.isSupported(argsArray[2].toUpperCase())) && (java.nio.charset.Charset.isSupported(argsArray[3].toUpperCase()))) {
						try {
							String[] mimLines = null;
							mimLines = new Coding().parseMIM(pathConverted(argsArray[1], defaultDir), argsArray[2], argsArray[3]);
							for (String sTemp07 : mimLines) {
								if (objNetworkSettings.codepage.isEmpty()) {
									System.out.println(sTemp07);
								} else {
									System.out.println(new String( sTemp07.getBytes(argsArray[3]), objNetworkSettings.codepage ));
								}
							}
						}
						catch (Exception e) {
							System.err.println(e.toString());
						}
					} else {
						System.out.println("Sorry! This codepage is not supported.");
						System.out.println("Type \"charsets\" to get a list of all the available codepages.");
					}
				}
				break;
			case "mim2file":
				if ((argsArray[1] != null) && (argsArray[2] != null) && (argsArray[3] != null) && (argsArray[4] != null)) {
					if ((java.nio.charset.Charset.isSupported(argsArray[2].toUpperCase())) && (java.nio.charset.Charset.isSupported(argsArray[3].toUpperCase()))) {
						try {
							new BasicOperations(pathConverted(argsArray[4], defaultDir), "").writeTextFile(
									new Coding().parseMIM(pathConverted(argsArray[1], defaultDir), argsArray[2], argsArray[3]),
									argsArray[3]
									);
							System.out.println("See the file of \"" + pathConverted(argsArray[4], defaultDir) + "\".");
						}
						catch (Exception e) {
							System.err.println(e.toString());
						}
					} else {
						System.out.println("Sorry! This codepage is not supported.");
						System.out.println("Type \"charsets\" to get a list of all the available codepages.");
					}
				}
				break;
			case "codepage":
				try {
					if (argsArray[1].isEmpty()) {
						objNetworkSettings.codepage = "";
						System.err.println(undef);
					} else {
						if (java.nio.charset.Charset.isSupported(argsArray[1].toUpperCase())) {
							objNetworkSettings.codepage = argsArray[1].toUpperCase(); 
							System.out.println("The codepage has been successfully changed for " + objNetworkSettings.codepage.toString() + ".");
						} else {
							System.out.println("Sorry! This codepage is not supported.");
							System.out.println("Type \"charsets\" to get a list of all the available codepages.");
						}
					}
					break;
				}
				catch (Exception e) {
					objNetworkSettings.codepage = "";
					System.err.println(undef);
					break;
				}
			case "proxyhost":
				try {
					if (argsArray[1].isEmpty()) {
						objNetworkSettings.proxyhost = "";
						System.err.println(undef);
					} else {
						objNetworkSettings.proxyhost = argsArray[1]; 
						System.out.println("The proxy host has been successfully changed for " + objNetworkSettings.proxyhost.toString() + ".");
					}
					break;
				}
				catch (Exception e) {
					objNetworkSettings.proxyhost = "";
					System.err.println(undef);
					break;
				}
			case "proxyport":
				try {
					if (argsArray[1].isEmpty()) {
						objNetworkSettings.proxyport = 0;
						System.err.println(undef);
					} else {
						objNetworkSettings.proxyport = Integer.parseInt(argsArray[1]); 
						System.out.println("The proxy port has been successfully changed for " + Integer.toString(objNetworkSettings.proxyport) + ".");
					}
					break;
				}
				catch (Exception e) {
					objNetworkSettings.proxyport = 0;
					System.err.println(undef);
					break;
				}
			case "proxyuser":
				try {
					if (argsArray[1].isEmpty()) {
						objNetworkSettings.uName = "";
						System.err.println(undef);
					} else {
						objNetworkSettings.uName = argsArray[1]; 
						System.out.println("The proxy user has been successfully changed for " + objNetworkSettings.uName.toString() + ".");
					}
					break;
				}
				catch (Exception e) {
					objNetworkSettings.uName = "";
					System.err.println(undef);
					break;
				}
			case "proxypassword":
				try {
					if (argsArray[1].isEmpty()) {
						objNetworkSettings.uPass = "";
						System.err.println(undef);
					} else {
						objNetworkSettings.uPass = argsArray[1]; 
						System.out.println("The proxy password has been successfully changed for " + objNetworkSettings.uPass.toString() + ".");
					}
					break;
				}
				catch (Exception e) {
					objNetworkSettings.uPass = "";
					System.err.println(undef);
					break;
				}
			case "netcalc": case "cidr": // http://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing#CIDR_notation
				try {
					if (argsArray[1].isEmpty()) {
						System.err.println(undef);
					} else {
						System.out.println("The IPv4 block " + argsArray[1] + " represents the IPv4 addresses as follows: " + new BasicOperations("", "").netcalc(argsArray[1], objNetworkSettings.noproxy) + "."); 
					}
					break;
				}
				catch (Exception e) {
					System.err.println(e.toString());
					break;
				}
			case "ff": case "find":
				try {
					if (argsArray[1].isEmpty()) {
						System.err.println(undef);
					} else {
						System.out.println("Getting recursively files and directories by the pathname - wait...");
						String[] ffObj = new BasicOperations("", "").findFiles(pathConverted(argsArray[1], defaultDir), null, (byte) 1);
						for (String sTemp06 : ffObj) {
							System.out.println(sTemp06);
						}
						System.out.println("Found: " + String.valueOf(ffObj.length) + " items.");
					}
					break;
				}
				catch (Exception e) {
					System.err.println(e.toString());
					break;
				}
			case "noproxy": case "no-proxy": case "no_proxy":
				try {
					String tempNProxy = s.substring(argsArray[0].length() + 1);
					if (tempNProxy.split(",").length == 0) {
						objNetworkSettings.noproxy = new String[] {};
						System.err.println(undef);
					} else {
						objNetworkSettings.noproxy = tempNProxy.split(",");
						for (x = 0; x < objNetworkSettings.noproxy.length; x++) {
							objNetworkSettings.noproxy[x] = objNetworkSettings.noproxy[x].trim();
						}
						System.out.println("The noproxy environment variable has been successfully changed for " + Arrays.toString(objNetworkSettings.noproxy) + ".");
					}
					break;
				}
				catch (Exception e) {
					System.out.println("dssdd");
					objNetworkSettings.noproxy = new String[] {};
					System.err.println(undef);
					break;
				}
			case "exec": // http://blog.eqlbin.ru/2010/09/java.html
				try {
					procArgs.clear();
					for (x = 1; x < argsArray.length; x++) {
						if ((argsArray[x] == null) || (argsArray[x].isEmpty())) {
							break;
						}
						procArgs.add(argsArray[x]);
					}
					if (procArgs.size() < 1) {
						System.err.println(undef);
					} else {
						System.out.println("Errorlevel: " + String.valueOf(new External().execCommand(procArgs, System.getProperty(sysHome), objNetworkSettings.codepage)) + ".");
					}
				}
				catch (Exception e) {
					System.err.println(e.toString());
				}
				break;
			case "webpage2file":
				try {
					if ((!(argsArray[1].isEmpty())) && (!(argsArray[2].isEmpty()))) {
						System.out.println("Saving the web page of " + argsArray[1] + " to the file of " + pathConverted(argsArray[2], defaultDir) + "...");
						new BasicOperations(pathConverted(argsArray[2], defaultDir), "").writeTextFile(
								(
										!(objNetworkSettings.proxyhost.isEmpty()) // if thru a proxy server
										) ? new BasicOperations(argsArray[1], "").getWebpageOverProxy(objNetworkSettings.codepage, objNetworkSettings.proxyhost, ((objNetworkSettings.proxyport < 1) ? defaultProxyPort : objNetworkSettings.proxyport), objNetworkSettings.uName, objNetworkSettings.uPass, objNetworkSettings.noproxy) : new BasicOperations(argsArray[1], "").getWebpage(objNetworkSettings.codepage),
												objNetworkSettings.codepage
								);
						System.out.println("Done.");
					}
					break;
				}
				catch (Exception e) {
					System.err.println("Some unexpected error has occurred.");
					break;
				}
			case "binaryurl2file":
				try {
					System.out.println(result(
							(
									objNetworkSettings.proxyhost.isEmpty() // is it direct connection?
									) ? new BasicOperations(argsArray[1], pathConverted(argsArray[2], defaultDir)).binarySaveFromURL("", 0, "", "", objNetworkSettings.noproxy) : new BasicOperations(argsArray[1], pathConverted(argsArray[2], defaultDir)).binarySaveFromURL(objNetworkSettings.proxyhost, objNetworkSettings.proxyport, objNetworkSettings.uName, objNetworkSettings.uPass, objNetworkSettings.noproxy)
							));
					break;
				}
				catch (Exception e) {
					System.err.println("Error while binary saving to the file from the URL.");
					break;
				}
			case "iconv": // iconv codepage1 _file1 codepage2 _file2
				String codepage1 = new String();
				String codepage2 = new String();
				String _file1 = new String();
				String _file2 = new String();
				if (
						((argsArray[1] == null) || (argsArray[1].isEmpty())) // codepage1
						|| ((argsArray[2] == null) || (argsArray[2].isEmpty())) // _file1
						|| ((argsArray[3] == null) || (argsArray[3].isEmpty())) // codepage2
						|| ((argsArray[4] == null) || (argsArray[4].isEmpty())) // _file2
						) {
					System.err.println("Wrong usage.");
				} else {
					codepage1 = argsArray[1];
					codepage2 = argsArray[3];
					_file1 = pathConverted(argsArray[2], defaultDir);
					_file2 = pathConverted(argsArray[4], defaultDir);
					if ((!(Charset.isSupported(codepage1))) || (!(Charset.isSupported(codepage2)))) {
						System.err.println("An unsupported codepage has been found as a parameter.");
						System.err.println("Type \"charsets\" to get a list of all the available codepages.");
					} else {
						try {
							new BasicOperations(_file2, "").writeTextFile(new BasicOperations(_file1, "").readTextFile(codepage1), codepage2);
							System.out.println("The file \"" + _file2 + "\" is ready.");
						}
						catch(Exception e) {
							System.err.println("I/O error while accessing the files.");
						}
					}
				}
				break;
			}

		}

		System.out.println("Good-bye!");
		System.out.println();

		System.out.println("* Copyleft 2016 Artem Efremov");
		System.out.println("*");
		System.out.println("* Redistribution and use in source and binary forms, with or without modification, are permitted.");
	}

}