package tk.yallandev.saintmc.common.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.ClassGetter;

/*
 * Forked from https://github.com/mcardy/CommandFramework
 * 
 */

public interface CommandFramework {
	
	Class<?> getJarClass();

	void registerCommands(CommandClass commandClass);

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Command {

		String name();

		Group groupToUse() default Group.MEMBRO;

		String[] aliases() default {};

		String description() default "";

		String usage() default "";

		boolean runAsync() default false;

	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Completer {

		String name();

		String[] aliases() default {};

	}
	
	default CommandFramework loadCommands(String packageName) {
		for (Class<?> commandClass : ClassGetter.getClassesForPackage(getJarClass(), packageName))
			if (CommandClass.class.isAssignableFrom(commandClass)) {
				try {
					registerCommands((CommandClass) commandClass.newInstance());
				} catch (Exception ex) {
					CommonGeneral.getInstance().getLogger()
							.warning("Error when loading command from " + commandClass.getSimpleName() + "!");
					ex.printStackTrace();
				}
			}

		return this;
	}

	default CommandFramework loadCommands(Class<?> jarClass, String packageName) {
		for (Class<?> commandClass : ClassGetter.getClassesForPackage(jarClass, packageName))
			if (CommandClass.class.isAssignableFrom(commandClass)) {
				try {
					registerCommands((CommandClass) commandClass.newInstance());
				} catch (Exception e) {
					CommonGeneral.getInstance().getLogger()
							.warning("Error when loading command from " + commandClass.getSimpleName() + "!");
					e.printStackTrace();
				}
			}

		return this;
	}

}
