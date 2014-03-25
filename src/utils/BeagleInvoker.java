package utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BeagleInvoker {
	private static String beagle_runnable_path = "beagle/linux_x86/beagle";

	private static BeagleInvoker invoker_instance = null;

	private BeagleInvoker() {

	}

	public static BeagleInvoker getIntance() {
		if (invoker_instance == null)
			invoker_instance = new BeagleInvoker();
		return invoker_instance;
	}

	public Pair<Integer, String> elts2XML(String filePath) {
		try {
			Process process = new ProcessBuilder(beagle_runnable_path, "-2xml",
					filePath).start();
			int exitValue = process.waitFor();
				InputStream input = process.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						input));
				String s = null, content = "";
				while ((s = br.readLine()) != null) {
					content += (s + "\n");
				}
				return new Pair<Integer, String>(exitValue, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Pair<Integer, String>(1, null);
	}
}
