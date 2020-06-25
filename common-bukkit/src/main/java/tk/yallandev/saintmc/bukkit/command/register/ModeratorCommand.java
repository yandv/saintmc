package tk.yallandev.saintmc.bukkit.command.register;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Joiner;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandSender;
import tk.yallandev.saintmc.bukkit.event.restore.RestoreEvent;
import tk.yallandev.saintmc.bukkit.event.restore.RestoreInitEvent;
import tk.yallandev.saintmc.bukkit.event.restore.RestoreStopEvent;
import tk.yallandev.saintmc.bukkit.event.teleport.PlayerTeleportCommandEvent;
import tk.yallandev.saintmc.bukkit.event.teleport.PlayerTeleportCommandEvent.TeleportResult;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.profile.Profile;
import tk.yallandev.saintmc.common.utils.DateUtils;

@SuppressWarnings("deprecation")
public class ModeratorCommand implements CommandClass {

	private DecimalFormat locationFormater = new DecimalFormat("######.##");

	@Command(name = "gamemode", aliases = { "gm" }, groupToUse = Group.MOD)
	public void gamemodeCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player player = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(" §e* §fUse §a/gamemode <gamemode>§f para alterar seu gamemode!");
			return;
		}

		GameMode gamemode = null;

		try {
			gamemode = GameMode.valueOf(args[0].toUpperCase());
		} catch (Exception e) {
			try {
				gamemode = GameMode.getByValue(Integer.parseInt(args[0]));
			} catch (Exception ex) {
				player.sendMessage(" §c* §fO gamemode §c\"" + args[0] + "\"§f não foi encontrado!");
				return;
			}
		}

		String gamemodeName = gamemode == GameMode.SURVIVAL ? "Sobrevivência"
				: gamemode == GameMode.ADVENTURE ? "Aventura"
						: gamemode == GameMode.SPECTATOR ? "Espectador" : "Criativo";

		if (args.length == 1) {
			if (player.getGameMode() != gamemode) {
				player.setGameMode(gamemode);
				player.sendMessage(" §a* §fVocê alterou seu gamemode para §a" + gamemodeName + "§f!");
			} else {
				player.sendMessage(" §c* §fVocê já está nesse gamemode!");
			}

			return;
		}

		Player target = Bukkit.getPlayer(args[1]);

		if (target == null) {
			player.sendMessage(" §c* §fO jogador §a\"" + args[1] + "\"§f não existe!");
			return;
		}

		if (target.getGameMode() != gamemode) {
			target.setGameMode(gamemode);
			player.sendMessage(
					" §a* §fVocê alterou gamemode de §a" + target.getName() + "§f para §a" + gamemodeName + "§f!");
		} else {
			player.sendMessage(" §d* §fO §a" + target.getName() + "§f já está nesse gamemode§f!");
		}
	}

	@Command(name = "restore", groupToUse = Group.MOD)
	public void restoreCommand(BukkitCommandArgs cmdArgs) {
		boolean restore = !BukkitMain.getInstance().getServerConfig().isRestoreMode();

		RestoreEvent event = restore
				? new RestoreInitEvent(CommonGeneral.getInstance().getMemberManager().getMembers().stream()
						.map(member -> new Profile(((BukkitMember) member).getPlayerName(),
								((BukkitMember) member).getUniqueId()))
						.collect(Collectors.toList()))
				: new RestoreStopEvent();
		Bukkit.getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			BukkitMain.getInstance().getServerConfig().setRestoreMode(restore);
			Bukkit.broadcastMessage(
					restore ? "§aO modo restauração foi ativado!" : "§cO modo restauração foi desativado!");
		}
	}

	@Command(name = "clear", groupToUse = Group.YOUTUBERPLUS)
	public void clear(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player player = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			player.getInventory().clear();
			player.getInventory().setArmorContents(new ItemStack[4]);
			player.getActivePotionEffects().clear();
			player.getInventory().setHeldItemSlot(0);
			player.sendMessage(" §a* §fVocê limpou o seu inventário!");
			return;
		}

		Player target = Bukkit.getPlayer(args[0]);

		if (target == null) {
			player.sendMessage(" §c* §fO jogador §a\"" + args[0] + "\"§f não existe!");
			return;
		}

		target.getInventory().clear();
		target.getInventory().setArmorContents(new ItemStack[4]);
		target.getActivePotionEffects().clear();
		target.getInventory().setHeldItemSlot(0);
		target.sendMessage(" §e* §fO seu inventário foi limpo pelo §a" + player.getName() + "§f!");
		player.sendMessage(" §a* §fVocê limpou o inventário de §a" + target.getName() + "§f!");
	}

	@Command(name = "enchant", usage = "/<command> <enchanment> <level>", groupToUse = Group.MOD)
	public void enchant(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player player = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(" §e* §fUtilize §a/enchant <encantamento> <level>§f para alterar seu gamemode!");
			return;
		}

		ItemStack item = player.getItemInHand();

		if (item == null || item.getType() == Material.AIR) {
			player.sendMessage(" §c* §fVocê não está com nada na sua mão para encantar!");
			return;
		}

		Enchantment enchantment = Enchantment.getByName(args[0].toUpperCase());

		if (enchantment == null) {
			Integer id = null;

			try {
				id = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				player.sendMessage(" §c* §fO formato de numero é inválido!");
				return;
			}

			enchantment = Enchantment.getById(id);
		}

		Integer level = 1;

		if (args.length >= 2) {
			try {
				level = Integer.valueOf(args[1]);
			} catch (NumberFormatException e) {
			}

			if (level < 1) {
				player.sendMessage(" §c* §fO nível de encantamento é muito baixo!");
				return;
			}
		}

		item.addUnsafeEnchantment(enchantment, level);
		player.sendMessage(" §a* §fVocê aplicou o encantamento §a" + enchantment.getName() + "§f no nível §a" + level
				+ "§f na sua §a" + item.getType().toString() + "§f!");
	}

	@Command(name = "whitelist", groupToUse = Group.MODPLUS, runAsync = true)
	public void whitelistCommand(CommandArgs cmdArgs) {

		tk.yallandev.saintmc.common.command.CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §e* §fUse §a/whitelist <on:off:list:add:remove>§f ");
			return;
		}

		// on/off/list/add/remove

		switch (args[0].toLowerCase()) {
		case "on": {

			if (BukkitMain.getInstance().getServerConfig().isWhitelist()) {
				sender.sendMessage("§cO servidor já está com a whitelist ativada!");
			} else {
				BukkitMain.getInstance().getServerConfig().setWhitelist(true);
				sender.sendMessage("§aVocê ativou a whitelist!");

				new BukkitRunnable() {
					@Override
					public void run() {
						CommonGeneral.getInstance().getServerData().setJoinEnabled(false);
					}
				}.runTaskAsynchronously(BukkitMain.getInstance());
			}

			break;
		}
		case "off": {
			if (!BukkitMain.getInstance().getServerConfig().isWhitelist()) {
				sender.sendMessage("§cO servidor já está com a whitelist desativada!");
			} else {
				BukkitMain.getInstance().getServerConfig().setWhitelist(false);
				sender.sendMessage("§cVocê desativou a whitelist!");

				new BukkitRunnable() {
					@Override
					public void run() {
						CommonGeneral.getInstance().getServerData().setJoinEnabled(true);
					}
				}.runTaskAsynchronously(BukkitMain.getInstance());
			}

			break;
		}
		case "add":
		case "remove": {
			if (args.length == 1) {
				sender.sendMessage(" §e*Use §f §a/whitelist <on:off:list:add:remove>§f ");
				break;
			}

			if (args[1].equalsIgnoreCase("all")) {

				List<Profile> profileList = new ArrayList<>();

				if (args[0].equalsIgnoreCase("add"))
					for (Member member : CommonGeneral.getInstance().getMemberManager().getMembers()) {
						Profile profile = new Profile(member.getPlayerName(), member.getUniqueId());

						if (BukkitMain.getInstance().getServerConfig().addWhitelist(profile))
							profileList.add(profile);
					}
				else
					for (Member member : CommonGeneral.getInstance().getMemberManager().getMembers()) {
						Profile profile = new Profile(member.getPlayerName(), member.getUniqueId());

						if (BukkitMain.getInstance().getServerConfig().removeWhitelist(profile))
							profileList.add(profile);
					}

				sender.sendMessage(
						(args[0].equalsIgnoreCase("add") ? "§aVocê adicionou " : "§cVocê removeu ")
								+ (profileList.size() <= 5
										? Joiner.on(", ")
												.join(profileList.stream().map(Profile::getPlayerName)
														.collect(Collectors.toList()))
										: profileList.size() + " jogadores")
								+ " na whitelist!");
				return;
			}

			String playerName = args[1];
			UUID uniqueId = CommonGeneral.getInstance().getUuid(playerName);

			Profile profile = new Profile(playerName, uniqueId);

			if (args[0].equalsIgnoreCase("add")) {
				BukkitMain.getInstance().getServerConfig().addWhitelist(profile);
				CommonGeneral.getInstance().getServerData().addWhitelist(profile);
				sender.sendMessage("§a" + args[1] + " adicionado na whitelist!");
			} else {
				BukkitMain.getInstance().getServerConfig().removeWhitelist(profile);
				CommonGeneral.getInstance().getServerData().removeWhitelist(profile);
				sender.sendMessage("§a" + args[1] + " removido na whitelist!");
			}
			break;
		}
		default: {
			sender.sendMessage(" §e* §fUse §a/whitelist <on:off:list:add:remove>§f ");
			break;
		}
		}
	}

	@Command(name = "blacklist", groupToUse = Group.MODPLUS, runAsync = true)
	public void blacklistCommand(CommandArgs cmdArgs) {

		tk.yallandev.saintmc.common.command.CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(
					" §e* §fUse §a/blacklist <add:remove>§f para um jogador não consegui mais entrar nesse servidor");
			return;
		}

		// on/off/list/add/remove

		switch (args[0].toLowerCase()) {
		case "add":
		case "remove": {
			if (args.length == 1) {
				sender.sendMessage(" §e*Use §f §a/whitelist <on:off:list:add:remove>§f ");
				break;
			}

			String playerName = args[1];
			UUID uniqueId = CommonGeneral.getInstance().getUuid(playerName);

			Profile profile = new Profile(playerName, uniqueId);

			if (args[0].equalsIgnoreCase("add")) {
				long time = System.currentTimeMillis() + (1000 * 60 * 60 * 12);

				if (args.length >= 3)
					time = DateUtils.getTime(args[1]);

				BukkitMain.getInstance().getServerConfig().blacklist(profile, time);
				sender.sendMessage("§a" + args[1] + " adicionado na blacklist!");
			} else {
				BukkitMain.getInstance().getServerConfig().unblacklist(profile);
				sender.sendMessage("§a" + args[1] + " removido na blacklist!");
			}
			break;
		}
		default: {
			sender.sendMessage(
					" §e* §fUse §a/blacklist <add:remove>§f para um jogador não consegui mais entrar nesse servidor");
			break;
		}
		}
	}

	@Command(name = "effect", usage = "/<command> <effect> <duration> <amplifier>", groupToUse = Group.MOD)
	public void effect(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player sender = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length < 2) {
			sender.sendMessage(
					" §e* §fUtilize §a/effect <player> <efeito> <duração> <intensidade>§f para aplicar um efeito em alguém!");
			return;
		}

		Player player = sender.getServer().getPlayer(args[0]);

		if (player == null) {
			sender.sendMessage(" §c* §fO jogador §a\"" + args[0] + "\"§f não existe!");
			return;
		}

		if (args[1].equalsIgnoreCase("clear")) {
			for (PotionEffect effect : player.getActivePotionEffects()) {
				player.removePotionEffect(effect.getType());
			}

			sender.sendMessage(" §a* §fO jogador §a" + player.getName() + "§f teve seus efeitos removidos!");
			return;
		}

		PotionEffectType effect = PotionEffectType.getByName(args[1].toUpperCase());

		if (effect == null) {
			Integer potionId = null;

			try {
				potionId = Integer.valueOf(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage(" §c* §fO efeito §a\"" + args[1] + "\"§f não existe!");
				return;
			}

			effect = PotionEffectType.getById(potionId);
		}

		if (effect == null) {
			sender.sendMessage(" §c* §fO efeito §a\"" + args[1] + "\"§f não existe!");
			return;
		}

		Integer duration = null;

		try {
			duration = Integer.valueOf(args[2]);
		} catch (NumberFormatException e) {
			sender.sendMessage(" §c* §fO formato de numero é inválido!");
			return;
		}

		Integer amplification = null;

		try {
			amplification = Integer.valueOf(args[3]);
		} catch (NumberFormatException e) {
			sender.sendMessage(" §c* §fO formato de numero é inválido!");
			return;
		}

		if (duration == 0) {
			if (!player.hasPotionEffect(effect)) {
				sender.sendMessage(" §c* §fO jogador não tem o efeito para ele ser removido!");
				return;
			}

			player.removePotionEffect(effect);
			sender.sendMessage(" §a* §fO jogador §a" + player.getName() + "§f teve o efeito §a" + effect.getName()
					+ "§f removido!");
		} else {
			PotionEffect applyEffect = new PotionEffect(effect, duration * 20, amplification);
			player.addPotionEffect(applyEffect, true);
			sender.sendMessage(" §a* §fO jogador §a" + player.getName() + "§f teve o efeito §a" + effect.getName()
					+ "§f adicionado §e(" + duration + " segundos e nível " + amplification + ")");
		}
	}

	@Command(name = "worldteleport", groupToUse = Group.DONO, aliases = { "tpworld", "tpworld" }, runAsync = false)
	public void worldteleport(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		if (cmdArgs.getArgs().length == 0) {
			cmdArgs.getSender().sendMessage(" §e* §fUtilize §a/tpworld <world>§f para mudar de mundo!");
			return;
		}

		World world = Bukkit.getWorld(cmdArgs.getArgs()[0]);

		if (world == null) {
			cmdArgs.getSender().sendMessage(" §e* §fO mundo está sendo carregado, aguarde!");

			WorldCreator worldCreator = new WorldCreator(cmdArgs.getArgs()[0].toLowerCase());

			worldCreator.type(WorldType.FLAT);
			worldCreator.generatorSettings("0;0");

			world = BukkitMain.getInstance().getServer().createWorld(worldCreator);

			world.setDifficulty(Difficulty.EASY);
			world.setGameRuleValue("doDaylightCycle", "false");

			CommonGeneral.getInstance().getLogger()
					.info("The world " + cmdArgs.getArgs()[0] + " has loaded successfully.");
			return;
		}

		cmdArgs.getPlayer().teleport(new Location(world, 0, 10, 0));
		cmdArgs.getSender().sendMessage(" §a* §fTeletransportado com sucesso!");
	}

	@Command(name = "teleport", aliases = { "tp", "teleportar" }, runAsync = false)
	public void teleport(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player p = cmdArgs.getPlayer();
		TeleportResult result = TeleportResult.NO_PERMISSION;
		String[] args = cmdArgs.getArgs();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

		if (!member.hasGroupPermission(Group.TRIAL)) {
			result = TeleportResult.NO_PERMISSION;
		} else {
			if (member.hasGroupPermission(Group.MOD)) {
				result = TeleportResult.ALLOWED;
			} else {
				result = TeleportResult.ONLY_PLAYER_TELEPORT;
			}
		}

		PlayerTeleportCommandEvent event = new PlayerTeleportCommandEvent(p, result);
		Bukkit.getPluginManager().callEvent(event);

		if (event.getResult() == TeleportResult.NO_PERMISSION || event.isCancelled()) {
			p.sendMessage(" ");
			p.sendMessage(" §c* §fVocê não tem §cpermissão§f para executar esse comando!");
			p.sendMessage(" ");
			return;
		}

		if (args.length == 0) {
			p.sendMessage(" §e* §fUtilize §a/tp <player> <player>§f para teletransportar jogadores!");
			return;
		}

		if (args.length == 1 || event.getResult() == TeleportResult.ONLY_PLAYER_TELEPORT) {
			Player t = Bukkit.getPlayer(args[0]);

			if (t == null) {
				p.sendMessage(" §c* §fO jogador §a\"" + args[0] + "\"§f não existe!");
				return;
			}

			p.teleport(t.getLocation());
			p.sendMessage(" §a* §fVocê se teletransportou até o §a" + t.getName() + "§f!");
			return;
		}

		if (args.length == 2 && event.getResult() == TeleportResult.ALLOWED) {
			Player player = Bukkit.getPlayer(args[0]);

			if (player == null) {
				p.sendMessage(" §c* §fO jogador §a\"" + args[0] + "\"§f não existe!");
				return;
			}

			Player target = Bukkit.getPlayer(args[1]);

			if (target == null) {
				p.sendMessage(" §c* §fO jogador §a\"" + args[1] + "\"§f não existe!");
				return;
			}

			player.teleport(target);
			p.sendMessage(" §a* §fVocê teletransportou §a" + player.getName() + "§f até §a" + target.getName() + "§f!");
			return;
		}

		if (args.length >= 3 && (event.getResult() == TeleportResult.ONLY_PLAYER_TELEPORT
				|| event.getResult() == TeleportResult.ALLOWED)) {
			if (args.length == 3) {
				Location loc = getLocationBased(p.getLocation(), args[0], args[1], args[2]);

				if (loc == null) {
					p.sendMessage(" §c* §fLocalização inválida!");
					return;
				}

				p.teleport(loc);
				p.sendMessage(" §a* §fVocê se teletransportou até §a%x%§f, §a%y%§f, §a%z%§f!"
						.replace("%x%", locationFormater.format(loc.getX()))
						.replace("%y%", locationFormater.format(loc.getY()))
						.replace("%z%", locationFormater.format(loc.getZ())));
				return;
			}

			Player target = Bukkit.getPlayer(args[1]);

			if (target == null) {
				p.sendMessage(" §c* §fO jogador §a\"" + args[1] + "\"§f não existe!");
				return;
			}

			Location loc = getLocationBased(target.getLocation(), args[1], args[2], args[3]);

			if (loc == null) {
				p.sendMessage(" §c* §fLocalização inválida!");
				return;
			}

			target.teleport(loc);
			p.sendMessage(" §a* §fVocê se teletransportou até §a%x%§f, §a%y%§f, §a%z%§f!"
					.replace("%x%", locationFormater.format(loc.getX()))
					.replace("%y%", locationFormater.format(loc.getY()))
					.replace("%z%", locationFormater.format(loc.getZ())).replace("%target%", target.getName()));
		}

		// TODO: ALERT STAFFS
	}

	@Command(name = "teleportall", aliases = { "tpall" }, groupToUse = Group.MOD)
	public void tpall(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player player = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			int i = 0;

			for (Player on : Bukkit.getOnlinePlayers()) {
				if (on != null && on.isOnline() && on.getUniqueId() != player.getUniqueId()) {
					on.teleport(player.getLocation());
					on.setFallDistance(0.0F);
					on.sendMessage(" §a* §fVocê foi teletransportado até o §a" + player.getName() + "§f!");
					i++;
				}
			}

			player.sendMessage(" §aVocê puxou todos os " + i + " jogadores até você!");
			return;
		}

		Player target = Bukkit.getPlayer(args[0]);

		if (target == null) {
			player.sendMessage(" §c* §fO jogador §a\"" + args[0] + "\"§f não existe!");
			return;
		}
		int i = 0;

		for (Player on : Bukkit.getOnlinePlayers()) {
			if (on != null && on.isOnline() && on.getUniqueId() != target.getUniqueId()
					&& on.getUniqueId() != player.getUniqueId()) {
				on.teleport(target.getLocation());
				on.setFallDistance(0.0F);
				on.sendMessage(" §a* §fVocê foi teletransportado até o §a" + target.getName() + "§f!");
				i++;
			}
		}

		player.sendMessage(" §aVocê levou todos os " + i + " jogadores até " + target.getName() + "!");
		return;

		// TODO: ALERT STAFFS
	}

	@Command(name = "kick", aliases = { "kickar" }, groupToUse = Group.TRIAL)
	public void kick(BukkitCommandArgs cmdArgs) {
		CommandSender sender = ((BukkitCommandSender) cmdArgs.getSender()).getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length < 1) {
			sender.sendMessage(" §e* §fUtilize §a/" + cmdArgs.getLabel() + " <player> <motivo>§f para kickar alguém!");
			return;
		}

		Player target = BukkitMain.getInstance().getServer().getPlayer(args[0]);

		if (target == null) {
			sender.sendMessage(" §c* §fO jogador §a\"" + args[0] + "\"§f não existe!");
			return;
		}

		boolean hasReason = false;
		StringBuilder builder = new StringBuilder();
		if (args.length > 1) {
			hasReason = true;
			for (int i = 1; i < args.length; i++) {
				String espaco = " ";
				if (i >= args.length - 1)
					espaco = "";
				builder.append(args[i] + espaco);
			}
		}

		if (!hasReason)
			builder.append("Sem motivo");

		for (Player player : Bukkit.getOnlinePlayers()) {
			Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

			if (!member.hasGroupPermission(Group.YOUTUBERPLUS))
				continue;

			player.sendMessage("\n§c* O jogador " + target.getName() + " foi kickado pelo " + sender.getName() + " por "
					+ builder.toString() + "\n§c ");
		}

		target.kickPlayer("§4§l" + CommonConst.KICK_PREFIX
				+ "\n§c\n§cSua conta foi expulsa do servidor!\n§f\n§7Motivo: §f" + builder.toString().trim());
	}

	private Location getLocationBased(Location loc, String argX, String argY, String argZ) {
		double x = 0;
		double y = 0;
		double z = 0;
		if (!argX.startsWith("~")) {
			try {
				x = Integer.parseInt(argX);
			} catch (Exception e) {
				return null;
			}
		} else {
			x = loc.getX();
			try {
				x += Integer.parseInt(argX.substring(1, argX.length()));
			} catch (Exception e) {
			}
		}
		if (!argY.startsWith("~")) {
			try {
				y = Integer.parseInt(argY);
			} catch (Exception e) {
				return null;
			}
		} else {
			y = loc.getY();
			try {
				y += Integer.parseInt(argY.substring(1, argY.length()));
			} catch (Exception e) {
			}
		}
		if (!argZ.startsWith("~")) {
			try {
				z = Integer.parseInt(argZ);
			} catch (Exception e) {
				return null;
			}
		} else {
			z = loc.getZ();
			try {
				z += Integer.parseInt(argZ.substring(1, argZ.length()));
			} catch (Exception e) {
			}
		}

		Location l = loc.clone();
		l.setX(x);
		l.setY(y);
		l.setZ(z);
		return l;
	}
}
