package tk.yallandev.saintmc.common.utils.string;

import net.md_5.bungee.api.ChatColor;

import java.util.List;

public class StringUtils {

	public static boolean isColor(ChatColor color) {
		return !(color == ChatColor.BOLD || color == ChatColor.ITALIC || color == ChatColor.UNDERLINE ||
				 color == ChatColor.STRIKETHROUGH || color == ChatColor.MAGIC || color == ChatColor.RESET);
	}

	public static String getLastColors(String input) {
		StringBuilder result = new StringBuilder();
		int length = input.length();

		// Search backwards from the end as it is faster
		for (int index = length - 1; index > -1; index--) {
			char section = input.charAt(index);
			if (section == ChatColor.COLOR_CHAR && index < length - 1) {
				char c = input.charAt(index + 1);
				ChatColor color = ChatColor.getByChar(c);

				if (color != null) {
					result.insert(0, color.toString());

					// Once we find a color or reset we can stop searching
					if (!isColor(color) || color.equals(ChatColor.RESET)) {
						break;
					}
				}
			}
		}

		return result.toString();
	}

	public static String join(List<String> input, String separator) {
		if (input == null || input.size() <= 0)
			return "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.size(); i++) {

			sb.append(input.get(i));

			if (i != input.size() - 1) {
				sb.append(separator);
			}

		}

		return sb.toString();

	}

	public static String join(String[] input, String separator) {
		if (input == null || input.length <= 0)
			return "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length; i++) {

			sb.append(input[i]);

			if (i != input.length - 1) {
				sb.append(separator);
			}

		}

		return sb.toString();
	}

	public static String format(int time) {
		if (time >= 3600) {
			int hours = (time / 3600), minutes = (time % 3600) / 60, seconds = (time % 3600) % 60;
			return (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + ":"
					+ (seconds < 10 ? "0" : "") + seconds;
		} else {
			int minutes = (time / 60), seconds = (time % 60);
			return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
		}
	}

	public static String formatTime(int time) {
		int minutes = time / 60, seconds = (time % 3600) % 60;
		return (minutes > 0 ? minutes + "m " : "") + seconds + "s";
	}

	public static String formatTime(int time, TimeFormat timeFormat) {
		int minutes = time / 60, seconds = (time % 3600) % 60;

		switch (timeFormat) {
		case SHORT:
			return (minutes > 0 ? minutes + "m " : "") + seconds + "s";
		default:
			return (minutes > 0 ? minutes + (minutes == 1 ? " minuto " : " minutos ") : "") + seconds
					+ (seconds == 1 ? " segundo" : " segundos");
		}
	}

	public static String formatString(String string) {
		if (string.isEmpty()) {
			return string;
		}

		char[] stringArray = string.toLowerCase().toCharArray();
		stringArray[0] = Character.toUpperCase(stringArray[0]);
		return new String(stringArray);
	}


	public enum TimeFormat {

		NORMAL, SHORT;

	}

}
