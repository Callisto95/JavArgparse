package net.callisto.argparse;

import java.util.regex.*;

public class NameConverter {
	private static final Pattern camelToKebab = Pattern.compile("([A-Z])");
	private static final Pattern kebabToCamel = Pattern.compile("-(.)");
	
	public static String camelToKebabCase(final String camel) {
		return camelToKebab.matcher(camel).replaceAll(matchResult -> "-" + matchResult.group(1).toLowerCase());
	}
	
	public static String kebabToCamel(final String kebab) {
		return kebabToCamel.matcher(kebab).replaceAll(mr -> mr.group(1).toUpperCase());
	}
}
