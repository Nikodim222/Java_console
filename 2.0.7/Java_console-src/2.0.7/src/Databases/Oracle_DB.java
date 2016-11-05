package Databases;

/**
 * This class contains a set of functions to work with an Oracle database.
 * @author Nikodim
 * 
 * http://otvet.mail.ru/question/25894586
 * http://docs.oracle.com/javase/tutorial/jdbc/overview/index.html
 * https://netbeans.org/kb/docs/ide/oracle-db_ru.html
 */
public class Oracle_DB extends Common_DB {

	public Oracle_DB(String user, String password, String url, String controlString) {
		super(user, password, url, controlString); // ojdbc6.jar
	}
}