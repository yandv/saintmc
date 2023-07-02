package br.com.saintmc.hungergames.controller;

import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.SimpleKit;
import com.google.gson.JsonParser;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.common.controller.StoreController;

public class SimplekitController extends StoreController<String, SimpleKit> {

    public SimplekitController() {
        super();

        if (GameMain.getInstance().getConfig().contains("skit")) {
            GameMain.getInstance().getConfig().getConfigurationSection("skit").getKeys(false).forEach(this::loadKit);
        }
    }

    @Override
    public boolean containsKey(String key) {
        return super.containsKey(key.toLowerCase());
    }

    @Override
    public void load(String key, SimpleKit value) {
        super.load(key.toLowerCase(), value);
    }

    @Override
    public boolean unload(String key) {
        return super.unload(key.toLowerCase());
    }

    @Override
    public SimpleKit getValue(String key) {
        return super.getValue(key.toLowerCase());
    }

    public void saveKit(SimpleKit simpleKit) {
        GameMain.getInstance().getConfig().set("skit." + simpleKit.getKitName().toLowerCase(), simpleKit.toString());
        GameMain.getInstance().saveConfig();
    }

    public void loadKit(String kitName) {
        if (GameMain.getInstance().getConfig().contains("skit." + kitName.toLowerCase())) {
            load(kitName.toLowerCase(), SimpleKit.fromString(JsonParser.parseString(
                                                                               GameMain.getInstance().getConfig().getString("skit." + kitName.toLowerCase())).getAsJsonObject()
                                                                       .getAsJsonObject()));
        }
    }
}
