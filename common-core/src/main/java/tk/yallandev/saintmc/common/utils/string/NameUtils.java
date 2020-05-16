package tk.yallandev.saintmc.common.utils.string;

public class NameUtils {

	public static String formatString(String string) {
		char[] stringArray = string.toLowerCase().toCharArray();
		stringArray[0] = Character.toUpperCase(stringArray[0]);
		return new String(stringArray);
	}
	
	public static String getName(String string) {
		return toReadable(string);
	}

	public static String toReadable(String string) {
		String[] names = string.split("_");
		for (int i = 0; i < names.length; i++) {
			names[i] = names[i].substring(0, 1) + names[i].substring(1).toLowerCase();
		}
		return StringUtils.join(names, " ");
	}

}