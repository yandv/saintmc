package br.com.saintmc.hungergames.constructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.saintmc.hungergames.utils.JsonItemStack;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;

/**
 * SimpleKit store information of player inventory and the player's potion
 * effect list
 *
 * @author yandv
 * @since 1.0
 */

@Getter
public class SimpleKit {

    private String kitName;

    private ItemStack[] armorContents;
    private ItemStack[] contents;

    private List<PotionEffect> effectList;

    public String toString() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("kitName", kitName);

        JsonObject armorContents = new JsonObject();

        for (int i = 0; i < this.armorContents.length; i++) {
            if (this.armorContents[i] == null) {
                armorContents.add(String.valueOf(i), JsonItemStack.toJson(new ItemStack(Material.AIR)));
            } else {
                armorContents.add(String.valueOf(i), JsonItemStack.toJson(this.armorContents[i]));
            }
        }

        jsonObject.add("armorContents", armorContents);

        JsonObject contents = new JsonObject();

        for (int i = 0; i < this.contents.length; i++) {
            if (this.contents[i] == null) {
                contents.add(String.valueOf(i), JsonItemStack.toJson(new ItemStack(Material.AIR)));
            } else {
                contents.add(String.valueOf(i), JsonItemStack.toJson(this.contents[i]));
            }
        }

        jsonObject.add("contents", contents);


        return jsonObject.toString();
    }

    public static SimpleKit fromString(JsonObject jsonObject) {
        String kitName = jsonObject.get("kitName").getAsString();

        JsonObject armorContents = jsonObject.get("armorContents").getAsJsonObject();

        ItemStack[] armorContentsArray = new ItemStack[armorContents.size()];

        for (int i = 0; i < armorContents.size(); i++) {
            armorContentsArray[i] = JsonItemStack.fromJson(armorContents.get(String.valueOf(i)).getAsJsonObject());
        }

        JsonObject contents = jsonObject.get("contents").getAsJsonObject();

        ItemStack[] contentsArray = new ItemStack[contents.size()];

        for (int i = 0; i < contents.size(); i++) {
            contentsArray[i] = JsonItemStack.fromJson(contents.get(String.valueOf(i)).getAsJsonObject());
        }

        return new SimpleKit(kitName, armorContentsArray, contentsArray, new ArrayList<>());
    }

    public SimpleKit(String kitName, Player player) {
        this.kitName = kitName;

        this.contents = player.getInventory().getContents();
        this.armorContents = player.getInventory().getArmorContents();

        this.effectList = new ArrayList<>(player.getActivePotionEffects());
    }

    public SimpleKit(String kitName, ItemStack[] armorContents, ItemStack[] contents, List<PotionEffect> effectList) {
        this.kitName = kitName;

        this.contents = contents;
        this.armorContents = armorContents;

        this.effectList = effectList;
    }

    /**
     * Reload the SimpleKit contents with the param player's contents
     *
     * @param player
     */

    public void updateKit(Player player) {
        this.contents = player.getInventory().getContents();
        this.armorContents = player.getInventory().getArmorContents();

        this.effectList = new ArrayList<>(player.getActivePotionEffects());
    }

    /**
     * Clear player's inventory, set the SimpleKit contents and send message update
     * message to player
     *
     * @param player
     */

    public void apply(Player player) {
        applySilent(player);
        player.sendMessage("§aKit " + getKitName() + " aplicado com sucesso!");
    }

    /**
     * Clear player inventory and set the SimpleKit contents
     *
     * @param player
     */

    public void applySilent(Player player) {
        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(armorContents);

        player.getActivePotionEffects().clear();
        player.addPotionEffects(getEffectList());
    }
}
