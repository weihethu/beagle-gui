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

public class BeagleInvoker {
	private static String beagle_executable_path = null;
	private static String tmpPath = null;
	private static BeagleInvoker invoker_instance = null;

	private BeagleInvoker() {
		// set beagle_executable_path according to os type & bit version
		// if no executable, or not supported, pop warning box
		beagle_executable_path = "beagle/linux_x86/beagle";
		tmpPath = "tmp";
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
			Process process = new ProcessBuilder(beagle_executable_path,
					"-2xml", filePath).start();
			int exitValue = process.waitFor();
			InputStream input = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
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

	public void verify(final VerifierPane verifierPane) {

		if (beagle_executable_path == null) {
			JOptionPane
					.showMessageDialog(verifierPane,
							"No beagle executable!", "Error",
							JOptionPane.ERROR_MESSAGE);
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

		final File tmpFile = new File(tmpPath, "tmp.elt");
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
			final Process process = new ProcessBuilder(args).start();
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
						int exitValue = process.waitFor();
						onVerifyEnd(verifierPane, exitValue);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			});
			outputThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void onVerifyEnd(VerifierPane pane, int exitValue) {
		pane.setProcessOutputStream(null);
		JOptionPane.showMessageDialog(pane,
				"Beagle process exited with value " + exitValue + "!", "Info",
				JOptionPane.INFORMATION_MESSAGE);
	}
}
