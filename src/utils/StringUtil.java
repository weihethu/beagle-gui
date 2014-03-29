package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	private static String IDENTIFIER_REG = "[_A-Za-z][_A-Za-z\\d]*";

	public static boolean validIdentifier(String str) {
		Pattern pattern = Pattern.compile("^" + IDENTIFIER_REG + "$");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public static boolean validMutipleIdenfitiersSeparatedByComma(String str) {
		String reg = "^" + IDENTIFIER_REG + "((\\s)*,(\\s)*" + IDENTIFIER_REG
				+ ")*$";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
}