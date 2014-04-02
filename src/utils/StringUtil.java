package utils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for some string operations
 * 
 * @author Wei He
 * 
 */
public class StringUtil {
	/**
	 * regular expression for valid elts identifiers must start with alpha
	 * letter or '_', and contain only digits or alpha letters or '_'
	 */
	private static String IDENTIFIER_REG = "[_A-Za-z][_A-Za-z\\d]*";

	/**
	 * judge whether string is valid identifier in ELTS
	 * 
	 * @param str
	 *            string
	 * @return a boolean indicator
	 */
	public static boolean validIdentifier(String str) {
		Pattern pattern = Pattern.compile("^" + IDENTIFIER_REG + "$");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * judge whether string is a concatenation of valid identifiers in ELTS, if
	 * multiple identifiers, they must be separated by commas
	 * 
	 * @param str
	 *            string
	 * @return a boolean indicator
	 */
	public static boolean validMutipleIdenfitiersSeparatedByComma(String str) {
		String reg = "^" + IDENTIFIER_REG + "((\\s)*,(\\s)*" + IDENTIFIER_REG
				+ ")*$";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * change extension of a file path, if the file path has no extension, then
	 * append one
	 * 
	 * @param path
	 *            file path
	 * @param ext
	 *            extension
	 * @return the changed file path
	 */
	public static String substitutePathExt(String path, String ext) {
		File pathFile = new File(path);
		String fileName = pathFile.getName();
		int position = fileName.lastIndexOf('.');
		if (position >= 0)
			fileName = fileName.substring(0, position + 1) + ext;
		else
			fileName = fileName + "." + ext;
		File file = new File(pathFile.getParent(), fileName);
		return file.getPath();
	}
}