package tk.yallandev.saintmc.kitpvp.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandFramework.Completer;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.warp.DuelWarp;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.types.PartyWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.ShadowWarp;

public class WarpCommand implements CommandClass {

	@Command(name = "warp")
	public void warpCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage("§cUso /warp <warpName> para ir até uma warp.");
			return;
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int x = 0; x < args.length; x++) {
			stringBuilder.append(args[x]).append(" ");
		}

		Warp warp = GameMain.getInstance().getWarpManager().getWarpByName(args[0]);

		if (warp == null) {
			player.sendMessage("§cA warp " + args[0] + " não existe!");
			return;
		}

		if (!warp.getWarpSettings().isWarpEnabled() && !Member.hasGroupPermission(player.getUniqueId(), Group.ADMIN)) {
			player.sendMessage("§cA warp " + warp.getName() + " está desativada!");
			return;
		}

		if (warp instanceof PartyWarp) {
			player.sendMessage("§cUse /evento para entrar nessa warp§f!");
			return;
		}

		GameMain.getInstance().getWarpManager().teleport(player, warp, -1);
	}

	@Command(name = "pvp", aliases = { "togglepvp" }, groupToUse = Group.MODPLUS)
	public void pvpCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length < 2) {
			player.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <warpName> <on/off>§f para ir até uma warp.");
			return;
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int x = 0; x < args.length; x++) {
			stringBuilder.append(args[x]).append(" ");
		}

		Warp warp = GameMain.getInstance().getWarpManager().getWarpByName(args[0]);

		if (warp == null) {
			player.sendMessage("§cA warp " + args[0] + " não existe!");
			return;
		}

		if (args[1].equalsIgnoreCase("on")) {
			if (warp.getWarpSettings().isPvpEnabled()) {
				player.sendMessage("§cO combate de jogadores já está §aativado§f!");
				return;
			}

			player.sendMessage(" §a* §fVocê §aativou§f o combate de jogadores§f!");
			warp.getWarpSettings().setPvpEnabled(true);
			GameMain.getInstance().getGamerManager().getGamers().stream().filter(gamer -> gamer.getWarp() == warp)
					.forEach(gamer -> gamer.getPlayer()
							.sendMessage("§6§l> §fO combate de jogadores foi §aativado§f na sua warp!"));
			CommonGeneral.getInstance().getMemberManager().getMembers().stream()
					.filter(member -> member.hasGroupPermission(Group.MOD)).forEach(member -> member
							.sendMessage("§7[" + player.getName() + " ativou o pvp da warp " + warp.getName() + "]"));
		} else if (args[1].equalsIgnoreCase("off")) {
			if (!warp.getWarpSettings().isPvpEnabled()) {
				player.sendMessage(" §c* §fO combate de jogadores já está §cdesativado§f!");
				return;
			}

			player.sendMessage(" §a* §fVocê §cdesativou§f o combate de jogadores§f!");
			warp.getWarpSettings().setPvpEnabled(false);
			GameMain.getInstance().getGamerManager().getGamers().stream().filter(gamer -> gamer.getWarp() == warp)
					.forEach(gamer -> gamer.getPlayer()
							.sendMessage("§6§l> §fO combate de jogadores foi §cdesativado§f na sua warp!"));
			CommonGeneral.getInstance().getMemberManager().getMembers().stream()
					.filter(member -> member.hasGroupPermission(Group.MOD)).forEach(member -> member.sendMessage(
							"§7[" + player.getName() + " desativou o pvp da warp " + warp.getName() + "]"));
		}
	}

	@Command(name = "damage", aliases = { "toggledamage" }, groupToUse = Group.MODPLUS)
	public void damageCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length < 2) {
			player.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <warpName> <on/off>§f para ir até uma warp.");
			return;
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int x = 0; x < args.length; x++) {
			stringBuilder.append(args[x]).append(" ");
		}

		Warp warp = GameMain.getInstance().getWarpManager().getWarpByName(args[0]);

		if (warp == null) {
			player.sendMessage(" §c* §fA warp §c" + args[0] + "§f não existe!");
			return;
		}

		if (args[1].equalsIgnoreCase("on")) {
			if (warp.getWarpSettings().isDamageEnabled()) {
				player.sendMessage(" §c* §fO dano já está §aativado§f!");
				return;
			}

			player.sendMessage(" §a* §fVocê §aativou§f o dano!");
			warp.getWarpSettings().setDamageEnabled(true);
			GameMain.getInstance().getGamerManager().getGamers().stream().filter(gamer -> gamer.getWarp() == warp)
					.forEach(gamer -> gamer.getPlayer().sendMessage("§6§l> §fO dano foi §aativado§f na sua warp!"));
			CommonGeneral.getInstance().getMemberManager().getMembers().stream()
					.filter(member -> member.hasGroupPermission(Group.MOD)).forEach(member -> member
							.sendMessage("§7[" + player.getName() + " ativou o dano da warp " + warp.getName() + "]"));
		} else if (args[1].equalsIgnoreCase("off")) {
			if (!warp.getWarpSettings().isDamageEnabled()) {
				player.sendMessage(" §c* §fO combate de jogadores já está §cdesativado§f!");
				return;
			}

			player.sendMessage(" §a* §fVocê §cdesativou§f o dano§f!");
			warp.getWarpSettings().setDamageEnabled(false);
			GameMain.getInstance().getGamerManager().getGamers().stream().filter(gamer -> gamer.getWarp() == warp)
					.forEach(gamer -> gamer.getPlayer().sendMessage("§6§l> §fO dano foi §cdesativado§f na sua warp!"));
			CommonGeneral.getInstance().getMemberManager().getMembers().stream()
					.filter(member -> member.hasGroupPermission(Group.MOD)).forEach(member -> member.sendMessage(
							"§7[" + player.getName() + " desativou o dano da warp " + warp.getName() + "]"));
		}
	}

	@Command(name = "spawn")
	public void spawnCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();
		GameMain.getInstance().getWarpManager().teleport(player,
				GameMain.getInstance().getWarpManager().getWarpByName("Spawn"), 5);
	}

	@Command(name = "setwarp", groupToUse = Group.TRIAL)
	public void setwarpCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player p = cmdArgs.getPlayer();

		if (!Member.hasGroupPermission(p.getUniqueId(), Group.ADMIN) && !Member.isGroup(p.getUniqueId(), Group.TRIAL)) {
			p.sendMessage(" §c* §fVocê não tem §cpermissão§f para executar esse comando!");
			return;
		}

		String[] a = cmdArgs.getArgs();

		if (a.length == 0) {
			p.sendMessage(" §e* §fUse §a/setwarp <warpName>§f para setar uma warp.");
			return;
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int x = 0; x < a.length; x++) {
			stringBuilder.append(a[x]).append(" ");
		}

		Warp warp = GameMain.getInstance().getWarpManager().getWarpByName(a[0]);

		if (warp == null) {
			p.sendMessage(" §c* §fA warp §c" + a[0] + "§f não existe!");
			return;
		}

		String configName = warp instanceof ShadowWarp ? "shadow" : warp.getName().toLowerCase();
		BukkitMain.getInstance().registerLocationInConfig(p.getLocation(), configName);
		warp.setSpawnLocation(p.getLocation());
		p.sendMessage(" §a* §fVocê setou a warp §a" + configName + "§f!");
	}

	@Command(name = "setprotection", groupToUse = Group.TRIAL)
	public void protectionCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();

		if (!Member.hasGroupPermission(player.getUniqueId(), Group.ADMIN)
				&& !Member.isGroup(player.getUniqueId(), Group.TRIAL)) {
			player.sendMessage(" §c* §fVocê não tem §cpermissão§f para executar esse comando!");
			return;
		}

		String[] args = cmdArgs.getArgs();

		if (args.length <= 1) {
			player.sendMessage(" §e* §fUse §a/setprotection <warpName> <double>§f para mudar a proteção de uma warp.");
			return;
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int x = 0; x < args.length; x++) {
			stringBuilder.append(args[x]).append(" ");
		}

		Warp warp = GameMain.getInstance().getWarpManager().getWarpByName(args[0]);

		if (warp == null) {
			player.sendMessage(" §c* §fA warp §c" + args[0] + "§f não existe!");
			return;
		}

		double protection = 0.0D;

		try {
			protection = Double.valueOf(args[1]);
		} catch (NumberFormatException ex) {
			return;
		}

		String configName = warp.getName().toLowerCase();
		GameMain.getInstance().registerProtectionInConfig(protection, configName);
		warp.setSpawnRadius(protection);
		player.sendMessage(" §a* §fVocê alterou a proteção da warp §a" + configName + "§f para " + protection + "!");
	}

	@Command(name = "setposition", groupToUse = Group.MODPLUS)
	public void setpositionCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (!Member.hasGroupPermission(player.getUniqueId(), Group.ADMIN)
				&& !Member.isGroup(player.getUniqueId(), Group.TRIAL)) {
			player.sendMessage(" §c* §fVocê não tem §cpermissão§f para executar esse comando!");
			return;
		}

		if (args.length <= 1) {
			player.sendMessage(" §e* §fUse §a/setposition <warp> <1/2>§f para setar uma posição da 1v1.");
			return;
		}

		Warp warp = GameMain.getInstance().getWarpManager().getWarpByName(args[0]);

		if (warp == null) {
			player.sendMessage(" §c* §fA warp §c" + args[0] + "§f não existe!");
			return;
		}

		if (!(warp instanceof DuelWarp)) {
			player.sendMessage(" §c* §fA warp §c" + args[0] + "§f não tem posições!");
			return;
		}

		if (!args[1].equals("1") && !args[1].equals("2")) {
			player.sendMessage(" §e* §fUse §a/setposition <warp> <1/2>§f para setar uma posição da 1v1.");
			return;
		}

		DuelWarp duelWarp = (DuelWarp) warp;

		if (args[1].equals("1")) {
			duelWarp.setFirstLocation(player.getLocation());

			if (duelWarp instanceof ShadowWarp)
				BukkitMain.getInstance().registerLocationInConfig(player.getLocation(), "shadow.pos1");
			else
				BukkitMain.getInstance().registerLocationInConfig(player.getLocation(), "sumo.pos1");

			player.sendMessage(" §a* §fVocê setou a posição §a1§f da warp §a" + warp.getName() + "§f!");
		} else {
			duelWarp.setSecondLocation(player.getLocation());

			if (duelWarp instanceof ShadowWarp)
				BukkitMain.getInstance().registerLocationInConfig(player.getLocation(), "shadow.pos2");
			else
				BukkitMain.getInstance().registerLocationInConfig(player.getLocation(), "sumo.pos2");

			player.sendMessage(" §a* §fVocê setou a posição §a2§f da warp §a" + warp.getName() + "§f!");
		}
	}

	@Completer(name = "warp", aliases = { "setprotection", "setwarp", "setposition" })
	public List<String> tabCompleter(CommandArgs cmdArgs) {
		String[] args = cmdArgs.getArgs();
		List<String> list = new ArrayList<>();

		if (args.length == 1) {
			if (cmdArgs.getArgs()[0].isEmpty()) {
				for (Warp warp : GameMain.getInstance().getWarpManager().getWarps())
					list.add(warp.getName().toLowerCase());
			} else {
				for (Warp warp : GameMain.getInstance().getWarpManager().getWarps())
					if (warp.getName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase()))
						list.add(warp.getName().toLowerCase());
			}
		}

		return list;
	}
}
