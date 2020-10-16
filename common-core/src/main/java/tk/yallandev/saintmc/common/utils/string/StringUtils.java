package tk.yallandev.saintmc.common.utils.string;

import java.util.List;

public class StringUtils {

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

	public enum TimeFormat {

		NORMAL, SHORT;

	}

}
