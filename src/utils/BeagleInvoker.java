package utils;

import elts.ELTSGenerator;
import gui.Environment;
import gui.verifiers.VerifierPane;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

import utils.Config.OS_BIT_VERSION;
import utils.Config.OS_TYPE;

/**
 * the utility class that invokes Beagle
 * 
 * @author Wei He
 * 
 */
public class BeagleInvoker {
	/**
	 * beagle executable path
	 */
	private static String beagle_executable_path = null;
	/**
	 * path for storing temporary model files
	 */
	private static String tmpPath = null;
	/**
	 * the invoker instance
	 */
	private static BeagleInvoker invoker_instance = null;
	/**
	 * current running beagle process
	 */
	private Process currentProcess = null;

	/**
	 * constructor
	 */
	private BeagleInvoker() {
		boolean auto_detect = Config.getInstance().get_auto_detect_os();
		OS_TYPE os_type = OS_TYPE.others;
		OS_BIT_VERSION os_bit = OS_BIT_VERSION.bit_32;

		if (auto_detect) {
			// automatic detecting os types & bit versions
			String os_name = System.getProperty("os.name").toLowerCase();
			if (os_name.contains("windows")) {
				// to see why is that, see the stackoverflow thread:
				// http://stackoverflow.com/questions/4748673/how-can-i-check-the-bitness-of-my-os-using-java-j2se-not-os-arch
				os_type = OS_TYPE.windows;
				String arch = System.getenv("PROCESSOR_ARCHITECTURE");
				String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

				os_bit = arch.endsWith("64") || wow64Arch != null
						&& wow64Arch.endsWith("64") ? OS_BIT_VERSION.bit_64
						: OS_BIT_VERSION.bit_32;

			} else if (os_name.contains("linux")) {
				os_type = OS_TYPE.linux;

				String os_arch = System.getProperty("os.arch");
				os_bit = os_arch.contains("64") ? OS_BIT_VERSION.bit_64
						: OS_BIT_VERSION.bit_32;

			} else {
				os_type = OS_TYPE.others;
			}
		} else {
			os_type = Config.getInstance().get_os_type();
			os_bit = Config.getInstance().get_os_bit_version();
		}

		if (os_type == OS_TYPE.windows) {
			if (os_bit == OS_BIT_VERSION.bit_64) {
				beagle_executable_path = Config.getInstance()
						.get_win64_beagle_executable_path();
			} else {
				beagle_executable_path = Config.getInstance()
						.get_win32_beagle_executable_path();
			}
		} else if (os_type == OS_TYPE.linux) {
			if (os_bit == OS_BIT_VERSION.bit_64)
				beagle_executable_path = Config.getInstance()
						.get_linux64_beagle_executable_path();
			else
				beagle_executable_path = Config.getInstance()
						.get_linux32_beagle_executable_path();
		} else {
			os_type = OS_TYPE.others;
			JOptionPane.showMessageDialog(Environment.getInstance(),
					"Unsupported os version!", "Error",
					JOptionPane.ERROR_MESSAGE);
			beagle_executable_path = null;
		}
		if (beagle_executable_path != null) {
			File beagle_executable = new File(beagle_executable_path);
			if (!beagle_executable.exists() || !beagle_executable.isFile()) {
				beagle_executable_path = null;
			}
		}
		tmpPath = Config.getInstance().get_tmp_dir();
		this.currentProcess = null;
	}

	/**
	 * get beagleInvoker instance
	 * 
	 * @return
	 */
	public static BeagleInvoker getIntance() {
		if (invoker_instance == null)
			invoker_instance = new BeagleInvoker();
		return invoker_instance;
	}

	/**
	 * call beagle process to translate elts to xml
	 * 
	 * @param filePath
	 *            elts model file path
	 * @return translating result, the 1st is the exit status of beagle process,
	 *         and the 2nd is XML content or error message
	 */
	public Pair<Integer, String> elts2XML(String filePath) {
		if (beagle_executable_path == null)
			return new Pair<Integer, String>(1, "No beagle executable!");
		if (this.isProcessRunning())
			return new Pair<Integer, String>(1,
					"Beagle process is running! Please wait for it to exit!");
		try {
			ProcessBuilder pb = new ProcessBuilder(beagle_executable_path,
					"-2xml", filePath);
			pb.redirectErrorStream(false);

			currentProcess = pb.start();
			InputStream input = currentProcess.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String s = null, content = "";
			while ((s = br.readLine()) != null) {
				content += (s + "\n");
			}

			br.close();

			InputStream error = currentProcess.getErrorStream();
			br = new BufferedReader(new InputStreamReader(error));
			s = null;
			String errorContent = "";
			while ((s = br.readLine()) != null) {
				errorContent += (s + "\n");
			}
			br.close();

			int exitValue = currentProcess.waitFor();
			currentProcess = null;
			return new Pair<Integer, String>(exitValue,
					exitValue == 0 ? content : errorContent + content);
		} catch (Exception e) {
			e.printStackTrace();
			return new Pair<Integer, String>(1, e.getMessage());
		}
	}

	/**
	 * call beagle process to verify
	 * 
	 * @param verifierPane
	 *            verifier pane
	 */
	public void verify(final VerifierPane verifierPane) {

		if (beagle_executable_path == null) {
			JOptionPane
					.showMessageDialog(verifierPane, "No beagle executable!",
							"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (this.isProcessRunning()) {
			JOptionPane.showMessageDialog(verifierPane,
					"Beagle process is running! Please wait for it to exit",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// write the model text to a file in tmp path
		boolean createTmpFile = true;

		File tmpDir = new File(tmpPath);
		if (tmpDir.exists() && !tmpDir.isDirectory()) {
			if (!tmpDir.delete())
				createTmpFile = false;
		}
		if (!tmpDir.exists()) {
			if (!tmpDir.mkdirs())
				createTmpFile = false;
		}

		if (!createTmpFile) {
			JOptionPane.showMessageDialog(verifierPane,
					"Failed to create temporary directory " + tmpPath, "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		final File tmpFile = new File(tmpPath, "tmp"
				+ Config.getInstance().get_elts_ext());
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
			bw.write(ELTSGenerator.getModelText(Environment.getInstance()
					.getModel()));
			bw.close();
		} catch (Exception e) {
			createTmpFile = false;
			e.printStackTrace();
		}

		if (!createTmpFile) {
			JOptionPane.showMessageDialog(verifierPane,
					"Failed writing model txt to " + tmpFile.getPath(),
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// form arguments
		String[] algorithmArgs = verifierPane.getArguments();
		final String[] args = new String[algorithmArgs.length + 2];
		args[0] = beagle_executable_path;
		for (int i = 0; i < algorithmArgs.length; i++)
			args[i + 1] = algorithmArgs[i];
		args[args.length - 1] = tmpFile.getPath();

		try {
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.redirectErrorStream(true);
			currentProcess = pb.start();
			verifierPane.setProcessOutputStream(currentProcess
					.getOutputStream());

			// since verification may take some time, then monitoring its output
			// and wait for exit in another threads, making this function
			// non-blocking
			Thread outputThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						InputStream input = currentProcess.getInputStream();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(input));
						String s = null;
						while ((s = br.readLine()) != null) {
							verifierPane.appendLine(s);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			});

			Thread waitThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						int exitValue = currentProcess.waitFor();
						onVerifyEnd(verifierPane, exitValue);
					} catch (InterruptedException e) {
						e.printStackTrace();
						currentProcess = null;
						verifierPane.setProcessOutputStream(null);
						JOptionPane.showMessageDialog(verifierPane,
								"Beagle process didn't exit gracefully!",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}

			});
			outputThread.start();
			waitThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * terminate beagle process
	 */
	public void terminateProcess() {
		if (currentProcess != null)
			currentProcess.destroy();
	}

	/**
	 * called when verification end
	 * 
	 * @param pane
	 *            verifier pane
	 * @param exitValue
	 *            beagle process exit status
	 */
	private void onVerifyEnd(VerifierPane pane, int exitValue) {
		currentProcess = null;
		pane.setProcessOutputStream(null);
		JOptionPane.showMessageDialog(pane, "Beagle process exited with value "
				+ exitValue + "!", "Info", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * is beagle process running
	 * 
	 * @return a boolean indicator
	 */
	public boolean isProcessRunning() {
		return this.currentProcess != null;
	}
}
