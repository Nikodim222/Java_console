package files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.NullPointerException;
import java.net.URL;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.InputStream;
import java.net.Proxy;
import java.net.InetSocketAddress;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import it.sauronsoftware.ftp4j.FTPClient; // http://www.sauronsoftware.it/projects/ftp4j/
import it.sauronsoftware.ftp4j.FTPFile;

/**
 * This class contains a set of methods to extend the existing java.io.File one.
 * @author Nikodim
 * @param from, to
 *
 */
public class BasicOperations extends File {

	private String from_filename = new String();
	private String to_filename = new String();
	private final boolean good = true; // successful
	private final boolean bad = false; // unsuccessful
	private final String readmeHeader = new String("Any questions can be sent at nspu@list.ru"); // house style ;-)

	private static final long serialVersionUID = 1L;

	public BasicOperations(String from, String to) {
		super(from);
		this.from_filename = from;
		this.to_filename = to;
	}

	/**
	 * This private class checks whether the host is at a no-proxy range or not.
	 * Its _isTrue() function returns false or true, accordingly.
	 * @author Nikodim
	 *
	 */
	private class NoProxyClass {
		private String[] noproxy = new String[] {};

		public NoProxyClass(String[] nproxy) {
			this.noproxy = nproxy;
		}

		private boolean _isTrue(String url) {
			try {
				String temp;
				String host = new URL(url).getHost();
				for (String sTemp : this.noproxy) {
					if (sTemp.equals(host)) {
						return good;
					}
					if (sTemp.charAt(0) == (char) '.') {
						temp = sTemp.substring(1);
						if (
								(host.equals(temp))
								|| (
										(host.length() - temp.length() - 1 >= 0)
										&& (host.substring(host.length() - temp.length() - 1).equals("." + temp))
										)
								) {
							return good;
						}
					}
					if (sTemp.lastIndexOf("/") > -1) { // http://www.lanberry.ru/lan/kaljkulyator_Netmask
						byte[][] resp = this.boundsByMask(sTemp);
						long ipLong, ipLong2, hostLong;
						ipLong = this._byteArr2long(resp[0]);
						ipLong2 = this._byteArr2long(resp[1]);
						hostLong = this._byteArr2long(InetAddress.getByName(host).getAddress());
						if ((hostLong >= ipLong) && (hostLong <= ipLong2)) {
							return good;
						}
					}
				}
				return bad;
			}
			catch (Exception e) { // catching java.net.MalformedURLException
				return bad;
			}
		}

		private long _byteArr2long(byte[] b) {
			long res = 0;
			for (int x = 0; x < b.length; x++) {
				// res += Byte.toUnsignedLong(b[Math.abs(x - (b.length - 1))]) * (long) (Math.pow(2, 8 * x)); // this only works since JDK 1.8
				res += ((long) ((byte) b[Math.abs(x - (b.length - 1))]) & (long) 0x00000000000000FF) * (long) (Math.pow(2, 8 * x));
			}
			return res;
		}

		private byte[][] boundsByMask(String subnet) throws UnknownHostException, IOException {
			byte[][] resp = new byte[2][];
			if (subnet.lastIndexOf("/") > -1) { // http://www.lanberry.ru/lan/kaljkulyator_Netmask
				String[] ipRange = subnet.split("/");
				if (ipRange.length != 2) {
					throw new IOException("Incorrect IP range."); // incorrect IP range
				}
				byte[] ip = InetAddress.getByName(ipRange[0]).getAddress();
				byte[] ip2 = new byte[ip.length];
				System.arraycopy(ip, 0, ip2, 0, ip2.length);
				byte mask = Byte.parseByte(ipRange[1]);
				if ((mask < 0) || (mask > 32)) {
					throw new IOException("Incorrect IP range."); // incorrect IP range
				}
				mask = (byte) ((byte) 32 - mask);
				for (int x = 0; x < ip2.length; x++) {
					if (x + 1 <= mask / 8) {
						ip2[Math.abs(x - (ip2.length - 1))] |= (byte) (Math.pow(2, 8) - 1);
					}
					if ((mask % 8 > 0) && (mask / 8 + 1 == x + 1)) {
						ip2[Math.abs(x - (ip2.length - 1))] |= (byte) (Math.pow(2, mask % 8) - 1);
					}
				}
				resp[0] = ip; // lower bound
				resp[1] = ip2; // upper bound
			}
			return resp;
		}

		private String ipRange(String subnet) throws UnknownHostException, IOException {
			byte[][] resp = this.boundsByMask(subnet);
			return InetAddress.getByAddress(resp[0]).getHostAddress() + " - " + InetAddress.getByAddress(resp[1]).getHostAddress();
		}
	}

	/**
	 * This function returns a range for an IPv4 subnet by its CIDR notation.
	 * For further information, read at http://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing#CIDR_notation
	 * Be ready to catch an exception, it throws.
	 * @param subnet
	 * @param noproxy
	 * @return String
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public String netcalc(String subnet, String[] noproxy) throws UnknownHostException, IOException {
		return new NoProxyClass(noproxy).ipRange(subnet);
	}

	/**
	 * This boolean function copies a binary file.
	 * It returns true if successful; false, otherwise.
	 * Be ready to catch an exception, it throws.
	 * @return boolean
	 * @throws IOException
	 */
	public boolean binaryCopy() throws IOException {
		if (
				(this.from_filename.isEmpty())
				|| (this.to_filename.isEmpty())
				|| (this.from_filename.equals(this.to_filename))
				) {
			return this.bad;
		}
		final int BUFFERSIZE = 32767; // the size of a data buffer
		File myFile = new File(this.from_filename);
		if (!myFile.exists()) { // check whether the file does not exist
			return this.bad;
		} else {
			if (myFile.isFile()) {
				if (myFile.canRead()) {
					FileInputStream from_file = null;
					FileOutputStream to_file = null;
					try {
						from_file = new FileInputStream(this.from_filename);
						to_file = new FileOutputStream(this.to_filename);
						byte[] buffer = new byte[BUFFERSIZE];
						int bytes_read;
						while ((bytes_read = from_file.read(buffer)) != -1) {
							to_file.write(buffer, 0, bytes_read);
						}
					}
					finally {
						if (from_file != null) {
							from_file.close();
						}
						if (to_file != null) {
							to_file.close();
						}
					}
				} else {
					return this.bad;
				}
			} else {
				return this.bad;
			}
		}
		return this.good;
	}

	/**
	 * This boolean function compares two files.
	 * It returns true if equal; false, otherwise.
	 * Be ready to catch an exception, it throws.
	 * @return
	 * @throws IOException
	 */
	public boolean binaryFileCompare() throws IOException {
		if (
				(this.from_filename.isEmpty())
				|| (this.to_filename.isEmpty())
				|| (this.from_filename.equals(this.to_filename))
				) {
			throw new IOException("A bad filename.");
		}
		final int BUFFERSIZE = 32767; // the size of a data buffer
		File myFile1 = new File(this.from_filename);
		File myFile2 = new File(this.to_filename);
		if ((!(myFile1.exists())) || (!(myFile2.exists()))) {
			throw new IOException("One of the files does not exist.");
		}
		if ((!(myFile1.canRead())) || (!(myFile2.canRead()))) {
			throw new IOException("One of the files has no permission to be read out.");
		}
		if (myFile1.length() != myFile2.length()) {
			return this.bad;
		}
		FileInputStream from_file = null;
		FileInputStream to_file = null;
		try {
			from_file = new FileInputStream(this.from_filename);
			to_file = new FileInputStream(this.to_filename);
			byte[] buffer1 = new byte[BUFFERSIZE];
			byte[] buffer2 = new byte[BUFFERSIZE];
			int bytes_read1, bytes_read2;
			while ((bytes_read1 = from_file.read(buffer1)) != -1) {
				bytes_read2 = to_file.read(buffer2);
				if (bytes_read1 != bytes_read2) {
					from_file.close();
					to_file.close();
					return this.bad;
				}
				for (int x = 0; x < buffer1.length; x++) {
					if (buffer1[x] != buffer2[x]) {
						from_file.close();
						to_file.close();
						return this.bad;
					}
				}
			}
		}
		finally {
			if (from_file != null) {
				from_file.close();
			}
			if (to_file != null) {
				to_file.close();
			}
		}
		return this.good;
	}

	/**
	 * This boolean function renames a file.
	 * It returns true if successful; false, otherwise.
	 * @return boolean
	 */
	public boolean renameTo() {
		if (new File(this.from_filename).renameTo(new File(this.to_filename))) {
			return this.good;
		}
		return this.bad;
	}

	private BufferedReader objFooBufferedReader(String codepage) throws FileNotFoundException, IOException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(this.from_filename), ((codepage.isEmpty()) ? "UTF-8" : codepage.toString())));
	}

	/**
	 * This String[] function reads a text file into an array.
	 * Be ready to catch an exception, it throws.
	 * @param codepage
	 * @return String[]
	 * @throws IOException
	 */
	public String[] readTextFile(String codepage) throws IOException {
		BufferedReader obj = objFooBufferedReader(codepage);
		String line01 = new String();
		String[] stringArray = new String[] {};
		ArrayList<String> lst_obj = new ArrayList<String>();
		while ((line01 = obj.readLine()) != null) {
			lst_obj.add(line01.toString());
		}
		obj.close();
		stringArray = lst_obj.toArray(new String[lst_obj.size()]); // dumping into a normal array
		return stringArray;
	}

	private BufferedWriter objFooBufferedWriter(String codepage) throws FileNotFoundException, IOException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.from_filename), ((codepage.isEmpty()) ? "UTF-8" : codepage.toString())));
	}

	/**
	 * This method saves the data in an array to a text file.
	 * Be ready to catch an exception, it throws.
	 * @param lines
	 * @param codepage
	 * @throws IOException
	 */
	public void writeTextFile(String[] lines, String codepage) throws IOException {
		BufferedWriter obj = objFooBufferedWriter(codepage);
		for (String s : lines) {
			obj.write(s + "\n"); // writing to the file
		}
		obj.close();
	}

	/**
	 * This String[] function returns a list of files and directories.
	 * Be ready to catch an exception, it throws.
	 * @return String[]
	 * @throws NullPointerException
	 */
	public String[] dir() throws NullPointerException {
		return this.getFileList(this.fileList(this.from_filename, null));
	}

	/**
	 * This String[] function returns a list of files and directories by a mask.
	 * Be ready to catch an exception, it throws.
	 * @param mask
	 * @return String[]
	 * @throws NullPointerException
	 */
	public String[] dirMask(String mask) throws NullPointerException {
		return this.getFileList(this.fileList(this.from_filename, mask));
	}

	/**
	 * This function returns a File[] object which is used for listing files and directories.
	 * @param path
	 * @param mask
	 * @return File[]
	 */
	private File[] fileList(String path, final String mask) {
		return (mask == null) ? // getting a files' list
				(
						new File(path).listFiles()
						)
				: (
						new File(path).listFiles(
								new FilenameFilter() {
									public boolean accept(File dir, String name) {
										if (name.toLowerCase().indexOf(mask.toLowerCase()) > 0) {
											return true;   // if there's such a file by the mask
										}
										else {
											return false;
										}
									}
								}
								)
						);
	}

	private final String[] getFileList(File[] myFiles) throws NullPointerException {
		final String myTab = new String("\t\t");
		String[] stringArray = new String[] {};
		ArrayList<String> lst_obj = new ArrayList<String>();
		String fn = new String();
		for (File s : myFiles) {
			if (s.isDirectory()) {
				fn = "<DIR>" + myTab + s.getAbsoluteFile();
			} else {
				fn = "<FILE>" + myTab + s.getAbsoluteFile() + myTab + "size: " + String.valueOf(s.length()) + " byte(s)";
			}
			lst_obj.add(fn);
		}
		stringArray = lst_obj.toArray(new String[lst_obj.size()]); // dumping into a normal array
		return stringArray;
	}

	/**
	 * This String[] function reads a web page into an array.
	 * Be ready to catch an exception, it throws.
	 * @param codepage
	 * @return String[]
	 * @throws IOException
	 */
	public String[] getWebpage(String codepage) throws IOException {
		String line01 = new String();
		String[] stringArray = new String[] {};
		ArrayList<String> lst_obj = new ArrayList<String>();
		BufferedReader obj = new BufferedReader(new InputStreamReader(new URL(this.from_filename).openStream(), ((codepage.isEmpty()) ? "UTF-8" : codepage.toString())));
		while ((line01 = obj.readLine()) != null) {
			lst_obj.add(line01.toString());
		}
		stringArray = lst_obj.toArray(new String[lst_obj.size()]); // dumping into a normal array
		return stringArray;
	}

	/**
	 * This String[] function reads a web page through a proxy server into an array.
	 * Be ready to catch an exception, it throws.
	 * @param codepage
	 * @param proxyhost
	 * @param proxyport
	 * @param uName
	 * @param uPass
	 * @param noproxy
	 * @return String[]
	 * @throws IOException
	 */
	public String[] getWebpageOverProxy(String codepage, String proxyhost, int proxyport, String uName, String uPass, String[] noproxy) throws IOException {
		final int defaultProxyPort = 3128; // the port by default
		URL objURL = new URL(this.from_filename);
		String line01 = new String();
		String[] stringArray = new String[] {};
		if (!(proxyhost.isEmpty())) { // the proxyhost variable mustn't be empty
			if (new NoProxyClass(noproxy)._isTrue(this.from_filename) == bad) {
				ArrayList<String> lst_obj = new ArrayList<String>();
				Socket objSocket = new Socket(proxyhost, ((proxyport < 1) ? defaultProxyPort : proxyport)); // establishing a connection
				String pa = new String(this.proxyAuthorizationThing(uName, uPass)); // creating a proxy authorization line (in Base64)
				BufferedReader in = new BufferedReader(new InputStreamReader(objSocket.getInputStream(), ((codepage.isEmpty()) ? "UTF-8" : codepage.toString())));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(objSocket.getOutputStream(), ((codepage.isEmpty()) ? "UTF-8" : codepage.toString())));
				out.print("GET " + objURL.toString() + " " + objURL.getProtocol().toUpperCase() + "/1.1\r\n" +
						"Host: " + objURL.getHost() + ":" + ((objURL.getPort() > 0)? objURL.getPort() : objURL.getDefaultPort()) + "\r\n" +
						"Accept: text/html, application/xml;q=0.9, application/xhtml+xml, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1\r\n" +
						"Connection: close\r\n" +
						"Readme: " + this.readmeHeader + "\r\n" +
						(
								(pa.isEmpty()) ? "" : "Proxy-Authorization: " + pa.toString() + "\r\n"
								) +
						"\r\n");
				out.flush();
				while ((line01 = in.readLine()) != null) {
					lst_obj.add(line01.toString());
				}
				stringArray = lst_obj.toArray(new String[lst_obj.size()]); // dumping into a normal array
				objSocket.close();
			} else {
				stringArray = this.getWebpage(codepage);
			}
		}
		return stringArray;
	}

	/**
	 * This function of the String class is used to generate a value for the Proxy-Authorization header.
	 * @param uName
	 * @param uPass
	 * @return String
	 */
	private String proxyAuthorizationThing(String uName, String uPass) {
		String encodedPassword;
		encodedPassword = (
				(
				(uName.isEmpty())
				&& (uPass.isEmpty())
				) ? "" : javax.xml.bind.DatatypeConverter.printBase64Binary(new String(uName + ":" + uPass).getBytes()) // http://docs.oracle.com/javase/6/docs/api/javax/xml/bind/DatatypeConverter.html#printBase64Binary(byte[])
			);
		return
				(
						(encodedPassword.isEmpty()) ? "" : "Basic " + encodedPassword
						);
	}

	/**
	 * This boolean function saves a file from an URL to a local file.
	 * It returns true if successful; false, otherwise.
	 * Direct connection is required.
	 * Be ready to catch an exception, it throws.
	 * @param proxyhost
	 * @param proxyport
	 * @param uName
	 * @param uPass
	 * @param noproxy
	 * @return boolean
	 * @throws IOException
	 */
	public boolean binarySaveFromURL(String proxyhost, int proxyport, String uName, String uPass, String[] noproxy) throws IOException {
		final int defaultProxyPort = 3128; // the port by default
		if (
				(this.from_filename.isEmpty())
				|| (this.to_filename.isEmpty())
				|| (this.from_filename.equals(this.to_filename))
				) {
			return this.bad;
		}
		File myFile = new File(this.to_filename);
		if (myFile.exists()) {
			if (!(myFile.canWrite())) { // checking whether the file is writable
				return this.bad;
			}
		}
		final int BUFFERSIZE = 32767; // the size of a data buffer
		InputStream from_file = null;
		FileOutputStream to_file = null;
		HttpURLConnection ucObj = null;
		try {
			if ((proxyhost.isEmpty()) || (new NoProxyClass(noproxy)._isTrue(this.from_filename))) {
				from_file = new URL(this.from_filename).openStream();
			} else {
				ucObj = (HttpURLConnection) new URL(this.from_filename).openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyhost.toString(), ((proxyport < 1) ? defaultProxyPort : proxyport))));
				String pa = new String(this.proxyAuthorizationThing(uName, uPass)); // creating a proxy authorization line (in Base64)
				ucObj.setRequestProperty("Readme", this.readmeHeader);
				if (!(pa.isEmpty())) { // does the proxy server need authorization?
					ucObj.setRequestProperty("Proxy-Authorization", pa.toString());
				}
				ucObj.connect(); // establishing a connection
				from_file = ucObj.getInputStream();
			}
			to_file = new FileOutputStream(this.to_filename);
			byte[] buffer = new byte[BUFFERSIZE];
			int bytes_read;
			while ((bytes_read = from_file.read(buffer)) != -1) {
				to_file.write(buffer, 0, bytes_read);
			}
		}
		finally {
			if (from_file != null) {
				from_file.close();
			}
			if (to_file != null) {
				to_file.close();
			}
			if (!(proxyhost.isEmpty())) { // thru a proxy server?
				if (ucObj != null) {
					ucObj.disconnect(); // establishing a disconnection
				}
			}
		}
		return this.good; // all has gone good
	}

	/**
	 * This function recursively finds files and directories by a pathname.
	 * @param path
	 * @param mask
	 * @param cntr
	 * @return String[]
	 * @throws IOException
	 */
	public String[] findFiles(String path, String mask, byte cntr) throws IOException {
		try {
			final int LIM = 4096; // limit to avoid the probable java.lang.OutOfMemoryError exception
			final byte nestingLIM = (byte) 5; // 1...127
			if (cntr < (byte) 1) {
				cntr = (byte) 1;
			}
			if (cntr >= nestingLIM) { // the nesting limit is exceeded.
				return new String[] {};
			}
			String[] stringArray = new String[] {};
			ArrayList<String> lst_obj = new ArrayList<String>();
			File[] myFiles = null;
			myFiles = this.fileList(path, null); // getting a files' list
			if (myFiles == null) {
				throw new IOException("Incorrect input data format."); // incorrect IP range
			}
			for (File sTemp : myFiles) {
				lst_obj.add(sTemp.getAbsolutePath());
				if ((sTemp.isDirectory()) && (sTemp.canRead())) {
					String[] addFiles = this.findFiles(sTemp.getAbsolutePath(), null, (byte) (cntr + (byte) 1));
					for (String sTemp2 : addFiles) {
						lst_obj.add(sTemp2);
						if (lst_obj.size() >= LIM) {
							break;
						}
					}
				}
				if (lst_obj.size() >= LIM) {
					break;
				}
			}
			stringArray = lst_obj.toArray(new String[lst_obj.size()]); // dumping into a normal array
			if (mask != null) {
				int c = 0;
				for (String arrTemp : stringArray) {
					if (new File(arrTemp).getName().toLowerCase().indexOf(mask.toLowerCase()) > 0) {
						c++;
					}
				}
				if (c > 0) {
					String[] temp01 = new String[c];
					c = -1;
					for (String arrTemp : stringArray) {
						if (new File(arrTemp).getName().toLowerCase().indexOf(mask.toLowerCase()) > 0) {
							temp01[++c] = arrTemp;
						}
					}
					stringArray = temp01;
				}
			}
			return stringArray;
		}
		catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * This method implements a Java full-features FTP client.
	 * It is based on the ftp4j library which is available at http://www.sauronsoftware.it/projects/ftp4j/
	 * @param defaultDir
	 * @param sysHome
	 * @param fileSeparator
	 */
	public void ftp(String defaultDir, String sysHome, String fileSeparator) {
		boolean _isExit = bad, _isPassive = good, _isMLSD = bad;
		final String notConn = new String("You are not connected.");
		final String undef = new String("The path is undefined.");
		final String undefFile = new String("The filename is unspecified.");
		final String unknownCmd = new String("\nUnknown command.");
		final int defaultPort = 21; // an FTP port by default
		final String anonymous = new String("anonymous"); // the Anonymous user
		final String prompt = new String("\nftp> ");
		final String[] cmdList = new String[] {
			"open", "Connecting to a remote FTP service.",
			"connect", "Synonymic to \"open\".",
			"passive", "Switching to work in passive data transfer mode.\nIn passive mode, the four indispensable conditions must be satisfied as follows:\n1. The client asks to the server to prepare a passive data transfer.\n2. The server replies with its IP address and a port number.\n3. The client asks the transfer and connects.\n4. The data transfer starts in the new established channel.\nIn passive mode, the client connects the server: no incoming connection is required.\nFor further information, please consult at http://www.sauronsoftware.it/projects/ftp4j/manual.php#15",
			"active", "Switching to work in active data transfer mode.\nIn active mode, the four indispensable conditions must be satisfied as follows:\n1. The client sends to the server its IP address and a port number.\n2. The client asks to the server a data transfer, and it starts listening the port sent before.\n3. The server connects the address and the port supplied by the client.\n4. The data transfer starts in the new established channel.\nThe active mode requires that your client could receive incoming connections from the server. If your client is behind a firewall, a proxy, a gateway or a mix of them, most of the time that is a problem, since it cannot receive incoming connections from outside.\nFor further information, please consult at http://www.sauronsoftware.it/projects/ftp4j/manual.php#15",
			"binary", "This sets the binary data transfer type.",
			"textual", "This sets the textual data transfer type.",
			"close", "This command invokes termination of an FTP session.",
			"disconnect", "Synonymic to \"close\".",
			"mkdir", "The command provokes creating a new directory on an FTP server.",
			"md", "Synonymic to \"mkdir\".",
			"rmdir", "The command provokes erasing a directory on an FTP server.",
			"rd", "Synonymic to \"rmdir\".",
			"chdir", "The command provokes changing a working directory on an FTP server.",
			"cd", "Synonymic to \"chdir\".",
			"ls", "Lists contents of the remote directory.",
			"dir", "Synonymic to \"ls\".",
			"rm", "The command provokes erasing a file on an FTP server.",
			"del", "Synonymic to \"rm\".",
			"erase", "Synonymic to \"rm\".",
			"delete", "Synonymic to \"rm\".",
			"get", "This command downloads a file from an FTP server.",
			"put", "This command uploads a file to an FTP server.",
			"mlsd", "This makes an FTP server use a standardized format, producing via the MLSD command (RFC 3659), while it is listing a directory.\nGaining detailed information about the MLSD command is obtainable at http://tools.ietf.org/html/rfc3659.html#page-23\nMLSD responses, in fact, are standard, accurated and more easily parsable. Unfortunately, not all of the servers support this command, and some of them support it badly.",
			"list", "This makes an FTP server use the outdated LIST command (RFC 0959) when listing a directory.\nGaining detailed information about the LIST command is obtainable at http://www.ietf.org/rfc/rfc959",
			"help", "Printing local help information.",
			"?", "Synonymic to \"help\".",
			"exit", "Quitting this FTP client.",
			"quit", "Synonymic to \"exit\".",
			"by", "Synonymic to \"exit\".",
			"bye", "Synonymic to \"exit\"."
		};

		final FTPClient objFTP = new FTPClient();
		final Scanner sc = new Scanner(System.in);

		String temp = null, temp2 = null;
		int x;

		System.out.println("\nWelcome to the light-weight built-in FTP client!");
		System.out.println("Type 'exit' to quit, 'help' to see the list of commands.");

		while (!_isExit) {
			System.out.print(prompt);
			switch (sc.nextLine().toLowerCase()) {
			case "exit": case "quit": case "by": case "bye":
				_isExit = good;
				break;
			case "open": case "connect":
				String[] ftpParams = new String[4]; // host, port, login, password
				System.out.print("\nHost: "); ftpParams[0] = sc.nextLine();
				System.out.print("\nPort: "); ftpParams[1] = sc.nextLine();
				System.out.print("\nLogin: "); ftpParams[2] = sc.nextLine();
				System.out.print("\nPassword: "); ftpParams[3] = sc.nextLine();
				System.out.println();
				try {
					if ((ftpParams[1] == null) || (ftpParams[1].isEmpty()) || (Integer.parseInt(ftpParams[1]) < 1) || (Integer.parseInt(ftpParams[1]) > 65535)) {
						ftpParams[1] = String.valueOf(defaultPort);
					}
				}
				catch (Exception e) {
					ftpParams[1] = String.valueOf(defaultPort);
				}
				if ((ftpParams[0] == null) || (ftpParams[0].isEmpty())) {
					System.err.println("\nThe hostname is undefined.");
					break;
				}
				if ((ftpParams[2] == null) || (ftpParams[2].isEmpty())) {
					ftpParams[2] = anonymous;
				}
				if (ftpParams[3] == null) {
					ftpParams[3] = "";
				}
				if (objFTP.isConnected()) {
					System.out.println("You are already connected.");
				} else {
					System.out.println("Establishing connection to the FTP server...");
					String[] welcome = null;
					try {
						welcome = objFTP.connect(ftpParams[0], Integer.parseInt(ftpParams[1]));
					}
					catch (Exception e) {
						System.err.println(e.toString());
					}
					if (welcome == null) {
						System.out.println("Something has gone wrong. " + notConn);
					} else {
						System.out.println("You are connected.");
						for (String sTemp01 : welcome) { // printing server's welcoming messages
							System.out.println(sTemp01);
						}
						objFTP.setPassive(_isPassive);
						objFTP.setType(FTPClient.TYPE_AUTO);
						objFTP.setMLSDPolicy((_isMLSD) ? FTPClient.MLSD_ALWAYS : FTPClient.MLSD_NEVER);
						System.out.print("Getting authenticated... ");
						try {
							objFTP.login(ftpParams[2], ftpParams[3]);
						}
						catch (Exception e) {
							System.err.println("\n" + e.toString());
						}
						if (objFTP.isAuthenticated()) {
							System.out.println("Done.");
						} else {
							System.out.println("Failed.");
						}
					}
				}
				break;
			case "passive":
				if (objFTP.isConnected()) {
					_isPassive = good;
					objFTP.setPassive(_isPassive);
					System.out.println("The passive mode is set.");
				} else {
					System.err.println(notConn);
				}
				break;
			case "active":
				if (objFTP.isConnected()) {
					_isPassive = bad;
					objFTP.setPassive(_isPassive);
					System.out.println("The active mode is set.");
				} else {
					System.err.println(notConn);
				}
				break;
			case "binary":
				if (objFTP.isConnected()) {
					objFTP.setType(FTPClient.TYPE_BINARY);
					System.out.println("The binary data transfer type is set.");
				} else {
					System.err.println(notConn);
				}
				break;
			case "textual":
				if (objFTP.isConnected()) {
					objFTP.setType(FTPClient.TYPE_TEXTUAL);
					System.out.println("The textual data transfer type is set.");
				} else {
					System.err.println(notConn);
				}
				break;
			case "close": case "disconnect":
				if (objFTP.isConnected()) {
					try {
						objFTP.disconnect(good);
						System.out.println("You are disconnected.");
					}
					catch (Exception e) {
						System.err.println(e.toString());
					}
				} else {
					System.out.println("You are not connected.");
				}
				break;
			case "mkdir": case "md":
				System.out.print("\nRemote path: "); temp = sc.nextLine(); System.out.println();
				if ((temp != null) && (!temp.isEmpty())) {
					try {
						objFTP.createDirectory(temp);
						System.out.print("The remote directory has been created.");
					}
					catch (Exception e) {}
				} else {
					System.err.println(undef);
				}
				break;
			case "rmdir": case "rd":
				System.out.print("\nRemote path: "); temp = sc.nextLine(); System.out.println();
				if ((temp != null) && (!temp.isEmpty())) {
					try {
						objFTP.deleteDirectory(temp);
						System.out.print("The remote directory has been erased.");
					}
					catch (Exception e) {}
				} else {
					System.err.println(undef);
				}
				break;
			case "chdir": case "cd":
				System.out.print("\nRemote path: "); temp = sc.nextLine(); System.out.println();
				if ((temp != null) && (!temp.isEmpty())) {
					try {
						objFTP.changeDirectory(temp);
						System.out.print("The remote working directory has been changed.");
					}
					catch (Exception e) {}
				} else {
					System.err.println(undef);
				}
				break;
			case "ls": case "dir":
				System.out.print("\nRemote path: "); temp = sc.nextLine(); System.out.println();
				if ((temp != null) && (!temp.isEmpty())) {
					try {
						for (FTPFile sTemp02 : objFTP.list(temp)) {
							System.out.format("%s\t%d bytes\t%s%n", sTemp02.getName(), sTemp02.getSize(), (sTemp02.getType() == FTPFile.TYPE_FILE) ? "<FILE>" : "<DIR>"     );
						}
					}
					catch (Exception e) {}
				} else {
					System.err.println(undef);
				}
				break;
			case "rm": case "del": case "erase": case "delete":
				System.out.print("\nRemote file: "); temp = sc.nextLine(); System.out.println();
				if ((temp != null) && (!temp.isEmpty())) {
					try {
						objFTP.deleteFile(temp);
						System.out.print("The remote file has been erased.");
					}
					catch (Exception e) {}
				} else {
					System.err.println(undefFile);
				}
				break;
			case "get":
				System.out.print("\nRemote file: "); temp = sc.nextLine(); System.out.println();
				System.out.print("\nFilename for local saving to: "); temp2 = sc.nextLine(); System.out.println();
				if (((temp != null) && (!temp.isEmpty())) && ((temp2 != null) && (!temp2.isEmpty()))) {
					temp2 = this.pathConverted(temp2, defaultDir, sysHome, fileSeparator);
					System.out.println("The file of \"" + temp + "\" is going to be downloaded and saved as \"" + temp2 + "\".");
					try {
						objFTP.download(temp, new File(temp2));
						System.out.print("Done. Check the local file of \"" + temp2 + "\".");
					}
					catch (Exception e) {}
				} else {
					System.err.println(undefFile);
				}
				break;
			case "put":
				System.out.print("\nLocal file: "); temp = sc.nextLine(); System.out.println();
				if ((temp != null) && (!temp.isEmpty())) {
					temp = this.pathConverted(temp, defaultDir, sysHome, fileSeparator);
					System.out.println("The file of \"" + temp + "\" is going to be uploaded.");
					try {
						objFTP.upload(new File(temp));
						System.out.print("Now that it has been done, check the file's existence on the FTP server.");
					}
					catch (Exception e) {}
				} else {
					System.err.println(undefFile);
				}
				break;
			case "mlsd":
				objFTP.setMLSDPolicy(FTPClient.MLSD_ALWAYS);
				_isMLSD = good;
				break;
			case "list":
				objFTP.setMLSDPolicy(FTPClient.MLSD_NEVER);
				_isMLSD = bad;
				break;
			case "help": case "?":
				String[] cmdListTemp = new String[cmdList.length / 2];
				for (x = 0; x < cmdList.length / 2; x++) {
					cmdListTemp[x] = cmdList[2 * x];
				}
				System.out.println(Arrays.toString(cmdListTemp));
				temp = "?";
				System.out.println("You are working in help mode now.");
				System.out.println("To come back, type a null string.");
				while ((temp != null) && (!temp.isEmpty())) {
					System.out.print("\nCommand> "); temp = sc.nextLine(); System.out.println();
					if ((temp != null) && (!temp.isEmpty())) {
						boolean found = bad;
						for (x = 0; x < cmdList.length / 2; x++) {
							if (cmdList[2 * x].toLowerCase().equals(temp.trim().toLowerCase())) {
								System.out.format("%s\t\t%s%n", cmdList[2 * x], cmdList[2 * x + 1]);
								found = good;
								break;
							}
						}
						if (!found) {
							System.err.println(unknownCmd);
						}
					}
				}
				System.out.println("Leaving the help mode.");
				break;
			default:
				System.err.println(unknownCmd);
				break;
			}
			System.out.println();
		}
		System.out.println("Quitting the FTP client.");
		if (objFTP.isConnected()) {
			try {
				objFTP.disconnect(good);
			}
			catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		System.out.println("Press <Enter> key to continue...");
		if (!sc.hasNextLine()) {
			sc.close();
		}
	}

	/**
	 * This String function returns an absolute pathname.
	 * @param path
	 * @param defaultDir
	 * @param sysHome
	 * @param fileSeparator
	 * @return String
	 */
	public String pathConverted(String path, String defaultDir, String sysHome, String fileSeparator) {
		if ((path == null) || (path.isEmpty())) {
			path = System.getProperty(fileSeparator);
		}
		if ((path.equals("..")) || (path.equals(".")) || (path.equals("~"))) {
			path += System.getProperty(fileSeparator);
		}
		while (path.lastIndexOf(System.getProperty(fileSeparator) + System.getProperty(fileSeparator)) != -1) {
			path = path.replaceAll(System.getProperty(fileSeparator) + System.getProperty(fileSeparator), System.getProperty(fileSeparator));
		}
		if (path.substring(path.length() - 1).equals("/")) {
			path = path.substring(0, path.length() - 1); 
		}
		String[] p = path.split(System.getProperty(fileSeparator));
		String finalString = new String();
		if (p[0].equals(".")) {
			p[0] = (System.getProperty(sysHome).equals(System.getProperty(fileSeparator))) ? "" : System.getProperty(sysHome);
		}
		if ((p[0].equals("~")) && (System.getProperty("os.name").toLowerCase().equals("linux"))) { // are we running this under Linux?
			p[0] = (defaultDir.equals(System.getProperty(fileSeparator))) ? "" : defaultDir;
		}
		if (p[0].equals("..")) {
			String[] p1 = System.getProperty(sysHome).split(System.getProperty(fileSeparator));
			String homeDir = new String();
			if (p1.length > 1) {
				for (int x = 0; x < p1.length - 1; x++) {
					homeDir += p1[x] + System.getProperty(fileSeparator);
				}
				homeDir = homeDir.substring(0, homeDir.length() - 1);
			}
			p[0] = homeDir;
		}
		for (String s : p) {
			finalString += s + System.getProperty(fileSeparator);
		}
		finalString = finalString.substring(0, finalString.length() - 1);
		return ((finalString.isEmpty()) ? System.getProperty(fileSeparator) : finalString);
	}

}