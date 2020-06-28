package tk.yallandev.saintmc.bukkit.menu.account;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.player.PlayerAPI;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.profile.Profile;

public class SkinInventory {

    private static final Map<String, Skin> SKIN_MAP;

    static {
        SKIN_MAP = new HashMap<>();

        SKIN_MAP.put("Charlotte",
                     new Skin("Charlotte", new Profile("Charlotte", UUID.fromString("c90f8018-9a94-4ce3-959e-e57eac48f074"))));
        SKIN_MAP.put("Coquetel", new Skin("Coquetel", new Profile("Coquetel", UUID.fromString(
                "0c92ae64-73a2-4d6b-8727-ab875ea4f3a0"))));
    }

    public SkinInventory(Player player, Member member, MenuType menuType) {
        MenuInventory menuInventory = new MenuInventory("§7Sua skin", menuType == MenuType.LIBRARY ? 6 : 5);

        switch (menuType) {
        case GENERAL: {
            menuInventory.setItem(13, new ItemBuilder().name("§aSua skin: " + (member.hasSkin() ?
                                                                               member.getSkinProfile().getPlayerName() :
                                                                               member.getPlayerName())).lore("\n" +
                                                                                                             (member.hasSkin() ?
                                                                                                              "§7Skin customizada " +
                                                                                                              (SKIN_MAP.containsKey(
                                                                                                                      member
                                                                                                                              .getSkinProfile()
                                                                                                                              .getPlayerName()) ?
                                                                                                               "originada da biblioteca" :
                                                                                                               "") :
                                                                                                              "§7Skin original da sua conta"))
                                                       .type(Material.SKULL_ITEM).skin(member.hasSkin() ?
                                                                                       member.getSkinProfile()
                                                                                             .getPlayerName() :
                                                                                       member.getPlayerName())
                                                       .durability(3).build());

            menuInventory.setItem(30, new ItemBuilder().name("§aCustomizar skin").type(Material.PAPER)
                                                       .lore("", "§7Escolha uma skin customizada",
                                                             "§7baseado em um nickname", "",
                                                             "§eClique para saber mais.").build(),
                                  (p, inv, type, stack, slot) -> {
                                      new SkinInventory(player, member, MenuType.CUSTOMIZE);
                                      return false;
                                  });

            menuInventory.setItem(32, new ItemBuilder().name("§aBiblioteca").type(Material.ENCHANTED_BOOK)
                                                       .lore("", "§7Confira o pacote de skins",
                                                             "§7padrão disponibilizado pelo servidor", "",
                                                             "§eClique para saber mais.").build(),
                                  (p, inv, type, stack, slot) -> {
                                      new SkinInventory(player, member, MenuType.LIBRARY);
                                      return false;
                                  });

            menuInventory.open(player);
            break;
        }
        case LIBRARY: {
            int w = 10;

            for (Entry<String, Skin> entry : SKIN_MAP.entrySet()) {
                Skin skin = entry.getValue();
                menuInventory.setItem(w, new ItemBuilder().name("§a" + skin.getDisplayName()).type(Material.SKULL_ITEM)
                                                          .durability(3).skin(skin.getDisplayName()).build(),
                                      (p, inv, type, stack, slot) -> {
                                          if (member.hasSkin() && member.getSkinProfile().equals(skin.getProfile())) {
                                              p.sendMessage("§cVocê já está usando essa skin!");
                                              p.closeInventory();
                                              return false;
                                          }

                                          member.setSkinProfile(skin.getProfile());
                                          p.sendMessage("§aSua skin foi alterada para " + skin.getDisplayName() + "!");
                                          p.closeInventory();
                                          p.playSound(p.getLocation(), Sound.LEVEL_UP, .8f, .2f);

                                          PlayerAPI.changePlayerSkin(p, skin.getProfile().getPlayerName(),
                                                                     skin.getProfile().getUniqueId(), true);
                                          return false;
                                      });

                w++;
                if (w % 9 == 8) {
                    w += 2;
                }
            }

            menuInventory.setItem(48, new ItemBuilder().name("§aVoltar").type(Material.ARROW).build(),
                                  (p, inv, type, stack, slot) -> {
                                      new SkinInventory(player, member, MenuType.GENERAL);
                                      return false;
                                  });
            menuInventory.setItem(49, new ItemBuilder().name("§cRestaurar skin").type(Material.BARRIER)
                                                       .lore("§7Clique para restaurar sua skin.").build(),
                                  (p, inv, type, stack, slot) -> {
                                      p.performCommand("skin #");
                                      p.closeInventory();
                                      return false;
                                  });

            menuInventory.open(player);
            break;
        }
        case CUSTOMIZE: {
            player.sendMessage("§cPara alterar sua skin para uma customizada utilize /skin <skin>.");
            player.closeInventory();
        }
        }
    }

    public enum MenuType {

        GENERAL,
        LIBRARY,
        CUSTOMIZE;
    }

    @AllArgsConstructor
    @Getter
    public static class Skin {

        private String displayName;

        private Profile profile;
    }
}
