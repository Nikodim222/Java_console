package Databases;

/**
 * This class contains a set of functions to work with a MySQL database.
 * @author Nikodim
 * 
 * http://sqlinfo.ru/articles/info/13.html
 * http://dev.mysql.com/downloads/connector/j/
 * java -cp /usr/share/java/mysql-connector-java.jar:. Dump
 */
public class MySQL_DB extends Common_DB {

	public MySQL_DB(String user, String password, String url, String controlString, String codepage) {
		super(user, password, url, controlString); // mysql-connector-java.jar
		this.props.put("characterEncoding", codepage);
	}

}