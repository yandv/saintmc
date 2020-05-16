package tk.yallandev.saintmc.game.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.GameType;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.constructor.Kit;
import tk.yallandev.saintmc.game.manager.KitManager;
import tk.yallandev.saintmc.game.manager.ServerManager;
import tk.yallandev.saintmc.game.manager.SimpleKitManager.SimpleKit;
import tk.yallandev.saintmc.game.stage.GameStage;

public class KitCommand implements CommandClass {

	@Command(name = "kit")
	public void kit(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player p = cmdArgs.getPlayer();
		String[] a = cmdArgs.getArgs();
		Gamer gamer = Gamer.getGamer(p.getUniqueId());

		if (!ServerManager.getInstance().isKit()) {
			p.sendMessage(" §c* §aTodos §fkits estão desabilitados!");
			return;
		}

		if (a.length == 0) {
			p.sendMessage(" §e* §fUse §a/kit <kit>§f para selecionar seu kit!");
			return;
		}

		Kit kit = KitManager.getKit(a[0]);

		if (kit == null) {
			p.sendMessage(" §c* §fO kit §a'" + a[0] + "' §fn§o existe!");
			return;
		}

		if (!kit.isActived()) {
			p.sendMessage(" §c* §fO kit §a" + kit.getName() + "§f est§ desabilitado!");
			return;
		}

		if (GameMain.getPlugin().getGameType() == GameType.HUNGERGAMES) {
			if (!GameStage.isPregame(GameMain.getPlugin().getGameStage())) {
				if (GameMain.getPlugin().getGameStage() == GameStage.INVINCIBILITY) {
					if (gamer.isNoKit()) {
						gamer.setNoKit(false);
					} else {
						p.sendMessage("§c *§fVoc§ §an§o§f pode mais selecionar kit!");
						return;
					}
				} else {
					if (GameMain.getPlugin().getTimer() <= 300) {
						if (gamer.isNoKit()) {
							gamer.setNoKit(false);
						}
					} else {
						p.sendMessage("§c *§fVoc§ §an§o§f pode mais selecionar kit!");
						return;
					}
				}
			}
		}

		GameMain.getPlugin().getKitManager().selectKit(p, kit);
	}

//	@Command(name = "givekit", groupToUse = Group.GERENTE)
//	public void givekit(BukkitCommandArgs cmdArgs) {
//		if (!cmdArgs.isPlayer())
//			return;
//
//		final CommandSender sender = cmdArgs.getSender();
//		final String[] args = cmdArgs.getArgs();
//
//		
//
//		if (args.length != 2) {
//			sender.sendMessage("§e *§f Use §a/givekit <player> <kit>§f para dar o kit para algum jogador!");
//			return;
//		}
//		
//
//		if (Bukkit.getPlayer(args[0]) == null) {
//			sender.sendMessage("§c *§f Este jogador est§ §aoff-line§f!"));
//			return;
//		}
//
//
//		if (player == null) {
//			try {
//				player = DataPlayer.getPlayer(uuid);
//			} catch (Exception e) {
//				e.printStackTrace();
//				sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), language, "cant-request-offline"));
//				return;
//			}
//			if (player == null) {
//				sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), language, "player-never-joined"));
//				return;
//			}
//		}
//
//		Kit kit = KitManager.getKit(args[1]);
//
//		if (kit == null) {
//			sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), language, "command-givekit-not-exist")
//					.replace("%kit%", NameUtils.formatString(args[1])));
//			return;
//		}
//
//		if (player.hasPermission("kit." + kit.getName().toLowerCase())) {
//			sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), language, "command-givekit-already-have-kit")
//					.replace("%kit%", NameUtils.formatString(args[1])));
//			return;
//		}
//
//		player.addPermission("kit." + kit.getName().toLowerCase());
//		sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), language, "command-givekit-success")
//				.replace("%kit%", NameUtils.formatString(kit.getName())).replace("%player%", player.getName()));
//	}

	@Command(name = "forcekit", groupToUse = Group.MODPLUS)
	public void forcekit(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player p = cmdArgs.getPlayer();
		String[] a = cmdArgs.getArgs();

		if (a.length <= 1) {
			p.sendMessage(" §e* §fUse §a/forcekit <player|all> <kit>");
			return;
		}

		if (a[0].equalsIgnoreCase("all")) {
			Kit kit = KitManager.getKit(a[1]);

			if (kit == null) {
				p.sendMessage(" §c* §fO kit §a'" + a[1] + "' §fn§o existe!");
				return;
			}

			for (Player player : Bukkit.getOnlinePlayers()) {
				GameMain.getPlugin().getKitManager().setKit(player, kit);
				player.sendMessage("§a * §fSeu kit foi alterado para §a" + kit.getName() + "§f!");
			}

			p.sendMessage("§a * §fVoc§ definiu o kit §a" + kit.getName() + " §fpara §atodos§f!");
			return;
		}

		Player player = Bukkit.getPlayer(a[0]);

		if (player == null) {
			p.sendMessage("§c *§f Este jogador est§ §aoff-line§f!");
			return;
		}

		Kit kit = KitManager.getKit(a[1]);

		if (kit == null) {
			p.sendMessage(" §c* §fO kit §a'" + a[1] + "' §fn§o existe!");
			return;
		}

		GameMain.getPlugin().getKitManager().setKit(player, kit);
		player.sendMessage("§a * §fSeu kit foi alterado para §a" + kit.getName() + "§f!");
		p.sendMessage("§a * §fVoc§ definiu o kit §a" + kit.getName() + " §fpara §a" + player.getName() + "§f!");
	}

	@Command(name = "togglekit", groupToUse = Group.MOD)
	public void togglekit(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player p = cmdArgs.getPlayer();
		String[] a = cmdArgs.getArgs();

		if (a.length == 0) {
			p.sendMessage("§e *§f Use §a/togglekit <on|off> <kit>");
			return;
		}

		if (a[0].equalsIgnoreCase("on")) {
			if (a.length == 1) {
				if (ServerManager.getInstance().isKit()) {
					p.sendMessage("§c *§f Os kits j§ est§o §ahabilitados§f!");
					return;
				}

				p.sendMessage("§a *§f Todos os kits foram habilitados!");
				// TODO broadcast
				ServerManager.getInstance().setKit(true);
			} else if (a.length == 2) {
				Kit kit = KitManager.getKit(a[1]);

				if (kit == null) {
					p.sendMessage(" §c* §fO kit §a'" + a[1] + "' §fn§o existe!");
					return;
				}

				if (kit.isActived()) {
					p.sendMessage("§c *§f O kit §a" + kit.getName() + "§f j§ est§ §ahabilitado§f!");
					return;
				}

				kit.setActived(true);
				// TODO broadcast
				p.sendMessage("§c *§f O kit §a" + kit.getName() + "§f foi §ahabilitado§f!");
				return;
			} else {
				p.sendMessage("§e *§f Use §a/togglekit <on|off> <kit>");
			}
		} else if (a[0].equalsIgnoreCase("off")) {
			if (a.length == 1) {
				if (!ServerManager.getInstance().isKit()) {
					p.sendMessage("§c *§f Os kits j§ est§o §adesabilitados§f!");
					return;
				}

				for (Player player : Bukkit.getOnlinePlayers()) {
					GameMain.getPlugin().getKitManager().unregisterPlayer(player);
				}

				p.sendMessage("§a *§f Todos os kits foram desabilitados!");
				// TODO broadcast
				ServerManager.getInstance().setKit(false);
			} else if (a.length == 2) {
				Kit kit = KitManager.getKit(a[1]);

				if (kit == null) {
					p.sendMessage(" §c* §fO kit §a'" + a[1] + "' §fn§o existe!");
					return;
				}

				if (!kit.isActived()) {
					p.sendMessage("§c *§f O kit §a'" + kit.getName() + "'§f j§ est§o §adesabilitados§f!");
					return;
				}

				for (Player player : Bukkit.getOnlinePlayers()) {
					if (!KitManager.getPlayersCurrentKit().containsKey(player.getUniqueId()))
						continue;

					if (KitManager.getPlayersCurrentKit().get(player.getUniqueId()).getName()
							.equalsIgnoreCase(kit.getName()))
						GameMain.getPlugin().getKitManager().unregisterPlayer(player);
				}

				kit.setActived(false);
				// TODO broadcast
				p.sendMessage("§a * §fVoc§ desabilitou o kit §a'" + kit.getName() + "'§f!");
				return;
			} else {
				p.sendMessage("§e *§f Use §a/togglekit <on|off> <kit>");
			}
		} else {
			p.sendMessage("§e *§f Use §a/togglekit <on|off> <kit>");
		}
	}

	@Command(name = "skit", groupToUse = Group.MOD)
	public void skit(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player p = cmdArgs.getPlayer();
		String[] a = cmdArgs.getArgs();
		Language lang = BattlePlayer.getLanguage(p.getUniqueId());

		String prefix = T.t(BukkitMain.getInstance(), lang, "command-skit-prefix") + " ";

		if (a.length <= 1) {
			p.sendMessage(T.t(BukkitMain.getInstance(), lang, "command-skit-usage").replace("%prefix%", prefix));
			return;
		}

		if (a[0].equalsIgnoreCase("criar") || a[0].equalsIgnoreCase("create")) {
			String name = a[1].toLowerCase();

			if (GameMain.getPlugin().getSimpleKitManager().loadKit(name, p)) {
				p.sendMessage(prefix
						+ T.t(BukkitMain.getInstance(), lang, "command-skit-created-success").replace("%name%", name));
			} else {
				p.sendMessage(prefix
						+ T.t(BukkitMain.getInstance(), lang, "command-skit-already-exist").replace("%name%", name));
			}

		} else if (a[0].equalsIgnoreCase("aplicar") || a[0].equalsIgnoreCase("apply")) {

			if (a.length == 2) {
				p.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-skit-usage"));
				return;
			}

			String name = a[1].toLowerCase();
			SimpleKit simpleKit = GameMain.getPlugin().getSimpleKitManager().getKit(name);

			if (simpleKit == null) {
				p.sendMessage(
						prefix + T.t(BukkitMain.getInstance(), lang, "command-skit-not-found").replace("%name%", a[1]));
				return;
			}

			if (a[2].equalsIgnoreCase("all")) {
				simpleKit.broadcast();
				p.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-skit-applied-success-all")
						.replace("%name%", a[1]));
			} else if (Bukkit.getPlayer(a[2]) != null) {
				Player player = Bukkit.getPlayer(a[2]);
				simpleKit.setPlayer(player);
				p.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-skit-applied-success-player")
						.replace("%name%", a[1]).replace("%player%", player.getName()));
			} else {
				Integer radius = null;

				try {
					radius = Integer.valueOf(a[1]);
				} catch (Exception e) {
					p.sendMessage(prefix
							+ T.t(BukkitMain.getInstance(), lang, "command-skit-not-found").replace("%name%", a[1]));
					return;
				}

				if (radius > 1200) {
					p.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-skit-radius-too-long"));
					return;
				}

				int count = 0;

				for (Entity entities : p.getNearbyEntities(radius, 100, radius)) {
					if (!(entities instanceof Player))
						continue;

					simpleKit.setPlayer((Player) entities);
					count++;
				}

				p.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-skit-applied-success-radius")
						.replace("%name%", a[1]).replace("%players%", "" + count));
			}
		}
	}

}
