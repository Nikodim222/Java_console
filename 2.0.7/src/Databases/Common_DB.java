package Databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * This class contains a set of functions to work with a database.
 * @author Nikodim
 * 
 * http://otvet.mail.ru/question/25894586
 * http://docs.oracle.com/javase/tutorial/jdbc/overview/index.html
 * https://netbeans.org/kb/docs/ide/oracle-db_ru.html
 */
public class Common_DB {

	protected Properties props = new Properties();
	protected String url, controlString;

	public Common_DB(String user, String password, String url, String controlString) {
		this.props.put("user", user); // DB's username
		this.props.put("password", password); // DB's password
		this.url = new String(url); // connection string
		this.controlString = new String(controlString); // it's for the Class.forName(this.controlString) thing
	}

	/**
	 * This closes the Oracle database connection.
	 * Be ready to catch an exception, it throws.
	 * @param con
	 * @throws SQLException
	 */
	public void disconnectFromDB(Connection con) throws SQLException {
		con.close();
	}

	/**
	 * This function establishes a connection to an Oracle database.
	 * Be ready to catch an exception, it throws.
	 * Example of an URL string: "jdbc:oracle:thin:@10.242.4.2:1525:REG1"
	 * @param url
	 * @param props
	 * @return Connection
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection establishConnection() throws ClassNotFoundException, SQLException {
		Class.forName(this.controlString);
		Connection con = DriverManager.getConnection(this.url, this.props);
		return con;
	}

	/**
	 * This function executes an SQL query. It does also manage opening and closing the connection.
	 * Be ready to catch an exception, it throws.
	 * @param url
	 * @param props
	 * @param query
	 * @return String[]
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public String[] executeQuery(String query) throws ClassNotFoundException, SQLException, SQLSyntaxErrorException, OutOfMemoryError {
		final int LIM = 4096; // limit to avoid the probable java.lang.OutOfMemoryError exception
		final String ERR01 = new String("The array cannot be null."), ERR02 = new String("Invalid data format found when processing input.");
		int cntr = 0, x, c;
		String[] stringArray = new String[] {};
		ArrayList<String> lst_obj = new ArrayList<String>();
		query = ((query.substring(query.length() - 1).equals(";")) ? query.substring(0, query.length() - 1) : query);
		Connection con = this.establishConnection(); // connect
		ResultSet rs = con.createStatement().executeQuery(query);
		c = rs.getMetaData().getColumnCount();
		lst_obj.add(String.valueOf(c));
		if (++cntr < LIM) {
			for (x = 1; x <= c; x++) {
				lst_obj.add(rs.getMetaData().getColumnName(x));
				if (++cntr >= LIM) {
					break;
				}
			}
			if (cntr < LIM) {
				while (rs.next()) {
					for (x = 1; x <= c; x++) {
						lst_obj.add(rs.getString(x));
						if (++cntr >= LIM) {
							break;
						}
					}
					if (cntr >= LIM) {
						break;
					}
				}
			}
		}
		try {
			con.commit(); // committing something if required
		}
		catch(Exception e) {}
		this.disconnectFromDB(con); // disconnect
		stringArray = lst_obj.toArray(new String[lst_obj.size()]); // dumping into a normal array
		for (x = 0; x < stringArray.length; x++) {
			if ((stringArray[x] == null) || (stringArray[x].isEmpty())) {
				stringArray[x] = "null";
			}
		}
		return (
				(
						(stringArray == null) // the array cannot be null
						|| ((stringArray.length - 1) % Integer.parseInt(stringArray[0]) != 0) // invalid data format found when processing input
						) ?
								(new String[] {"1", "ERROR", ((stringArray == null) ? ERR01 : ERR02)}) // something went wrong
								: stringArray // no errors found
				);
	}

}