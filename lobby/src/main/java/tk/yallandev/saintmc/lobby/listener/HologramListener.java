package tk.yallandev.saintmc.lobby.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.hologramapi.hologram.Hologram;
import tk.yallandev.hologramapi.hologram.impl.SimpleHologram;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalModel;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.lobby.LobbyMain;

public class HologramListener implements Listener {

	public HologramListener() {
		Hologram hologram = BukkitMain.getInstance().getHologramController().createHologram("§6§lArena PvP",
				new Location(Bukkit.getWorld("world"), -4.5, 123, -50.5), SimpleHologram.class);

		hologram.addLine("");
		hologram.addLine("Pule para entrar na §aarena");
		hologram.addLine("");
		hologram.addLine("O §aPvP§f na arena está §aativado§f,");
		hologram.addLine("§fas estatisticas da arena são usadas");
		hologram.addLine("§fapenas para o rankeamento no lobby");
		hologram.addLine("");
		hologram.addLine("§eO drop de §bxp§e está desativado!");
		
		ranking();
	}

	private void ranking() {
		new BukkitRunnable() {
			
			Hologram hologram = null;

			@Override
			public void run() {
				
				if (hologram != null) {
					hologram.remove();
					hologram = null;
				}
				
				if (hologram == null) {
					hologram = BukkitMain.getInstance().getHologramController().createHologram(
							"§6§lRANKING - KILLS", new Location(Bukkit.getWorld("world"), 7.5, 123.5, -45.5),
							SimpleHologram.class);
				}
				
				hologram.addLine("§eJogadores com as maiores");
				hologram.addLine("§equantidade de kills da arena!");
				hologram.addLine("");

				int index = 1;

				for (Object model : CommonGeneral.getInstance().getStatusData().ranking(StatusType.LOBBY, "kills")) {
					if (model instanceof NormalModel) {
						NormalModel normalModel = (NormalModel) model;

						Member member = CommonGeneral.getInstance().getMemberManager()
								.getMember(normalModel.getUniqueId());

						if (member == null) {
							try {
								MemberModel loaded = CommonGeneral.getInstance().getPlayerData()
										.loadMember(normalModel.getUniqueId());

								if (loaded == null) {
									CommonGeneral.getInstance()
											.debug("Não foi possível pegar as informações do jogador "
													+ normalModel.getUniqueId() + "!");
								} else {
									member = new MemberVoid(loaded);
								}

							} catch (Exception e) {
								CommonGeneral.getInstance().debug("Não foi possível pegar as informações do jogador "
										+ normalModel.getUniqueId() + "!");
							}
						}

						if (member != null) {
							hologram.addLine("§a" + index + "° §7- "
									+ ChatColor.getLastColors(Tag.valueOf(member.getGroup().name()).getPrefix())
									+ member.getPlayerName() + " §7- §3" + normalModel.getKills() + " kills");
						}
						index++;
					}
				}
			}
		}.runTaskTimer(LobbyMain.getInstance(), 0, 20 * 60 * 5);
	}

}
