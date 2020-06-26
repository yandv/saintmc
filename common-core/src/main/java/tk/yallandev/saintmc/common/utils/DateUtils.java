package tk.yallandev.saintmc.common.utils;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {
	
	public static String getTime(long expire) {
		String string = DateUtils.formatDifference((expire - System.currentTimeMillis()) / 1000L);
		if (string == null || string.isEmpty()) {
			string = "0 segundo";
		}
		return string;
	}
	
	public static String formatTime(long time, DecimalFormat decimalFormat) {
		long seconds = (time - System.currentTimeMillis()) / 1000l;
		return decimalFormat.format(seconds);
	}

	public static String formatDifference(long time) {
		if (time == 0L) {
			return "";
		}
		
		long day = TimeUnit.SECONDS.toDays(time);
		long hours = TimeUnit.SECONDS.toHours(time) - day * 24L;
		long minutes = TimeUnit.SECONDS.toMinutes(time) - TimeUnit.SECONDS.toHours(time) * 60L;
		long seconds = TimeUnit.SECONDS.toSeconds(time) - TimeUnit.SECONDS.toMinutes(time) * 60L;

		StringBuilder sb = new StringBuilder();
		if (day > 0L) {
			sb.append(day).append(" ").append("dias").append(" ");
		}
		if (hours > 0L) {
			sb.append(hours).append(" ").append("horas").append(" ");
		}
		if (minutes > 0L) {
			sb.append(minutes).append(" ").append("minutos").append(" ");
		}
		if (seconds > 0L) {
			sb.append(seconds).append(" ").append("segundos");
		}
		String diff = sb.toString();

		return diff.isEmpty() ? "0 " + "segundo" : diff;
	}

	public static Long getTime(String string) {
		try {
			return parseDateDiff(string, true) + TimeUnit.SECONDS.toMillis(1);
		} catch (Exception e) {}
		return null;
	}

    public static String getDifferenceFormat(long timestamp) {
        return formatDifference(timestamp - (System.currentTimeMillis() / 1000L));
    }

    public static long parseDateDiff(String time, boolean future) throws Exception {
        Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
        Matcher m = timePattern.matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() == null || m.group().isEmpty()) {
                continue;
            }
            
            for (int i = 0; i < m.groupCount(); i++) {
                if (m.group(i) != null && !m.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }
            
            if (found) {
                if (m.group(1) != null && !m.group(1).isEmpty()) {
                    years = Integer.parseInt(m.group(1));
                }
                
                if (m.group(2) != null && !m.group(2).isEmpty()) {
                    months = Integer.parseInt(m.group(2));
                }
                
                if (m.group(3) != null && !m.group(3).isEmpty()) {
                    weeks = Integer.parseInt(m.group(3));
                }
                
                if (m.group(4) != null && !m.group(4).isEmpty()) {
                    days = Integer.parseInt(m.group(4));
                }
                
                if (m.group(5) != null && !m.group(5).isEmpty()) {
                    hours = Integer.parseInt(m.group(5));
                }
                
                if (m.group(6) != null && !m.group(6).isEmpty()) {
                    minutes = Integer.parseInt(m.group(6));
                }
                
                if (m.group(7) != null && !m.group(7).isEmpty()) {
                    seconds = Integer.parseInt(m.group(7));
                }
                
                break;
            }
        }
        if (!found) {
            throw new Exception("Illegal Date");
        }

        if (years > 100) {
            throw new Exception("Illegal Date");
        }

        Calendar c = new GregorianCalendar();
        if (years > 0) {
            c.add(Calendar.YEAR, years * (future ? 1 : -1));
        }
        if (months > 0) {
            c.add(Calendar.MONTH, months * (future ? 1 : -1));
        }
        if (weeks > 0) {
            c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
        }
        if (days > 0) {
            c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
        }
        if (hours > 0) {
            c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
        }
        if (minutes > 0) {
            c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
        }
        if (seconds > 0) {
            c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
        }
        return c.getTimeInMillis();
    }
    
    public static boolean isForever(long time) {
    	return time - System.currentTimeMillis() >= (1000l * 60l * 60l * 24l * 300l);
    }
    
    public static void main(String[] args) {
		long time = System.currentTimeMillis() + (1000l * 60l * 60l * 24l * 350l);
		
		System.out.println(time);
		System.out.println(getTime(time));
		
		System.out.println(time - System.currentTimeMillis());
		System.out.println(1000 * 60 * 60 * 24 * 300);
		System.out.println(isForever(time));
	}
    
}
