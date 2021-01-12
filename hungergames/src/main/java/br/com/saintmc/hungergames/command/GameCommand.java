package br.com.saintmc.hungergames.command;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.game.GameStartEvent;
import br.com.saintmc.hungergames.game.GameState;
import br.com.saintmc.hungergames.kit.KitType;
import br.com.saintmc.hungergames.listener.register.GameListener;
import br.com.saintmc.hungergames.scheduler.types.GameScheduler;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.api.vanish.VanishAPI;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

public class GameCommand implements CommandClass {

	@Command(name = "tempo", groupToUse = Group.MOD)
	public void tempoCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <tempo:stop>§f para alterar o tempo do jogo!");
			return;
		}

		if (args[0].equalsIgnoreCase("stop")) {
			GameGeneral.getInstance().setCountTime(!GameGeneral.getInstance().isCountTime());
			sender.sendMessage(" §a* §fVocê " + (GameGeneral.getInstance().isCountTime() ? "§aativou" : "§cdesativou")
					+ "§f o timer!");
			return;
		}

		long time;

		try {
			time = DateUtils.parseDateDiff(args[0], true);
		} catch (Exception e) {
			sender.sendMessage(" §c* §fO formato de tempo não é v§lido.");
			return;
		}

		int seconds = (int) Math.floor((time - System.currentTimeMillis()) / 1000);

		if (seconds >= 60 * 120)
			seconds = 60 * 120;

		sender.sendMessage(" §a* §fO tempo do jogo foi alterado para §a" + args[0] + "§f!");
		GameGeneral.getInstance().setTime(seconds);
	}

	@Command(name = "reviver", groupToUse = Group.ADMIN)
	public void reviverCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage("§cUso /" + cmdArgs.getLabel() + " <player> para executar esse comando.");
			return;
		}

		Player player = Bukkit.getPlayer(args[0]);

		if (player == null) {
			sender.sendMessage("§cO jogador não existe.");
			return;
		}

		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

		if (gamer == null) {
			sender.sendMessage("§cO jogador não existe.");
			return;
		}

		gamer.setDeathCause(null);
		gamer.setSpectator(false);
		gamer.setGamemaker(false);
		gamer.setTimeout(false);
		AdminMode.getInstance().setPlayer(player,
				CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));

		player.getInventory().clear();
		player.getInventory().addItem(new ItemStack(Material.COMPASS));
		player.setGameMode(GameMode.SURVIVAL);
		player.setAllowFlight(false);
		player.setHealth(20d);
		player.setFoodLevel(20);
		player.setExhaustion(1f);
	}

	@Command(name = "start", aliases = { "comecar", "iniciar" }, groupToUse = Group.MODPLUS)
	public void startCommand(CommandArgs args) {
		if (GameState.isPregame(GameGeneral.getInstance().getGameState())) {
			args.getSender().sendMessage(" §a* §fVocê iniciou o jogo!");

			GameGeneral.getInstance().setCountTime(true);
			GameGeneral.getInstance().setGameState(GameState.INVINCIBILITY);
			GameMain.getInstance().registerListener(new GameListener());
			Bukkit.getPluginManager().callEvent(new GameStartEvent());

			CommonGeneral.getInstance().getMemberManager()
					.broadcast("§7[INFO] O " + args.getSender().getName() + " iniciou a partida", Group.AJUDANTE);
		}
	}

	@Command(name = "game", aliases = { "help" })
	public void gameCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();

		sender.sendMessage(" ");
		sender.sendMessage("§7Tempo: §f" + StringUtils.format(GameGeneral.getInstance().getTime()));
		sender.sendMessage("§7Estágio: §f" + GameGeneral.getInstance().getGameState().name());
		sender.sendMessage(" ");

		if (cmdArgs.isPlayer()) {
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(sender.getUniqueId());

			if (GameMain.DOUBLEKIT) {
				sender.sendMessage("§7Kit 1: §a" + NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)));
				sender.sendMessage("§7Kit 2: §a" + NameUtils.formatString(gamer.getKitName(KitType.SECONDARY)));
			} else
				sender.sendMessage("§7Kit: §a" + NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)));

			sender.sendMessage("§7Kills: §a" + gamer.getMatchKills());
		}

		sender.sendMessage(" ");
		sender.sendMessage("§7Sala: §b" + GameMain.getInstance().getRoomId());
	}

	@Command(name = "feast")
	public void feastCommand(BukkitCommandArgs cmdArgs) {
		if (cmdArgs.isPlayer()) {
			Player player = cmdArgs.getPlayer();

			if (GameScheduler.feastLocation == null)
				player.sendMessage("§cO feast ainda não spawnou!");
			else {
				player.sendMessage("§aBussola apontando para o feast!");
				player.setCompassTarget(GameScheduler.feastLocation);
			}
		}
	}

	@Command(name = "spawn")
	public void spawnCommand(CommandArgs cmdArgs) {
		if (cmdArgs.isPlayer()) {
			if (GameGeneral.getInstance().getGameState().isPregame()) {
				Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();

				player.teleport(BukkitMain.getInstance().getLocationFromConfig("spawn"));
			}
		}
	}

	@Command(name = "spectator", aliases = { "spec" }, groupToUse = Group.AJUDANTE)
	public void spectatorCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();

		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

		gamer.setSpectatorsEnabled(!gamer.isSpectatorsEnabled());
		player.sendMessage(gamer.isSpectatorsEnabled() ? "§aVocê agora vê os jogadores no espectador!"
				: "§cVocê agora não vê mais os jogadores no espectador!");
		VanishAPI.getInstance().updateVanishToPlayer(player);
	}

}
