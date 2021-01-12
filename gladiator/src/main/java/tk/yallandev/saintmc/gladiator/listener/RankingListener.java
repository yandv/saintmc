package tk.yallandev.saintmc.gladiator.listener;

import org.bukkit.ChatColor;
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
import tk.yallandev.saintmc.gladiator.GameMain;

public class RankingListener implements Listener {

	private Hologram xpHologram;
	private Hologram winsHologram;
	private Hologram killstreakHologram;

	public RankingListener() {
		new BukkitRunnable() {

			@Override
			public void run() {
				createXp();
				createWins();
				createKillstreak();
			}
		}.runTaskTimer(GameMain.getInstance(), 80, 20 * 60 * 10);
	}
	
	@EventHandler
	public void onLocationChange(LocationChangeEvent event) {
		if (event.getConfigName().equals("hologram-xp"))
			createXp();
		else if (event.getConfigName().equals("hologram-kills"))
			createWins();
		else if (event.getConfigName().equals("hologram-winstreak"))
			createKillstreak();
	}

	public void createXp() {
		if (xpHologram != null) {
			xpHologram.remove();
			BukkitMain.getInstance().getHologramController().unregisterHologram(xpHologram);
		}

		xpHologram = new HologramBuilder("§6§lRANKING - XP",
				BukkitMain.getInstance().getLocationFromConfig("hologram-xp")).setHologramClass(SimpleHologram.class)
						.build();
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

	public void createWins() {
		if (winsHologram != null) {
			winsHologram.remove();
			BukkitMain.getInstance().getHologramController().unregisterHologram(winsHologram);
		}

		winsHologram = new HologramBuilder("§6§lRANKING - WINS",
				BukkitMain.getInstance().getLocationFromConfig("hologram-kills")).setHologramClass(SimpleHologram.class)
						.build();
		winsHologram.spawn();

		winsHologram.addLine("§eJogadores com as maiores quantidades de");
		winsHologram.addLine("§ewins do gladiator!");
		winsHologram.addLine("");

		int index = 1;

		for (Object model : CommonGeneral.getInstance().getStatusData().ranking(StatusType.GLADIATOR, "kills")) {
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
					winsHologram.addLine("§a" + index + "° §7- "
							+ ChatColor.getLastColors(Tag.valueOf(member.getGroup().name()).getPrefix())
							+ member.getPlayerName() + " §7- §3" + normalModel.getKills() + " wins");
				}
				index++;
			}
		}

		BukkitMain.getInstance().getHologramController().registerHologram(winsHologram);
	}

	public void createKillstreak() {
		if (killstreakHologram != null) {
			killstreakHologram.remove();
			BukkitMain.getInstance().getHologramController().unregisterHologram(killstreakHologram);
		}

		killstreakHologram = new HologramBuilder("§6§lRANKING - WINSTREAK",
				BukkitMain.getInstance().getLocationFromConfig("hologram-winstreak"))
						.setHologramClass(SimpleHologram.class).build();
		killstreakHologram.spawn();

		killstreakHologram.addLine("§eJogadores com as maiores quantidades de");
		killstreakHologram.addLine("§ewinstreak do gladiator!");
		killstreakHologram.addLine("");

		int index = 1;

		for (Object model : CommonGeneral.getInstance().getStatusData().ranking(StatusType.GLADIATOR, "killstreak")) {
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
					killstreakHologram.addLine("§a" + index + "° §7- "
							+ ChatColor.getLastColors(Tag.valueOf(member.getGroup().name()).getPrefix())
							+ member.getPlayerName() + " §7- §3" + normalModel.getKillstreak() + " winstreak");
				}
				index++;
			}
		}

		BukkitMain.getInstance().getHologramController().registerHologram(killstreakHologram);
	}

}
