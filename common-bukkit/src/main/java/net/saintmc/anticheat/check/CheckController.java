package net.saintmc.anticheat.check;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import net.saintmc.anticheat.storage.Storage;
import tk.yallandev.saintmc.bukkit.BukkitMain;

public class CheckController {

	private Map<CheckClass, List<Entry<CheckType, Method>>> handlerMap;

	public CheckController() {
		handlerMap = new HashMap<>();
	}

	public void registerCheck(CheckClass checkClass) {
		List<Entry<CheckType, Method>> list = new ArrayList<>();

		if (Listener.class.isAssignableFrom(checkClass.getClass()))
			Bukkit.getPluginManager().registerEvents((Listener) checkClass, BukkitMain.getInstance());

		for (Method m : checkClass.getClass().getMethods()) {
			CheckHandler checkHandler = m.getAnnotation(CheckHandler.class);

			if (checkHandler == null)
				continue;

			if (m.getParameterTypes().length > 1 || m.getParameterTypes().length <= 0
					|| !Storage.class.isAssignableFrom(m.getParameterTypes()[0])) {
				System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
				continue;
			}

			if (m.getReturnType() == boolean.class) {
				list.add(new AbstractMap.SimpleEntry<CheckType, Method>(checkHandler.checkType(), m));
				System.out.println("Registered " + m.getName());
			}
		}

		handlerMap.put(checkClass, list);
	}

	public <T extends Storage> T call(CheckType checkType, T t) {
		for (Entry<CheckClass, List<Entry<CheckType, Method>>> e : handlerMap.entrySet()) {
			for (Entry<CheckType, Method> entry : e.getValue()) {
				if (entry.getKey() == checkType)
					try {
						entry.getValue().invoke(e.getKey(), t);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
						e1.printStackTrace();
					}
			}
		}

		return t;
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface CheckHandler {

		CheckType checkType() default CheckType.MOVEMENT;

	}

	public enum CheckType {

		MOVEMENT, DAMAGE, ATTACK, INTERACT;

	}

}
