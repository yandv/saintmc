package tk.yallandev.saintmc.common.utils.reflection;

import java.lang.reflect.Field;

public class ReflectionUtils {
	
	public static void setValue(String field, Class<?> clazz, Object instance, Object value) {
		try {
			Field f = clazz.getDeclaredField(field);
			f.setAccessible(true);
			f.set(instance, value);
		} catch (Exception exception) {

		}
	}

}
