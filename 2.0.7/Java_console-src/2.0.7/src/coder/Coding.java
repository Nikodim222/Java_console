package coder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import javax.xml.bind.DatatypeConverter;

import files.BasicOperations; // linking the handy BasicOperations class

/**
 * This class contains functions for performing decoding and encoding.
 * @author Nikodim
 *
 */
public class Coding { // http://en.wikipedia.org/wiki/MIME#Content-Transfer-Encoding

	/**
	 * This function receives a byte[] array in Quoted-Printable as an input and decodes it converting
	 * one codepage into another one at the same time.
	 * @param encodedMsg
	 * @param from_codepage
	 * @param to_codepage
	 * @return byte[]
	 * @throws UnsupportedEncodingException
	 */
	public byte[] decodeQP(byte[] encodedMsg, String from_codepage, String to_codepage) throws UnsupportedEncodingException { // http://en.wikipedia.org/wiki/Quoted-printable
		ArrayList<Byte> lst_obj = new ArrayList<Byte>();
		int x;
		String reencoded = new String(encodedMsg, from_codepage);
		for (x = 0; x < reencoded.length(); x++) {
			if (reencoded.charAt(x) != (char) '=') {
				String hexS = Integer.toHexString((int) (reencoded.charAt(x))).toUpperCase();
				if (hexS.length() < 2) {
					hexS = "0" + hexS;
				}
				lst_obj.add((byte) ((char) '='));
				lst_obj.add((byte) hexS.charAt(0));
				lst_obj.add((byte) hexS.charAt(1));
			} else {
				if ((x + 1 < reencoded.length()) && (x + 2 < reencoded.length())) {
					lst_obj.add((byte) reencoded.charAt(x));
					lst_obj.add((byte) reencoded.charAt(x + 1));
					lst_obj.add((byte) reencoded.charAt(x + 2));
					x += 2;
				}
			}
		}
		byte[] encodedMsg2 = new byte[lst_obj.size()];
		for (x = 0; x < encodedMsg2.length; x++) {
			encodedMsg2[x] = lst_obj.get(x);
		}
		lst_obj.clear();
		if (encodedMsg2.length % 3 != 0) { // is it divisible by 3?
			return new byte[] {}; // Error!
		}
		for (x = 0; x < encodedMsg2.length / 3; x++) {
			if (
					((char) encodedMsg2[3 * x] != (char) '=')
					|| (!((((char) encodedMsg2[3 * x + 1] >= (char) '0') && ((char) encodedMsg2[3 * x + 1] <= (char) '9')) || (((char) encodedMsg2[3 * x + 1] >= (char) 'A') && ((char) encodedMsg2[3 * x + 1] <= (char) 'Z')) || (((char) encodedMsg2[3 * x + 1] >= (char) 'a') && ((char) encodedMsg2[3 * x + 1] <= (char) 'z'))))
					|| (!((((char) encodedMsg2[3 * x + 2] >= (char) '0') && ((char) encodedMsg2[3 * x + 2] <= (char) '9')) || (((char) encodedMsg2[3 * x + 2] >= (char) 'A') && ((char) encodedMsg2[3 * x + 2] <= (char) 'Z')) || (((char) encodedMsg2[3 * x + 2] >= (char) 'a') && ((char) encodedMsg2[3 * x + 2] <= (char) 'z'))))
					) {
				return new byte[] {}; // Error!
			}
		}
		byte[] predecoded = new byte[encodedMsg2.length / 3];
		for (x = 0; x < encodedMsg2.length / 3; x++) {
			predecoded[x] = (byte) Integer.parseInt(new String(new byte[] {encodedMsg2[3 * x + 1], encodedMsg2[3 * x + 2]}), 16);
		}
		return new String(predecoded, from_codepage).getBytes(to_codepage);
	}

	/**
	 * This function receives a byte[] array in Base64 as an input and decodes it converting
	 * one codepage into another one at the same time.
	 * @param encodedMsg
	 * @param from_codepage
	 * @param to_codepage
	 * @return byte[]
	 * @throws UnsupportedEncodingException
	 */
	public byte[] decodeBase64(byte[] encodedMsg, String from_codepage, String to_codepage) throws UnsupportedEncodingException { // https://docs.oracle.com/javase/7/docs/api/javax/xml/bind/DatatypeConverter.html#parseBase64Binary(java.lang.String)
		String reencoded = new String(encodedMsg, from_codepage);
		return new String(DatatypeConverter.parseBase64Binary(reencoded)).getBytes(to_codepage);
	}

	/**
	 * This parses a file in the MIM format.
	 * Be ready to catch an exception, it throws.
	 * @param from_file
	 * @param from_codepage
	 * @param to_codepage
	 * @return String[]
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public String[] parseMIM(String from_file, String from_codepage, String to_codepage) throws IOException, UnsupportedEncodingException {
		final String unsupp = new String("Unsupported data format.");
		boolean ok = false;
		int x, x2;
		String[] arrTemp = null;
		String[] stringArray = null;
		String[] buffer = new BasicOperations(from_file, "").readTextFile(from_codepage);
		for (x = 0; x < buffer.length; x++) {
			arrTemp = buffer[x].split(":");
			if ((arrTemp.length == 2) && (arrTemp[0].toLowerCase().trim().equals("Content-Transfer-Encoding".toLowerCase()))) {
				ok = true;
				break;
			}
		}
		if (ok) {
			// (arrTemp[1].toLowerCase().trim().equals("quoted-printable".toLowerCase()))
			stringArray = new String[buffer.length];
			switch (arrTemp[1].toLowerCase().trim()) {
			case "quoted-printable":
				for (x = 0; x < stringArray.length; x++) {
					stringArray[x] = new String(this.decodeQP(buffer[x].getBytes(), from_codepage, to_codepage), to_codepage);
				}
				break;
			case "base64":
				for (x2 = 0; x2 <= x; x2++) {
					stringArray[x2] = buffer[x2];
				}
				for (x2 = x + 1; x2 < stringArray.length; x2++) {
					stringArray[x2] = new String(this.decodeBase64(buffer[x2].getBytes(), from_codepage, to_codepage), to_codepage);
					if ((buffer[x2].length() >= 2) && (buffer[x2].substring(buffer[x2].length() - 2, buffer[x2].length()).equals("=="))) {
						break;
					}
				}
				for (x = x2 + 1; x < stringArray.length; x++) {
					stringArray[x] = buffer[x];
				}
				break;
			default:
				throw new IOException(unsupp);
			}
			return stringArray;
		}
		throw new IOException(unsupp);
	}

}