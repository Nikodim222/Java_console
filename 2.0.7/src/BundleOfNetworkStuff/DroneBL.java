package BundleOfNetworkStuff;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Checks an IP address in a DroneBL database.
 * http://www.dronebl.org/docs/howtouse
 * @param host
 */

public class DroneBL {
	final private String DNSBL = "dnsbl.dronebl.org";
	String myHost;

	public DroneBL(String host) {
		this.myHost = host;
	}

	final public boolean _isBlacklisted() {
		String[] s = this.myHost.split("\\.");
		StringBuilder hostObj = new StringBuilder();
		for (int x = s.length - 1; x >= 0; x--) {
			hostObj.append(s[x]); hostObj.append(".");
		}
		hostObj.append(DNSBL);
		try {
			if (InetAddress.getAllByName(hostObj.toString()).length > 0) {
				return true;
			}
			return false;
		}
		catch (UnknownHostException e) {
			return false;
		}
	}

}