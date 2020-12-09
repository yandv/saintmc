package tk.yallandev.saintmc.bukkit.menu.account;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.player.PlayerAPI;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.profile.Profile;

public class SkinInventory {

	private static final Map<String, Skin> SKIN_MAP;

	static {
		SKIN_MAP = new HashMap<>();

		SKIN_MAP.put("yandv",
				new Skin("yandv", new Profile("yandv", UUID.fromString("fa1a1461-8e39-4536-89ba-6a54143ddaeb"))));
		SKIN_MAP.put("CabecinhaDeKiwi", new Skin("CabecinhaDeKiwi",
				new Profile("CabecinhaDeKiwi", UUID.fromString("6560284e-863b-48f6-901f-8cebdcee404f"))));
		SKIN_MAP.put("HugPvP",
				new Skin("HugPvP", new Profile("HugPvP", UUID.fromString("673daeda-5464-42e5-9ad8-7a0d917b276b"))));
	}

	public SkinInventory(Player player, Member member, MenuType menuType) {
		MenuInventory menuInventory = new MenuInventory("§7Sua skin", menuType == MenuType.LIBRARY ? 6 : 5);

		switch (menuType) {
		case GENERAL: {
			menuInventory.setItem(13, new ItemBuilder()
					.name("§aSua skin: "
							+ (member.hasSkin() ? member.getSkinProfile().getPlayerName() : member.getPlayerName()))
					.lore("\n" + (member.hasSkin()
							? "§7Skin customizada " + (SKIN_MAP.containsKey(member.getSkinProfile().getPlayerName())
									? "originada da biblioteca"
									: "")
							: "§7Skin original da sua conta"))
					.type(Material.SKULL_ITEM)
					.skin(member.hasSkin() ? member.getSkinProfile().getPlayerName() : member.getPlayerName())
					.durability(3).build());

			menuInventory.setItem(30,
					new ItemBuilder().name("§aCustomizar skin").type(Material.PAPER)
							.lore("", "§7Escolha uma skin customizada", "§7baseado em um nickname", "",
									"§eClique para saber mais.")
							.build(),
					(p, inv, type, stack, slot) -> {
						new SkinInventory(player, member, MenuType.CUSTOMIZE);
					});

			menuInventory.setItem(32,
					new ItemBuilder().name("§aBiblioteca").type(Material.ENCHANTED_BOOK)
							.lore("", "§7Confira o pacote de skins", "§7padrão disponibilizado pelo servidor", "",
									"§eClique para saber mais.")
							.build(),
					(p, inv, type, stack, slot) -> {
						new SkinInventory(player, member, MenuType.LIBRARY);
					});

			menuInventory.open(player);
			break;
		}
		case LIBRARY: {
			int w = 10;

			for (Entry<String, Skin> entry : SKIN_MAP.entrySet()) {
				Skin skin = entry.getValue();
				menuInventory.setItem(w, new ItemBuilder().name("§a" + skin.getDisplayName()).type(Material.SKULL_ITEM)
						.durability(3).skin(skin.getDisplayName()).build(), (p, inv, type, stack, slot) -> {
							if (member.hasSkin() && member.getSkinProfile().equals(skin.getProfile())) {
								p.sendMessage("§cVocê já está usando essa skin!");
								p.closeInventory();
								return;
							}

							member.setSkinProfile(skin.getProfile());
							p.sendMessage("§aSua skin foi alterada para " + skin.getDisplayName() + "!");
							p.closeInventory();

							Bukkit.getScheduler().runTaskAsynchronously(BukkitMain.getInstance(),
									() -> PlayerAPI.changePlayerSkin(p, skin.getProfile().getPlayerName(),
											skin.getProfile().getUniqueId(), true));
						});

				w++;
				if (w % 9 == 8) {
					w += 2;
				}
			}

			menuInventory.setItem(49, new ItemBuilder().name("§cRestaurar skin").type(Material.BARRIER)
					.lore("§7Clique para restaurar sua skin.").build(), (p, inv, type, stack, slot) -> {
						p.performCommand("skin #");
						p.closeInventory();
					});

			menuInventory.open(player);
			break;
		}
		case CUSTOMIZE: {
			player.sendMessage("§aPara alterar sua skin para uma customizada utilize /skin <skin>!");
			player.closeInventory();
		}
		}
	}

	public enum MenuType {

		GENERAL, LIBRARY, CUSTOMIZE;

	}

	@AllArgsConstructor
	@Getter
	public static class Skin {

		private String displayName;

		private Profile profile;

	}

}
