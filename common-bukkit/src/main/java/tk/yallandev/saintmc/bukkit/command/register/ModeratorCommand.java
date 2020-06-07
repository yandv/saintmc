package tk.yallandev.saintmc.bukkit.command.register;

import java.text.DecimalFormat;

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

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandSender;
import tk.yallandev.saintmc.bukkit.event.teleport.PlayerTeleportCommandEvent;
import tk.yallandev.saintmc.bukkit.event.teleport.PlayerTeleportCommandEvent.TeleportResult;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;

@SuppressWarnings("deprecation")
public class ModeratorCommand implements CommandClass {

	private DecimalFormat locationFormater = new DecimalFormat("######.##");

	@Command(name = "build", groupToUse = Group.MODGC)
	public void buildCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(cmdArgs.getPlayer().getUniqueId());

		member.setBuildEnabled(!member.isBuildEnabled());
		member.sendMessage(" §a* §fVocê " + (member.isBuildEnabled() ? "§aativou§f" : "§cdesativou§f")
				+ "§f o modo de construção!");
	}

	@Command(name = "gamemode", aliases = { "gm" }, groupToUse = Group.TRIAL)
	public void gamemode(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player p = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			p.sendMessage(" §e* §fUse §a/gamemode <gamemode>§f para alterar seu gamemode!");
			return;
		}

		GameMode gm = null;

		try {
			gm = GameMode.valueOf(args[0].toUpperCase());
		} catch (Exception e) {
			try {
				gm = GameMode.getByValue(Integer.parseInt(args[0]));
			} catch (Exception ex) {
				p.sendMessage(" §c* §fO gamemode §c\"" + args[0] + "\"§f não foi encontrado!");
				return;
			}
		}

		String gamemodeName = gm == GameMode.SURVIVAL ? "Sobrevivência"
				: gm == GameMode.ADVENTURE ? "Aventura" : gm == GameMode.SPECTATOR ? "Espectador" : "Criativo";

		if (args.length == 1) {
			if (p.getGameMode() != gm) {
				p.setGameMode(gm);
				p.sendMessage(" §a* §fVocê alterou seu gamemode para §a" + gamemodeName + "§f!");
			} else {
				p.sendMessage(" §c* §fVocê já está nesse gamemode!");
			}

			return;
		}

		Player t = Bukkit.getPlayer(args[1]);

		if (t == null) {
			p.sendMessage(" §c* §fO jogador §a\"" + args[1] + "\"§f não existe!");
			return;
		}

		if (t.getGameMode() != gm) {
			t.setGameMode(gm);
			p.sendMessage(" §a* §fVocê alterou gamemode de §a" + t.getName() + "§f para §a" + gamemodeName + "§f!");
		} else {
			p.sendMessage(" §d* §fO §a" + t.getName() + "§f já está nesse gamemode§f!");
		}
	}

	@Command(name = "clear", groupToUse = Group.YOUTUBERPLUS)
	public void clear(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player p = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			p.getInventory().clear();
			p.getInventory().setArmorContents(new ItemStack[4]);
			p.getActivePotionEffects().clear();
			p.getInventory().setHeldItemSlot(0);
			p.sendMessage(" §a* §fVocê limpou o seu inventário!");
			return;
		}

		Player t = Bukkit.getPlayer(args[0]);

		if (t == null) {
			p.sendMessage(" §c* §fO jogador §a\"" + args[0] + "\"§f não existe!");
			return;
		}

		t.getInventory().clear();
		t.getInventory().setArmorContents(new ItemStack[4]);
		t.getActivePotionEffects().clear();
		t.getInventory().setHeldItemSlot(0);
		t.sendMessage(" §e* §fO seu inventário foi limpo pelo §a" + p.getName() + "§f!");
		p.sendMessage(" §a* §fVocê limpou o inventário de §a" + t.getName() + "§f!");
	}

	@Command(name = "enchant", usage = "/<command> <enchanment> <level>", groupToUse = Group.MOD)
	public void enchant(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player p = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			p.sendMessage(" §e* §fUtilize §a/enchant <encantamento> <level>§f para alterar seu gamemode!");
			return;
		}

		ItemStack item = p.getItemInHand();

		if (item == null || item.getType() == Material.AIR) {
			p.sendMessage(" §c* §fVocê não está com nada na sua mão para encantar!");
			return;
		}

		Enchantment enchantment = Enchantment.getByName(args[0].toUpperCase());

		if (enchantment == null) {
			Integer id = null;

			try {
				id = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				p.sendMessage(" §c* §fO formato de numero é inválido!");
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
				p.sendMessage(" §c* §fO nível de encantamento é muito baixo!");
				return;
			}
		}

		item.addUnsafeEnchantment(enchantment, level);
		p.sendMessage(" §a* §fVocê aplicou o encantamento §a" + enchantment.getName() + "§f no nível §a" + level
				+ "§f na sua §a" + item.getType().toString() + "§f!");
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
		Member bp = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

		if (!bp.hasGroupPermission(Group.TRIAL)) {
			result = TeleportResult.NO_PERMISSION;
		} else {
			if (bp.hasGroupPermission(Group.MOD)) {
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

		Player p = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			int i = 0;

			for (Player on : Bukkit.getOnlinePlayers()) {
				if (on != null && on.isOnline() && on.getUniqueId() != p.getUniqueId()) {
					on.teleport(p.getLocation());
					on.setFallDistance(0.0F);
					on.sendMessage(" §a* §fVocê foi teletransportado até o §a" + p.getName() + "§f!");
					i++;
				}
			}

			p.sendMessage(" §aVocê puxou todos os " + i + " jogadores até você!");
			return;
		}

		Player t = Bukkit.getPlayer(args[0]);

		if (t == null) {
			p.sendMessage(" §c* §fO jogador §a\"" + args[0] + "\"§f não existe!");
			return;
		}
		int i = 0;

		for (Player on : Bukkit.getOnlinePlayers()) {
			if (on != null && on.isOnline() && on.getUniqueId() != t.getUniqueId()
					&& on.getUniqueId() != p.getUniqueId()) {
				on.teleport(t.getLocation());
				on.setFallDistance(0.0F);
				on.sendMessage(" §a* §fVocê foi teletransportado até o §a" + t.getName() + "§f!");
				i++;
			}
		}

		p.sendMessage(" §aVocê levou todos os " + i + " jogadores até " + t.getName() + "!");
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

		for (Player p : Bukkit.getOnlinePlayers()) {
			Member player = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

			if (!player.hasGroupPermission(Group.YOUTUBERPLUS))
				continue;

			p.sendMessage("\n§c* O jogador " + target.getName() + " foi kickado pelo " + sender.getName() + " por "
					+ builder.toString() + "\n§c ");
		}

		target.kickPlayer("§4§l" + CommonConst.KICK_PREFIX + "\n§c\n§cSua conta foi expulsa do servidor!\n§f\n§f");
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
