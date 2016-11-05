package files;

import java.util.Map;
import java.io.File;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.UnsupportedOperationException;
import java.lang.NullPointerException;

/**
 * This class contains a set of methods and functions to interact with the external environment.
 * @author Nikodim
 *
 */
public class External {

	private String[] getEnvs() {
		int x = -1;
		Map<String, String> env = System.getenv();
		String[] result = new String[2 * env.keySet().size()];
		for (String envName : env.keySet()) {
			result[++x] = envName;
			result[++x] = env.get(envName);
		}
		return result;
	}

	/**
	 * This method prints all the environment variables.
	 */
	public void printEnvs() {
		String[] env = this.getEnvs();
		for (int x = 0; x < env.length / 2; x++) {
			System.out.format("%s=%s%n", env[2 * x], env[2 * x + 1]);
		}
	}

	/**
	 * This String function returns a value by an environment variable's name.
	 * The search is case-insensitive.
	 * @param envVar
	 * @return String
	 */
	public String getEnv(String envVar) {
		String[] env = this.getEnvs();
		for (int x = 0; x < env.length / 2; x++) {
			if (env[2 * x].toUpperCase().equals(envVar.toUpperCase())) {
				return env[2 * x + 1];
			}
		}
		return new String();
	}

	/**
	 * This int function executes an external command or program under the operating system.
	 * Be ready to catch an exception, it throws. 
	 * @param args
	 * @param directory
	 * @param codepage
	 * @return Errorlevel
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws UnsupportedOperationException
	 * @throws NullPointerException
	 */
	public int execCommand(ArrayList<String> args, String directory, String codepage) throws IOException, InterruptedException, UnsupportedOperationException, NullPointerException { // http://blog.eqlbin.ru/2010/09/java.html
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		Map<String, String> env = pb.environment();
		env.clear(); // http://spec-zone.ru/RU/Java/Docs/8/api/java/util/Map.html#clear()
		String[] envsList = this.getEnvs();
		for (int x = 0; x < envsList.length / 2; x++) {
			env.put(envsList[2 * x], envsList[2 * x + 1]);
		}
		pb.directory(new File(directory)); // setting the working directory
		Process process = pb.start();
		BufferedReader brStdout = new BufferedReader(new InputStreamReader(process.getInputStream(), ((codepage.isEmpty()) ? "UTF-8" : codepage.toString())));
		String line = null;
		while ((line = brStdout.readLine()) != null) {
			System.out.println(line);
		}
		int errorlevel = process.waitFor(); // Errorlevel
		return errorlevel;
	}

}