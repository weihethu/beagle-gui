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

public class BeagleInvoker {
	private static String beagle_executable_path = null;
	private static String tmpPath = null;
	private static BeagleInvoker invoker_instance = null;

	private BeagleInvoker() {
		boolean auto_detect = Config.getInstance().get_auto_detect_os();
		OS_TYPE os_type = OS_TYPE.others;
		OS_BIT_VERSION os_bit = OS_BIT_VERSION.bit_32;

		if (auto_detect) {
			String os_name = System.getProperty("os.name").toLowerCase();
			if (os_name.contains("windows")) {
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
	}

	public static BeagleInvoker getIntance() {
		if (invoker_instance == null)
			invoker_instance = new BeagleInvoker();
		return invoker_instance;
	}

	public Pair<Integer, String> elts2XML(String filePath) {
		if (beagle_executable_path == null)
			return new Pair<Integer, String>(1, "No beagle executable!");
		try {
			ProcessBuilder pb = new ProcessBuilder(beagle_executable_path,
					"-2xml", filePath);
			pb.redirectErrorStream(false);

			Process process = pb.start();
			InputStream input = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String s = null, content = "";
			while ((s = br.readLine()) != null) {
				content += (s + "\n");
			}
			int exitValue = process.waitFor();
			return new Pair<Integer, String>(exitValue, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Pair<Integer, String>(1, null);
	}

	public void verify(final VerifierPane verifierPane) {

		if (beagle_executable_path == null) {
			JOptionPane
					.showMessageDialog(verifierPane, "No beagle executable!",
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

		String[] algorithmArgs = verifierPane.getArguments();
		final String[] args = new String[algorithmArgs.length + 2];
		args[0] = beagle_executable_path;
		for (int i = 0; i < algorithmArgs.length; i++)
			args[i + 1] = algorithmArgs[i];
		args[args.length - 1] = tmpFile.getPath();

		try {
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.redirectErrorStream(true);
			final Process process = pb.start();
			verifierPane.setProcessOutputStream(process.getOutputStream());

			Thread outputThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						InputStream input = process.getInputStream();
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
						int exitValue = process.waitFor();
						onVerifyEnd(verifierPane, exitValue);
					} catch (InterruptedException e) {
						e.printStackTrace();
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

	private void onVerifyEnd(VerifierPane pane, int exitValue) {
		pane.setProcessOutputStream(null);
		JOptionPane.showMessageDialog(pane, "Beagle process exited with value "
				+ exitValue + "!", "Info", JOptionPane.INFORMATION_MESSAGE);
	}
}
