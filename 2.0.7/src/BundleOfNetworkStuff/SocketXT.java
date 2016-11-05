package BundleOfNetworkStuff;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.lang.IllegalArgumentException;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
// import java.util.Base64;
import java.io.OutputStream;
import java.io.InputStream;

/**
 * Extends the Socket class by supporting a proxy server with authorization.
 * Use the connect() function to return a Socket instance.
 * When running this function, a connection is established by default.
 * So you must run the close() method at the end of the work with this class to drop the connection.
 * @author Nikodim
 * @param proxy
 * @param _needAuth
 * @param uName
 * @param uPass
 * @param noproxy
 * @param sa
 * @throws IOException
 * @throws SocketTimeoutException
 * @throws IllegalBlockingModeException
 * @throws IllegalArgumentException
 */
public class SocketXT extends Socket {
	private final boolean good = true; // successful
	private final boolean bad = false; // unsuccessful

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
	}

	private Proxy thisProxy;
	private boolean thisNeedAuth;
	private String thisUName, thisUPass;
	private String[] thisNoproxy;
	private SocketAddress thisSA;

	private Socket inoutSocket = null;
	public OutputStream out = null;
	public InputStream in = null;

	public SocketXT(Proxy proxy, boolean _needAuth, String uName, String uPass, String[] noproxy, SocketAddress sa) throws IOException, SocketTimeoutException, IllegalBlockingModeException, IllegalArgumentException {
		this.thisProxy = proxy;
		this.thisNeedAuth = _needAuth;
		this.thisUName = uName;
		this.thisUPass = uPass;
		this.thisNoproxy = noproxy;
		this.thisSA = sa;
	}

	public final void connect() throws IOException {
		final int TIMEOUT = 10000;
		StringBuilder objSB = new StringBuilder();

		final String[] httpHeaders = new String[] {
				"User-Agent: SocketXT/1.0.0 (Java 1.7+)",
				"E-Mail: nspu@list.ru",
				"Connection: Keep-Alive",
				"Proxy-Connection: keep-alive"
		};
		final String br = "\n", endOfHeaders = "\n\r";

		objSB = objSB.append(this.thisUName).append(":").append(this.thisUPass);
		// String enc = Base64.getEncoder().encodeToString(objSB.toString().getBytes());
		String enc = javax.xml.bind.DatatypeConverter.printBase64Binary(objSB.toString().getBytes()); // http://docs.oracle.com/javase/6/docs/api/javax/xml/bind/DatatypeConverter.html#printBase64Binary(byte[])
		objSB = objSB.delete(0, objSB.length()).append("Proxy-Authorization: Basic ").append(enc);

		String node = this.thisSA.toString();
		String nodeHost = ((node.split(":")[0].split("/")[0].isEmpty()) ? node.split(":")[0].split("/")[1] : node.split(":")[0].split("/")[0]);
		int nodePort = Integer.parseInt(node.split(":")[1]);

		if (new NoProxyClass(this.thisNoproxy)._isTrue("http://" + nodeHost)) {
			// without proxy
			this.inoutSocket = new Socket();
			this.inoutSocket.connect(this.thisSA, TIMEOUT);
			this.out = this.inoutSocket.getOutputStream();
			this.in = this.inoutSocket.getInputStream();
		} else {
			// with proxy
			if (this.thisProxy.type().equals(Proxy.Type.HTTP)) {
				this.inoutSocket = new Socket();
				this.inoutSocket.connect(this.thisProxy.address(), TIMEOUT);
				this.out = this.inoutSocket.getOutputStream();
				this.in = this.inoutSocket.getInputStream();
				this.out.write(
						new StringBuilder()
						// CONNECT server.com:80 HTTP/1.1
						.append("CONNECT ")
						.append(nodeHost)
						.append(":")
						.append(String.valueOf(nodePort))
						.append(" HTTP/1.1")
						.append("\n")
						.toString().getBytes()
						);
				for (String arrTemp01 : httpHeaders) { // the headers
					this.out.write(
							new StringBuilder()
							.append(arrTemp01)
							.append(br)
							.toString().getBytes()
							);
				}
				this.out.write( // a node to be required
						new StringBuilder()
						.append("Host: ")
						.append(nodeHost)
						.append(":")
						.append(String.valueOf(nodePort))
						.append(br)
						.toString().getBytes()
						);
				if (this.thisNeedAuth) {
					this.out.write( // the proxy server's login & password (in Base64)
							new StringBuilder()
							.append(objSB.toString())
							.append(br)
							.toString().getBytes()
							);
				}
				this.out.write(endOfHeaders.getBytes());
				this.out.flush();
				try {
					Thread.sleep(TIMEOUT); // pause
				}
				catch (InterruptedException e) {}
			} else {
				this.inoutSocket = new Socket(this.thisProxy);
				this.inoutSocket.connect(this.thisSA, TIMEOUT);
				this.out = this.inoutSocket.getOutputStream();
				this.in = this.inoutSocket.getInputStream();
			}
		}
	}

	@Override
	public final void close() throws IOException {
		if (this.out != null) {
			this.out.flush();
			this.out.close();
		}
		if (this.in != null) {
			this.in.close();
		}
		if (this.inoutSocket != null) {
			this.inoutSocket.close();
		}
	}

}