package tk.yallandev.saintmc.common.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tk.yallandev.saintmc.common.permission.Group;

/*
 * Forked from https://github.com/mcardy/CommandFramework
 * Took from https://github.com/Battlebits
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

		/**
		 * The command that this completer completes. If it is a sub command then its
		 * values would be separated by periods. ie. a command that would be a
		 * subcommand of test would be 'test.subcommandname'
		 *
		 * @return String
		 */
		String name();

		/**
		 * A list of alternate names that the completer is executed under. See name()
		 * for details on how names work
		 *
		 * @return String
		 */
		String[] aliases() default {};

	}

}
