package tk.yallandev.saintmc.kitpvp.hologram;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.hologramapi.hologram.Hologram;
import tk.yallandev.hologramapi.hologram.HologramBuilder;
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
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.hologram.HologramUpdateEvent;

public class RankingHologram implements Listener {

	private Hologram xpHologram;
	private Hologram killsHologram;

	public RankingHologram() {
		new BukkitRunnable() {

			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(new HologramUpdateEvent());
			}
		}.runTaskTimer(GameMain.getInstance(), 80, 20 * 60 * 30);
	}

	@EventHandler
	public void onLocationChange(LocationChangeEvent event) {
		if (event.getConfigName().equals("hologram-xp"))
			createXp();
		else if (event.getConfigName().equals("hologram-kills"))
			createKills();
	}

	@EventHandler
	public void onLocationChange(HologramUpdateEvent event) {
		createXp();
		createKills();
	}

	public void createXp() {
		if (xpHologram != null) {
			xpHologram.remove();
			BukkitMain.getInstance().getHologramController().unregisterHologram(xpHologram);
		}

		xpHologram = new HologramBuilder("§6§lRANKING - XP", new Location(Bukkit.getWorld("world"), 5.5, 177, 5.5))
				.setHologramClass(SimpleHologram.class).build();
		xpHologram.spawn();

		int index = 1;
		xpHologram.addLine("§eJogadores com os maiores rank e maior");
		xpHologram.addLine("§equantidade de xp do servidor!");
		xpHologram.addLine("");

		for (MemberModel memberModel : CommonGeneral.getInstance().getPlayerData().ranking("xp")) {
			MemberVoid memberVoid = new MemberVoid(memberModel);

			xpHologram.addLine("§a" + index + "° §7- "
					+ ChatColor.getLastColors(Tag.valueOf(memberVoid.getGroup().name()).getPrefix())
					+ memberModel.getPlayerName() + " §7- §7(" + memberModel.getLeague().getColor()
					+ memberModel.getLeague().getSymbol() + "§7) §7- §b" + memberModel.getXp());
			index++;
		}

		BukkitMain.getInstance().getHologramController().registerHologram(xpHologram);
	}

	public void createKills() {
		if (killsHologram != null) {
			killsHologram.remove();
			BukkitMain.getInstance().getHologramController().unregisterHologram(killsHologram);
		}

		killsHologram = new HologramBuilder("§6§lRANKING - KILLS",
				new Location(Bukkit.getWorld("world"), -5.5, 177, -5.5)).setHologramClass(SimpleHologram.class).build();
		killsHologram.spawn();

		killsHologram.addLine("§eJogadores com as maiores quantidades de");
		killsHologram.addLine("§ekills do servidor!");
		killsHologram.addLine("");

		int index = 1;

		for (Object model : CommonGeneral.getInstance().getStatusData().ranking(StatusType.PVP, "kills")) {
			if (model instanceof NormalStatus) {
				NormalStatus normalStatus = (NormalStatus) model;

				Member member = CommonGeneral.getInstance().getMemberManager().getMember(normalStatus.getUniqueId());

				if (member == null) {
					try {
						MemberModel loaded = CommonGeneral.getInstance().getPlayerData()
								.loadMember(normalStatus.getUniqueId());

						if (loaded == null) {
							CommonGeneral.getInstance().debug("Não foi possível pegar as informações do jogador "
									+ normalStatus.getUniqueId() + "!");
						} else {
							member = new MemberVoid(loaded);
						}

					} catch (Exception e) {
						CommonGeneral.getInstance().debug(
								"Não foi possível pegar as informações do jogador " + normalStatus.getUniqueId() + "!");
					}
				}

				if (member != null) {
					killsHologram.addLine("§a" + index + "° §7- "
							+ ChatColor.getLastColors(Tag.valueOf(member.getGroup().name()).getPrefix())
							+ member.getPlayerName() + " §7- §3" + normalStatus.getKills() + " kills");
				}
				index++;
			}
		}

		BukkitMain.getInstance().getHologramController().registerHologram(killsHologram);
	}

}
