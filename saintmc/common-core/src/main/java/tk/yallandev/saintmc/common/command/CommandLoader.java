package tk.yallandev.saintmc.common.command;

import java.io.File;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.utils.ClassGetter;

/*
 * https://github.com/mcardy/CommandFramework
 * Took from https://github.com/Battlebits
 * 
 */

@RequiredArgsConstructor
public class CommandLoader {

    @NonNull
    private CommandFramework framework;

    public int loadCommandsFromPackage(String packageName) {
        int i = 0;
        for (Class<?> commandClass : ClassGetter.getClassesForPackage(framework.getJarClass(), packageName)) {
            if (CommandClass.class.isAssignableFrom(commandClass)) {
                try {
                    CommandClass commands = (CommandClass) commandClass.newInstance();
                    framework.registerCommands(commands);
                } catch (Exception e) {
                    e.printStackTrace();
                    CommonGeneral.getInstance().getLogger().warning("Erro ao carregar comandos da classe " + commandClass.getSimpleName() + "!");
                }
                i++;
            }
        }
        return i;
    }

    public int loadCommandsFromPackage(File jarFile, String packageName) {
        int i = 0;
        
        for (Class<?> commandClass : ClassGetter.getClassesForPackageByFile(jarFile, packageName)) {
            if (CommandClass.class.isAssignableFrom(commandClass)) {
                try {
                    CommandClass commands = (CommandClass) commandClass.newInstance();
                    framework.registerCommands(commands);
                } catch (Exception e) {
                    e.printStackTrace();
                    CommonGeneral.getInstance().getLogger().warning("Erro ao carregar comandos da classe " + commandClass.getSimpleName() + "!");
                }
                i++;
            }
        }
        
        return i;
    }
}
