package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * utility class for reading configurations
 * 
 * @author Wei He
 * 
 */
public class Config {

	/**
	 * OS types enumeration
	 * 
	 * @author Wei He
	 * 
	 */
	public static enum OS_TYPE {
		/**
		 * windows
		 */
		windows,
		/**
		 * linux
		 */
		linux,
		/**
		 * others
		 */
		others,
		/**
		 * not set
		 */
		notset
	};

	/**
	 * OS bit versions enumeration
	 * 
	 * @author Wei He
	 * 
	 */
	public static enum OS_BIT_VERSION {
		/**
		 * 32 bit
		 */
		bit_32,
		/**
		 * 64 bit
		 */
		bit_64,
		/**
		 * not set
		 */
		notset
	};

	/**
	 * config file path
	 */
	private static final String config_file_path = "beagle_gui.config";
	/**
	 * config instance
	 */
	private static Config config_instance = null;

	// the following variable names are self-explanatory, so I add no comment

	private static String AUTO_DETECT_OS_KEY = "auto_detect_os";
	private static String OS_TYPE_KEY = "os_type";
	private static String OS_BIT_VERSION_KEY = "os_bit_version";
	private static String BEAGLE_WIN32_EXEC_KEY = "beagle_windows_32_executable";
	private static String BEAGLE_WIN64_EXEC_KEY = "beagle_windows_64_executable";
	private static String BEAGLE_LINUX32_EXEC_KEY = "beagle_linux_32_executable";
	private static String BEAGLE_LINUX64_EXEC_KEY = "beagle_linux_64_executable";
	private static String BDD_METHOD_KEY = "bdd_method";
	private static String BMC_METHOD_KEY = "bmc_method";
	private static String BMC_DEFAULT_STEP_KEY = "bmc_default_step";
	private static String BMC_MIN_STEP_KEY = "bmc_minimum_step";
	private static String BMC_MAX_STEP_KEY = "bmc_maximum_step";
	private static String TEMP_DIR_KEY = "temporary_dir";
	private static String ELTS_EXT_KEY = "elts_ext";
	private static String GRAPH_EXT_KEY = "graph_ext";

	private static boolean AUTO_DETECT_OS_DEFAULT = true;
	private static OS_TYPE OS_TYPE_DEFAULT = OS_TYPE.notset;
	private static OS_BIT_VERSION OS_BIT_DEFAULT = OS_BIT_VERSION.notset;
	private static String BEAGLE_WIN32_EXEC_DEFAULT = "beagle/windows_x86/beagle.exe";
	private static String BEAGLE_WIN64_EXEC_DEFAULT = "beagle/windows_x64/beagle.exe";
	private static String BEAGLE_LINUX32_EXEC_DEFAULT = "beagle/linux_x86/beagle.exe";
	private static String BEAGLE_LINUX64_EXEC_DEFAULT = "beagle/linux_x64/beagle.exe";
	private static String[] BDD_METHOD_DEFAULT = new String[] { "whole",
			"separate", "eqsSeparate" };
	private static String[] BMC_METHOD_DEFAULT = new String[] { "std", "inc",
			"incDec", "macro", "macroInc", "macroIncDec" };
	private static int BMC_DEFAULT_STEP_DEFAULT = 100;
	private static int BMC_MIN_STEP_DEFAULT = 1;
	private static int BMC_MAX_STEP_DEFAULT = 10000;
	private static String TEMP_DIR_DEFAULT = "tmp";
	private static String ELTS_EXT_DEFAULT = "elt";
	private static String GRAPH_EXT_DEFAULT = "xml";

	private boolean auto_detect_os_val = AUTO_DETECT_OS_DEFAULT;
	private OS_TYPE os_type_val = OS_TYPE_DEFAULT;
	private OS_BIT_VERSION os_bit_val = OS_BIT_DEFAULT;
	private String beagle_win32_exec_val = BEAGLE_WIN32_EXEC_DEFAULT;
	private String beagle_win64_exec_val = BEAGLE_WIN64_EXEC_DEFAULT;
	private String beagle_linux32_exec_val = BEAGLE_LINUX32_EXEC_DEFAULT;
	private String beagle_linux64_exec_val = BEAGLE_LINUX64_EXEC_DEFAULT;
	private String[] bdd_method_val = BDD_METHOD_DEFAULT;
	private String[] bmc_method_val = BMC_METHOD_DEFAULT;
	private int bmc_default_step_val = BMC_DEFAULT_STEP_DEFAULT;
	private int bmc_min_step_val = BMC_MIN_STEP_DEFAULT;
	private int bmc_max_step_val = BMC_MAX_STEP_DEFAULT;
	private String temp_dir_val = TEMP_DIR_DEFAULT;
	private String elts_ext_val = ELTS_EXT_DEFAULT;
	private String graph_ext_val = GRAPH_EXT_DEFAULT;

	/**
	 * get instance of Config
	 * 
	 * @return instance
	 */
	public static Config getInstance() {
		if (config_instance == null)
			config_instance = new Config();
		return config_instance;
	}

	/**
	 * private constructor
	 */
	private Config() {
		init();
	}

	/**
	 * read from config files
	 */
	private void init() {
		String configFilePath = config_file_path;
		File configFile = new File(configFilePath);
		if (!configFile.exists() || !configFile.isFile())
			return;
		try {
			BufferedReader br = new BufferedReader(new FileReader(configFile));

			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				int commentStartIndex = line.indexOf('#');
				if (commentStartIndex >= 1)
					line = line.substring(0, commentStartIndex - 1);
				else if (commentStartIndex == 0)
					line = "";
				line = line.trim();
				if (!line.isEmpty()) {
					int firstCommaIndex = line.indexOf(':');
					int lastCommaIndex = line.lastIndexOf(':');
					if (firstCommaIndex != -1
							&& firstCommaIndex == lastCommaIndex) {
						String key = line.substring(0, firstCommaIndex).trim()
								.toLowerCase();
						String value = line.substring(firstCommaIndex + 1)
								.trim();

						if (!value.isEmpty()) {
							if (key.equalsIgnoreCase(AUTO_DETECT_OS_KEY)) {
								if (value.equalsIgnoreCase("true"))
									this.auto_detect_os_val = true;
								else if (value.equalsIgnoreCase("false"))
									this.auto_detect_os_val = false;

							} else if (key.equalsIgnoreCase(OS_TYPE_KEY)) {
								if (value.equalsIgnoreCase("windows"))
									this.os_type_val = OS_TYPE.windows;
								else if (value.equalsIgnoreCase("linux"))
									this.os_type_val = OS_TYPE.linux;

							} else if (key.equalsIgnoreCase(OS_BIT_VERSION_KEY)) {
								if (value.equalsIgnoreCase("32"))
									this.os_bit_val = OS_BIT_VERSION.bit_32;
								else if (value.equalsIgnoreCase("64"))
									this.os_bit_val = OS_BIT_VERSION.bit_64;

							} else if (key
									.equalsIgnoreCase(BEAGLE_WIN32_EXEC_KEY)) {
								this.beagle_win32_exec_val = value;
							} else if (key
									.equalsIgnoreCase(BEAGLE_WIN64_EXEC_KEY)) {
								this.beagle_win64_exec_val = value;
							} else if (key
									.equalsIgnoreCase(BEAGLE_LINUX32_EXEC_KEY)) {
								this.beagle_linux32_exec_val = value;
							} else if (key
									.equalsIgnoreCase(BEAGLE_LINUX64_EXEC_KEY)) {
								this.beagle_linux64_exec_val = value;
							} else if (key.equalsIgnoreCase(BDD_METHOD_KEY)) {
								String[] methods = value.split("(\\s)*,(\\s)*");
								if (methods.length > 0)
									this.bdd_method_val = methods;
							} else if (key.equalsIgnoreCase(BMC_METHOD_KEY)) {
								String[] methods = value.split("(\\s)*,(\\s)*");
								if (methods.length > 0)
									this.bmc_method_val = methods;
							} else if (key
									.equalsIgnoreCase(BMC_DEFAULT_STEP_KEY)) {
								try {
									int val = Integer.parseInt(value);
									this.bmc_default_step_val = val;
								} catch (NumberFormatException ex) {
								}

							} else if (key.equalsIgnoreCase(BMC_MIN_STEP_KEY)) {
								try {
									int val = Integer.parseInt(value);
									this.bmc_min_step_val = val;
								} catch (NumberFormatException ex) {
								}

							} else if (key.equalsIgnoreCase(BMC_MAX_STEP_KEY)) {
								try {
									int val = Integer.parseInt(value);
									this.bmc_max_step_val = val;
								} catch (NumberFormatException ex) {
								}

							} else if (key.equalsIgnoreCase(TEMP_DIR_KEY)) {
								this.temp_dir_val = value;
							} else if (key.equalsIgnoreCase(ELTS_EXT_KEY)) {
								this.elts_ext_val = value;
							} else if (key.equalsIgnoreCase(GRAPH_EXT_KEY)) {
								this.graph_ext_val = value;
							}
						}
					}
				}
			}
			br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// the following methods are simple and their names are self explanatory, so
	// I add no comments

	public boolean get_auto_detect_os() {
		return (this.auto_detect_os_val || this.os_type_val == OS_TYPE.notset || this.os_bit_val == OS_BIT_VERSION.notset);
	}

	public OS_TYPE get_os_type() {
		return this.os_type_val;
	}

	public OS_BIT_VERSION get_os_bit_version() {
		return this.os_bit_val;
	}

	public String get_win32_beagle_executable_path() {
		return this.beagle_win32_exec_val;
	}

	public String get_win64_beagle_executable_path() {
		return this.beagle_win64_exec_val;
	}

	public String get_linux32_beagle_executable_path() {
		return this.beagle_linux32_exec_val;
	}

	public String get_linux64_beagle_executable_path() {
		return this.beagle_linux64_exec_val;
	}

	public String[] get_bdd_methods() {
		return this.bdd_method_val;
	}

	public String[] get_bmc_methods() {
		return this.bmc_method_val;
	}

	public int get_bmc_default_step() {
		return this.bmc_default_step_val;
	}

	public int get_bmc_min_step() {
		return this.bmc_min_step_val;
	}

	public int get_bmc_max_step() {
		return this.bmc_max_step_val;
	}

	public String get_tmp_dir() {
		return this.temp_dir_val;
	}

	public String get_elts_ext() {
		return this.elts_ext_val;
	}

	public String get_graph_ext() {
		return this.graph_ext_val;
	}
}
