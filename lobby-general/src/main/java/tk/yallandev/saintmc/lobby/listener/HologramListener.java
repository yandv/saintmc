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
import tk.yallandev.saintmc.common.account.status.types.game.GameStatus;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalStatus;
import tk.yallandev.saintmc.common.clan.ClanModel;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.lobby.LobbyPlatform;

public class HologramListener implements Listener {

	private Hologram pvpHologram;
	private Hologram killsHologram;
	private Hologram clanHologram;
	private Hologram winsHologram;

	public HologramListener() {
		new BukkitRunnable() {

			@Override
			public void run() {
				createKills();
				createClan();
				createWins();
			}
		}.runTaskTimer(LobbyPlatform.getInstance().getPlugin(), 80, 20 * 60 * 10);
		createPvP();
	}

	@EventHandler
	public void onLocationChange(LocationChangeEvent event) {
		if (event.getConfigName().equals("hologram-lobby-kills"))
			createKills();
		else if (event.getConfigName().equals("hologram-pvp"))
			createPvP();
		else if (event.getConfigName().equals("hologram-clan"))
			createClan();
		else if (event.getConfigName().equals("hologram-hg-wins"))
			createWins();
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

	public void createClan() {
		if (clanHologram != null) {
			clanHologram.remove();
			clanHologram = null;
		}

		if (clanHologram == null) {
			clanHologram = BukkitMain.getInstance().getHologramController().createHologram("§c§lRANKING - CLAN",
					BukkitMain.getInstance().getLocationFromConfig("hologram-clan"), SimpleHologram.class);
		}

		clanHologram.addLine("§eClan com as maiores");
		clanHologram.addLine("§equantidade de xp do servidor!");
		clanHologram.addLine("");

		int index = 1;

		for (ClanModel clanModel : CommonGeneral.getInstance().getClanData().ranking("xp")) {
			clanHologram.addLine("§a" + index + "° §7- " + clanModel.getClanName() + "("
					+ clanModel.getClanAbbreviation() + ") §7- §3" + clanModel.getXp() + " xp");
			index++;
		}
	}

	public void createWins() {
		if (winsHologram != null) {
			winsHologram.remove();
			winsHologram = null;
		}

		if (winsHologram == null) {
			winsHologram = BukkitMain.getInstance().getHologramController().createHologram("§c§lRANKING - WINS HG",
					BukkitMain.getInstance().getLocationFromConfig("hologram-hg-wins"), SimpleHologram.class);
		}

		winsHologram.addLine("§eJogadores com as maiores");
		winsHologram.addLine("§equantidade de wins do servidor!");
		winsHologram.addLine("");

		int index = 1;

		for (Object model : CommonGeneral.getInstance().getStatusData().ranking(StatusType.HG, "wins")) {
			if (model instanceof GameStatus) {
				GameStatus gameStatus = (GameStatus) model;

				Member member = CommonGeneral.getInstance().getMemberManager().getMember(gameStatus.getUniqueId());

				if (member == null) {
					try {
						MemberModel loaded = CommonGeneral.getInstance().getPlayerData()
								.loadMember(gameStatus.getUniqueId());

						if (loaded == null) {
							CommonGeneral.getInstance().debug("Não foi possível pegar as informações do jogador "
									+ gameStatus.getUniqueId() + "!");
						} else {
							member = new MemberVoid(loaded);
						}

					} catch (Exception e) {
						CommonGeneral.getInstance().debug(
								"Não foi possível pegar as informações do jogador " + gameStatus.getUniqueId() + "!");
					}
				}

				if (member != null) {
					winsHologram.addLine("§a" + index + "° §7- "
							+ ChatColor.getLastColors(Tag.valueOf(member.getGroup().name()).getPrefix())
							+ member.getPlayerName() + " §7- §3" + gameStatus.getWins() + " wins");
				}
				index++;
			}
		}
	}

	public void createKills() {
		if (killsHologram != null) {
			killsHologram.remove();
			killsHologram = null;
		}

		if (killsHologram == null) {
			killsHologram = BukkitMain.getInstance().getHologramController().createHologram("§c§lRANKING - KILLS",
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