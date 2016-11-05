package BundleOfNetworkStuff;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.net.SocketException;

import BundleOfNetworkStuff.SocketXT;


/**
 * This class contains a set of functions for working with the WHOIS protocol.
 * @author Nikodim
 * @param socket
 * @param codepage
 * @param ip
 *
 */
public final class WhoIs {
	private SocketXT thisSocket;
	private String thisCodepage, thisIP;
	private BufferedReader in = null;
	private PrintWriter out = null;

	public WhoIs(SocketXT socket, String codepage, String ip) {
		this.thisSocket = socket;
		this.thisCodepage = codepage;
		this.thisIP = ip;
	}

	/**
	 * This thing returns a response from a WHOIS server.
	 * @return String[]
	 * @throws IOException
	 * @throws SocketException
	 */
	public String[] whoisResponse() throws IOException, SocketException {
		final int TIMEOUT = 10000;
		String[] stringArray = new String[] {};
		ArrayList<String> lst_obj = new ArrayList<String>();

		this.thisSocket.connect();
		this.in = new BufferedReader(new InputStreamReader(this.thisSocket.in, this.thisCodepage));
		this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.thisSocket.out, this.thisCodepage)), true);

		this.out.println(this.thisIP);
		this.out.println(); // end of the commands
		this.out.flush();
		try {
			Thread.sleep(TIMEOUT); // pause
		}
		catch (InterruptedException e) {}
		if (this.in.ready()) {
			String line01 = new String();
			while ((line01 = this.in.readLine()) != null) {
				lst_obj.add(line01.toString());
			}
		}
		stringArray = lst_obj.toArray(new String[lst_obj.size()]); // dumping into a normal array

		this.out.close();
		this.in.close();
		this.thisSocket.close();

		return stringArray;
	}

}