package br.com.saintmc.hungergames.controller;

import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.event.VarChangeEvent;
import org.bukkit.Bukkit;
import tk.yallandev.saintmc.CommonGeneral;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VarManager {

    private Map<String, String> varMap;

    public VarManager() {
        varMap = new HashMap<>();

        if (GameMain.getInstance().getConfig().contains("var")) {
            for (String varName : GameMain.getInstance().getConfig().getConfigurationSection("var").getKeys(true)) {
                if (GameMain.getInstance().getConfig().get("var." + varName.toLowerCase()) instanceof String) {
                    CommonGeneral.getInstance().debug("The var " + varName + " has been loaded!");
                    varMap.put(varName.toLowerCase(),
                               GameMain.getInstance().getConfig().getString("var." + varName.toLowerCase()));
                }
            }
        }
    }

    public boolean hasVariable(String varName) {
        return varMap.containsKey(varName.toLowerCase());
    }

    public <T> T getVar(String varName, T defaultValue) {
        if (varMap.containsKey(varName.toLowerCase())) {
            if (defaultValue.getClass().isAssignableFrom(Boolean.class)) {
                return (T) Boolean.valueOf(varMap.get(varName.toLowerCase()));
            } else if (defaultValue.getClass().isAssignableFrom(Integer.class)) {
                return (T) Integer.valueOf(varMap.get(varName.toLowerCase()));
            } else if (defaultValue.getClass().isAssignableFrom(Double.class)) {
                return (T) Double.valueOf(varMap.get(varName.toLowerCase()));
            } else if (defaultValue.getClass().isAssignableFrom(Long.class)) {
                return (T) Long.valueOf(varMap.get(varName.toLowerCase()));
            }

            return (T) varMap.get(varName.toLowerCase());
        }

        setVar(varName, defaultValue, false);
        GameMain.getInstance().getConfig().set("var." + varName.toLowerCase(), defaultValue.toString());
        GameMain.getInstance().saveConfig();
        return defaultValue;
    }

    public String getVar(String varName) {
        if (varMap.containsKey(varName.toLowerCase())) {
            return varMap.get(varName.toLowerCase());
        }

        return null;
    }

    public <T> T getVar(String varName, Class<T> clazz) {
        if (varMap.containsKey(varName.toLowerCase())) {
            if (clazz.isAssignableFrom(Boolean.class)) {
                return clazz.cast(Boolean.valueOf(varMap.get(varName.toLowerCase())));
            } else if (clazz.isAssignableFrom(Integer.class)) {
                return clazz.cast(Integer.valueOf(varMap.get(varName.toLowerCase())));
            } else if (clazz.isAssignableFrom(Double.class)) {
                return clazz.cast(Double.valueOf(varMap.get(varName.toLowerCase())));
            } else if (clazz.isAssignableFrom(Long.class)) {
                return clazz.cast(Long.valueOf(varMap.get(varName.toLowerCase())));
            }

            return clazz.cast(varMap.get(varName.toLowerCase()));
        }

        return null;
    }

    public void saveVar(String varName) {
        GameMain.getInstance().getConfig().set("var." + varName.toLowerCase(), getVar(varName));
        GameMain.getInstance().saveConfig();
    }

    public void setVar(String varName, Object object, boolean save) {
        varMap.put(varName.toLowerCase(), object.toString());
        Bukkit.getPluginManager().callEvent(new VarChangeEvent(varName, getVar(varName), object.toString()));

        if (save) {
            GameMain.getInstance().getConfig().set("var." + varName.toLowerCase(), object.toString());
            GameMain.getInstance().saveConfig();
        }
    }

    public void setVar(String varName, Object object) {
        setVar(varName, object, false);
    }

    public Set<String> getVariables() {
        return varMap.keySet();
    }

    public ClassType getClassType(String varName) {
        if (varMap.containsKey(varName.toLowerCase())) {
            return ClassType.getClassType(varMap.get(varName.toLowerCase()));
        }

        return ClassType.NONE;
    }

    public enum ClassType {

        BOOLEAN, INTEGER, LONG, DOUBLE, STRING, NONE;

        public static ClassType getClassType(String value) {
            try {
                Integer.valueOf(value);
                return ClassType.INTEGER;
            } catch (NumberFormatException ex) {

            }

            try {
                Double.valueOf(value);
                return ClassType.DOUBLE;
            } catch (NumberFormatException ex) {

            }

            try {
                Long.valueOf(value);
                return ClassType.LONG;
            } catch (NumberFormatException ex) {

            }

            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                return ClassType.BOOLEAN;
            }

            return ClassType.STRING;
        }

        public boolean isNumber() {
            return this == INTEGER || this == DOUBLE || this == LONG;
        }
    }
}
