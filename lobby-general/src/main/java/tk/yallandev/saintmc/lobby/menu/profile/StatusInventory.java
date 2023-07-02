package tk.yallandev.saintmc.lobby.menu.profile;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.combat.CombatStatus;
import tk.yallandev.saintmc.common.account.status.types.game.GameStatus;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalStatus;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;

public class StatusInventory {

	public StatusInventory(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		MenuInventory inv = new MenuInventory("§7§nSeu status", 5);

		inv.setItem(13, new ItemBuilder().type(Material.SKULL_ITEM).durability(3).skin(player.getName())
				.name((member.getGroup() == Group.MEMBRO ? "§7" + player.getName()
						: Tag.getByName(member.getGroup().toString()).getPrefix() + " " + player.getName()) + " "
						+ "§7(" + member.getLeague().getColor() + member.getLeague().getSymbol() + "§7)")
				.build());

		inv.setItem(29, createItem(player.getUniqueId(), StatusType.PVP));
		inv.setItem(30, createItem(player.getUniqueId(), StatusType.SHADOW));
		inv.setItem(31, createItem(player.getUniqueId(), StatusType.GLADIATOR));
		inv.setItem(32, createItem(player.getUniqueId(), StatusType.HG));

		CommonGeneral.getInstance().getStatusManager().unloadStatus(player.getUniqueId());

		inv.open(player);
	}

	private ItemStack createItem(UUID uuid, StatusType statusType) {
		String name = null;
		String lore = "";
		Material type = Material.STONE;

		switch (statusType) {
		case PVP: {
			NormalStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(uuid, statusType,
					NormalStatus.class);

			name = "§aKitPvP";
			lore = "\n§f§fKills: §7" + status.getKills() + "\n§fDeaths: §7" + status.getDeaths() + "\n§fKillstreak: §7"
					+ status.getKillstreak() + "\n§fMaior killstreak: §7" + status.getMaxKillstreak();
			type = Material.DIAMOND_SWORD;
			break;
		}
		case SHADOW: {
			CombatStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(uuid, statusType,
					CombatStatus.class);

			name = "§a1v1";
			lore = "\n§fPartidas: §7" + (status.getKills() + status.getDeaths()) + "\n§fKills: §7" + status.getKills()
					+ "\n§fDeaths: §7" + status.getDeaths() + "\n§fKillstreak: §7" + status.getKillstreak()
					+ "\n§fMaior killstreak: §7" + status.getMaxKillstreak();
			type = Material.BLAZE_ROD;
			break;
		}
		case HG: {
			GameStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(uuid, statusType,
					GameStatus.class);

			name = "§aHungerGames";
			lore = "\n§fPartidas: §7" + status.getMatches() + "\n§fKills: §7" + status.getKills() + "\n§fDeaths: §7"
					+ status.getDeaths() + "\n§fWins: §7" + status.getWins() + "\n§fMaior killstreak: §7"
					+ status.getMaxKillstreak();
			type = Material.MUSHROOM_SOUP;
			break;
		}
		case GLADIATOR: {
			CombatStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(uuid, statusType,
					CombatStatus.class);

			name = "§aGladiator";
			lore = "\n§fPartidas: §7" + (status.getKills() + status.getDeaths()) + "\n§fWins: §7" + status.getKills()
					+ "\n§fLosses: §7" + status.getDeaths() + "\n§fMaior killstreak: §7" + status.getMaxKillstreak();
			type = Material.IRON_FENCE;
			break;
		}
		default:
			break;
		}

		return new ItemBuilder().name(name).lore(lore).type(type).build();
	}
}
