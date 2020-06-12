package tk.yallandev.saintmc.lobby.menu.profile;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;

public class StatusInventory {

	public StatusInventory(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		MenuInventory inv = new MenuInventory("§7Seu status", 5);

		inv.setItem(13, new ItemBuilder().type(Material.SKULL_ITEM).durability(3).skin(player.getName())
				.name((member.getGroup() == Group.MEMBRO ? "§7" + player.getName()
						: Tag.getByName(member.getGroup().toString()).getPrefix() + " " + player.getName()) + " "
						+ "§7(" + member.getLeague().getColor() + member.getLeague().getSymbol() + "§7)")
				.build());

		inv.setItem(29, createItem(player.getUniqueId(), StatusType.PVP));
		inv.setItem(30, createItem(player.getUniqueId(), StatusType.SHADOW));
		inv.setItem(31, createItem(player.getUniqueId(), StatusType.HG));
		inv.setItem(32, new ItemBuilder().type(Material.IRON_FENCE).name("§3§lGladiator")
				.lore("", "§fKills: §70", "§fMortes: §70", "").build());

		CommonGeneral.getInstance().getStatusManager().unloadStatus(player.getUniqueId());

		inv.open(player);
	}

	private ItemStack createItem(UUID uuid, StatusType statusType) {
		Status status = CommonGeneral.getInstance().getStatusManager().loadStatus(uuid, statusType);

		String name = null;
		String lore = "";
		Material type = Material.STONE;

		switch (statusType) {
		case PVP: {
			name = "§1§lKitPvP";
			lore = "\n§f§fKills: §7" + status.getKills() + "\n§fDeaths: §7" + status.getDeaths()
					+ "\n§fMaior killstreak: §7" + status.getMaxKillstreak();
			type = Material.DIAMOND_SWORD;
			break;
		}
		case SHADOW: {
			name = "§9§l1v1";
			lore = "\n§fPartidas: §7" + (status.getKills() + status.getDeaths()) + "\n§fKills: §7" + status.getKills()
					+ "\n§fDeaths: §7" + status.getDeaths() + "\n§fMaior killstreak: §7" + status.getMaxKillstreak();
			type = Material.BLAZE_ROD;
			break;
		}
		case HG: {
			name = "§a§lHungerGames";
			lore = "\n§fPartidas: §7" + status.getMatches() + "\n§fKills: §7" + status.getKills() + "\n§fDeaths: §7"
					+ status.getDeaths() + "\n§fWins: §7" + status.getWins() + "\n§fMaior killstreak: §7"
					+ status.getMaxKillstreak();
			type = Material.MUSHROOM_SOUP;
//			inv.setItem(31, new ItemBuilder().type(Material.MUSHROOM_SOUP).name("§a§lHungerGames")
//					.lore("", "§fPartidas: §7", "§fSKills: §70", "§fortes: §70", "").build());
			break;
		}
		case GLADIATOR: {
			break;
		}
		}

		status = null;

		return new ItemBuilder().name(name).lore(lore).type(type).build();
	}
}
