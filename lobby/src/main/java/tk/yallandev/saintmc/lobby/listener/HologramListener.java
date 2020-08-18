package tk.yallandev.saintmc.lobby.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.hologramapi.hologram.Hologram;
import tk.yallandev.hologramapi.hologram.impl.SimpleHologram;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.event.LocationChangeEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalStatus;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.lobby.LobbyMain;

public class HologramListener implements Listener {

	private Hologram pvpHologram;
	private Hologram killsHologram;

	public HologramListener() {
		new BukkitRunnable() {

			@Override
			public void run() {
				createKills();
			}
		}.runTaskTimer(LobbyMain.getInstance(), 80, 20 * 60 * 10);
		createPvP();
	}

	@EventHandler
	public void onLocationChange(LocationChangeEvent event) {
		if (event.getConfigName().equals("hologram-lobby-kills"))
			createKills();
		else if (event.getConfigName().equals("hologram-pvp"))
			createPvP();
	}

	public void createPvP() {
		if (pvpHologram != null) {
			pvpHologram.remove();
			pvpHologram = null;
		}

		pvpHologram = BukkitMain.getInstance().getHologramController().createHologram("§6§lArena PvP",
				BukkitMain.getInstance().getLocationFromConfig("hologram-pvp"), SimpleHologram.class);

		pvpHologram.addLine("");
		pvpHologram.addLine("Pule para entrar na §aarena");
		pvpHologram.addLine("");
		pvpHologram.addLine("O §aPvP§f na arena está §aativado§f,");
		pvpHologram.addLine("§fas estatisticas da arena são usadas");
		pvpHologram.addLine("§fapenas para o rankeamento no lobby");
		pvpHologram.addLine("");
		pvpHologram.addLine("§eO drop de §bxp§e está desativado!");
	}

	public void createKills() {
		if (killsHologram != null) {
			killsHologram.remove();
			killsHologram = null;
		}

		if (killsHologram == null) {
			killsHologram = BukkitMain.getInstance().getHologramController().createHologram("§6§lRANKING - KILLS",
					BukkitMain.getInstance().getLocationFromConfig("hologram-lobby-kills"), SimpleHologram.class);
		}

		killsHologram.addLine("§eJogadores com as maiores");
		killsHologram.addLine("§equantidade de kills da arena!");
		killsHologram.addLine("");

		int index = 1;

		for (Object model : CommonGeneral.getInstance().getStatusData().ranking(StatusType.LOBBY, "kills")) {
			if (model instanceof NormalStatus) {
				NormalStatus normalModel = (NormalStatus) model;

				Member member = CommonGeneral.getInstance().getMemberManager().getMember(normalModel.getUniqueId());

				if (member == null) {
					try {
						MemberModel loaded = CommonGeneral.getInstance().getPlayerData()
								.loadMember(normalModel.getUniqueId());

						if (loaded == null) {
							CommonGeneral.getInstance().debug("Não foi possível pegar as informações do jogador "
									+ normalModel.getUniqueId() + "!");
						} else {
							member = new MemberVoid(loaded);
						}

					} catch (Exception e) {
						CommonGeneral.getInstance().debug(
								"Não foi possível pegar as informações do jogador " + normalModel.getUniqueId() + "!");
					}
				}

				if (member != null) {
					killsHologram.addLine("§a" + index + "° §7- "
							+ ChatColor.getLastColors(Tag.valueOf(member.getGroup().name()).getPrefix())
							+ member.getPlayerName() + " §7- §3" + normalModel.getKills() + " kills");
				}
				index++;
			}
		}
	}

}
