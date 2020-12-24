package tk.yallandev.saintmc.kitpvp.hologram;

import org.bukkit.Bukkit;
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
import tk.yallandev.saintmc.common.account.status.types.challenge.ChallengeStatus;
import tk.yallandev.saintmc.common.account.status.types.challenge.ChallengeType;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalStatus;
import tk.yallandev.saintmc.common.clan.ClanModel;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.hologram.HologramUpdateEvent;

public class RankingHologram implements Listener {

	private Hologram clanHologram;
	private Hologram xpHologram;

	private Hologram killsHologram;
	private Hologram killStreakHologram;

	private Hologram fpsKillsHologram;
	private Hologram fpsKillstreakHologram;

	private Hologram easyHologram;
	private Hologram mediumHologram;
	private Hologram hardHologram;
	private Hologram hardcoreHologram;

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
		else if (event.getConfigName().equals("hologram-killstreak"))
			createKillstreak();
		else if (event.getConfigName().equals("hologram-fps-kills"))
			createFps();
		else if (event.getConfigName().equals("hologram-fps-killstreak"))
			createFpsKillStreak();
		else if (event.getConfigName().equals("hologram-easy"))
			createEasy();
		else if (event.getConfigName().equals("hologram-medio"))
			createMedium();
		else if (event.getConfigName().equals("hologram-hard"))
			createHard();
		else if (event.getConfigName().equals("hologram-hardcore"))
			createHardcore();
		else if (event.getConfigName().equals("hologram-clan"))
			createClan();
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

	private void createKillstreak() {
		if (killStreakHologram != null) {
			killStreakHologram.remove();
			BukkitMain.getInstance().getHologramController().unregisterHologram(killStreakHologram);
		}

		killStreakHologram = new HologramBuilder("§c§lRANKING -  KILLSTREAK",
				BukkitMain.getInstance().getLocationFromConfig("hologram-killstreak"))
						.setHologramClass(SimpleHologram.class).build();
		killStreakHologram.spawn();

		killStreakHologram.addLine("§eJogadores com as maiores quantidades de");
		killStreakHologram.addLine("§ekillstreak do servidor!");
		killStreakHologram.addLine("");

		int index = 1;

		for (Object model : CommonGeneral.getInstance().getStatusData().ranking(StatusType.PVP, "killstreak")) {
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
					killStreakHologram.addLine("§a" + index + "° §7- "
							+ ChatColor.getLastColors(Tag.valueOf(member.getGroup().name()).getPrefix())
							+ member.getPlayerName() + " §7- §3" + normalStatus.getKillstreak() + " killstreak");
				}
				index++;
			}
		}

		BukkitMain.getInstance().getHologramController().registerHologram(killStreakHologram);
	}

	private void createFpsKillStreak() {
		if (fpsKillstreakHologram != null) {
			fpsKillstreakHologram.remove();
			BukkitMain.getInstance().getHologramController().unregisterHologram(fpsKillstreakHologram);
		}

		fpsKillstreakHologram = new HologramBuilder("§c§lRANKING - FPS - KILLSTREAK",
				BukkitMain.getInstance().getLocationFromConfig("hologram-fps-killstreak"))
						.setHologramClass(SimpleHologram.class).build();
		fpsKillstreakHologram.spawn();

		fpsKillstreakHologram.addLine("§eJogadores com as maiores quantidades de");
		fpsKillstreakHologram.addLine("§ekillstreak do servidor!");
		fpsKillstreakHologram.addLine("");

		int index = 1;

		for (Object model : CommonGeneral.getInstance().getStatusData().ranking(StatusType.FPS, "killstreak")) {
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
					fpsKillstreakHologram.addLine("§a" + index + "° §7- "
							+ ChatColor.getLastColors(Tag.valueOf(member.getGroup().name()).getPrefix())
							+ member.getPlayerName() + " §7- §3" + normalStatus.getKillstreak() + " killstreak");
				}
				index++;
			}
		}

		BukkitMain.getInstance().getHologramController().registerHologram(fpsKillstreakHologram);
	}

	private void createFps() {
		if (fpsKillsHologram != null) {
			fpsKillsHologram.remove();
			BukkitMain.getInstance().getHologramController().unregisterHologram(fpsKillsHologram);
		}

		fpsKillsHologram = new HologramBuilder("§c§lRANKING - FPS - KILLS",
				BukkitMain.getInstance().getLocationFromConfig("hologram-fps-kills"))
						.setHologramClass(SimpleHologram.class).build();
		fpsKillsHologram.spawn();

		fpsKillsHologram.addLine("§eJogadores com as maiores quantidades de");
		fpsKillsHologram.addLine("§ekills da fps!");
		fpsKillsHologram.addLine("");

		int index = 1;

		for (Object model : CommonGeneral.getInstance().getStatusData().ranking(StatusType.FPS, "kills")) {
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
					fpsKillsHologram.addLine("§a" + index + "° §7- "
							+ ChatColor.getLastColors(Tag.valueOf(member.getGroup().name()).getPrefix())
							+ member.getPlayerName() + " §7- §3" + normalStatus.getKills() + " kills");
				}
				index++;
			}
		}

		BukkitMain.getInstance().getHologramController().registerHologram(fpsKillsHologram);
	}

	@EventHandler
	public void onLocationChange(HologramUpdateEvent event) {
		createFps();
		createKillstreak();
		createFpsKillStreak();
		createXp();
		createKills();
		createEasy();
		createMedium();
		createHard();
		createHardcore();
	}

	private void createEasy() {
		if (easyHologram != null) {
			easyHologram.remove();
			BukkitMain.getInstance().getHologramController().unregisterHologram(easyHologram);
		}

		easyHologram = new HologramBuilder("§a§lRANKING - LAVA - FACIL",
				BukkitMain.getInstance().getLocationFromConfig("hologram-easy")).setHologramClass(SimpleHologram.class)
						.build();
		easyHologram.spawn();

		easyHologram.addLine("");

		int index = 1;

		for (Object model : CommonGeneral.getInstance().getStatusData().ranking(StatusType.LAVA,
				"challengeInfo.EASY.wins")) {
			if (model instanceof ChallengeStatus) {
				ChallengeStatus challengeStatus = (ChallengeStatus) model;

				Member member = CommonGeneral.getInstance().getMemberManager().getMember(challengeStatus.getUniqueId());

				if (member == null) {
					try {
						MemberModel loaded = CommonGeneral.getInstance().getPlayerData()
								.loadMember(challengeStatus.getUniqueId());

						if (loaded == null) {
							CommonGeneral.getInstance().debug("Não foi possível pegar as informações do jogador "
									+ challengeStatus.getUniqueId() + "!");
						} else {
							member = new MemberVoid(loaded);
						}

					} catch (Exception e) {
						CommonGeneral.getInstance().debug("Não foi possível pegar as informações do jogador "
								+ challengeStatus.getUniqueId() + "!");
					}
				}

				if (member != null) {
					easyHologram.addLine("§a" + index + "° §7- "
							+ ChatColor.getLastColors(Tag.valueOf(member.getGroup().name()).getPrefix())
							+ member.getPlayerName() + " §7- §3Passou " + challengeStatus.getWins(ChallengeType.EASY)
							+ " vezes");
				}
				index++;
			}
		}

		BukkitMain.getInstance().getHologramController().registerHologram(easyHologram);
	}

	private void createMedium() {
		if (mediumHologram != null) {
			mediumHologram.remove();
			BukkitMain.getInstance().getHologramController().unregisterHologram(mediumHologram);
		}

		mediumHologram = new HologramBuilder("§e§lRANKING - LAVA - MEDIO",
				BukkitMain.getInstance().getLocationFromConfig("hologram-medio")).setHologramClass(SimpleHologram.class)
						.build();
		mediumHologram.spawn();

		mediumHologram.addLine("");

		int index = 1;

		for (Object model : CommonGeneral.getInstance().getStatusData().ranking(StatusType.LAVA,
				"challengeInfo.MEDIUM.wins")) {
			if (model instanceof ChallengeStatus) {
				ChallengeStatus challengeStatus = (ChallengeStatus) model;

				Member member = CommonGeneral.getInstance().getMemberManager().getMember(challengeStatus.getUniqueId());

				if (member == null) {
					try {
						MemberModel loaded = CommonGeneral.getInstance().getPlayerData()
								.loadMember(challengeStatus.getUniqueId());

						if (loaded == null) {
							CommonGeneral.getInstance().debug("Não foi possível pegar as informações do jogador "
									+ challengeStatus.getUniqueId() + "!");
						} else {
							member = new MemberVoid(loaded);
						}

					} catch (Exception e) {
						CommonGeneral.getInstance().debug("Não foi possível pegar as informações do jogador "
								+ challengeStatus.getUniqueId() + "!");
					}
				}

				if (member != null) {
					mediumHologram.addLine("§a" + index + "° §7- "
							+ ChatColor.getLastColors(Tag.valueOf(member.getGroup().name()).getPrefix())
							+ member.getPlayerName() + " §7- §3Passou " + challengeStatus.getWins(ChallengeType.MEDIUM)
							+ " vezes");
				}
				index++;
			}
		}

		BukkitMain.getInstance().getHologramController().registerHologram(mediumHologram);
	}

	private void createHard() {
		if (hardHologram != null) {
			hardHologram.remove();
			BukkitMain.getInstance().getHologramController().unregisterHologram(hardHologram);
		}

		hardHologram = new HologramBuilder("§c§lRANKING - LAVA - HARD",
				BukkitMain.getInstance().getLocationFromConfig("hologram-hard")).setHologramClass(SimpleHologram.class)
						.build();
		hardHologram.spawn();

		hardHologram.addLine("");

		int index = 1;

		for (Object model : CommonGeneral.getInstance().getStatusData().ranking(StatusType.LAVA,
				"challengeInfo.HARD.wins")) {
			if (model instanceof ChallengeStatus) {
				ChallengeStatus challengeStatus = (ChallengeStatus) model;

				Member member = CommonGeneral.getInstance().getMemberManager().getMember(challengeStatus.getUniqueId());

				if (member == null) {
					try {
						MemberModel loaded = CommonGeneral.getInstance().getPlayerData()
								.loadMember(challengeStatus.getUniqueId());

						if (loaded == null) {
							CommonGeneral.getInstance().debug("Não foi possível pegar as informações do jogador "
									+ challengeStatus.getUniqueId() + "!");
						} else {
							member = new MemberVoid(loaded);
						}

					} catch (Exception e) {
						CommonGeneral.getInstance().debug("Não foi possível pegar as informações do jogador "
								+ challengeStatus.getUniqueId() + "!");
					}
				}

				if (member != null) {
					hardHologram.addLine("§a" + index + "° §7- "
							+ ChatColor.getLastColors(Tag.valueOf(member.getGroup().name()).getPrefix())
							+ member.getPlayerName() + " §7- §3Passou " + challengeStatus.getWins(ChallengeType.HARD)
							+ " vezes");
				}
				index++;
			}
		}

		BukkitMain.getInstance().getHologramController().registerHologram(hardHologram);
	}

	private void createHardcore() {
		if (hardcoreHologram != null) {
			hardcoreHologram.remove();
			BukkitMain.getInstance().getHologramController().unregisterHologram(hardcoreHologram);
		}

		hardcoreHologram = new HologramBuilder("§4§lRANKING - LAVA - HARDCORE",
				BukkitMain.getInstance().getLocationFromConfig("hologram-hardcore"))
						.setHologramClass(SimpleHologram.class).build();
		hardcoreHologram.spawn();

		hardcoreHologram.addLine("");

		int index = 1;

		for (Object model : CommonGeneral.getInstance().getStatusData().ranking(StatusType.LAVA,
				"challengeInfo.HARDCORE.wins")) {
			if (model instanceof ChallengeStatus) {
				ChallengeStatus challengeStatus = (ChallengeStatus) model;

				Member member = CommonGeneral.getInstance().getMemberManager().getMember(challengeStatus.getUniqueId());

				if (member == null) {
					try {
						MemberModel loaded = CommonGeneral.getInstance().getPlayerData()
								.loadMember(challengeStatus.getUniqueId());

						if (loaded == null) {
							CommonGeneral.getInstance().debug("Não foi possível pegar as informações do jogador "
									+ challengeStatus.getUniqueId() + "!");
						} else {
							member = new MemberVoid(loaded);
						}

					} catch (Exception e) {
						CommonGeneral.getInstance().debug("Não foi possível pegar as informações do jogador "
								+ challengeStatus.getUniqueId() + "!");
					}
				}

				if (member != null) {
					hardcoreHologram.addLine("§a" + index + "° §7- "
							+ ChatColor.getLastColors(Tag.valueOf(member.getGroup().name()).getPrefix())
							+ member.getPlayerName() + " §7- §3Passou "
							+ challengeStatus.getWins(ChallengeType.HARDCORE) + " vezes");
				}
				index++;
			}
		}

		BukkitMain.getInstance().getHologramController().registerHologram(hardcoreHologram);
	}

	public void createXp() {
		if (xpHologram != null) {
			xpHologram.remove();
			BukkitMain.getInstance().getHologramController().unregisterHologram(xpHologram);
		}

		xpHologram = new HologramBuilder("§c§lRANKING - XP",
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

	public void createKills() {
		if (killsHologram != null) {
			killsHologram.remove();
			BukkitMain.getInstance().getHologramController().unregisterHologram(killsHologram);
		}

		killsHologram = new HologramBuilder("§c§lRANKING - KILLS",
				BukkitMain.getInstance().getLocationFromConfig("hologram-kills")).setHologramClass(SimpleHologram.class)
						.build();
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
