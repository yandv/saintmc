package tk.yallandev.saintmc.bukkit.command.register;

import java.lang.management.ManagementFactory;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import tk.yallandev.saintmc.BukkitConst;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandSender;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

public class ServerCommand implements CommandClass {

	@Command(name = "torneio", aliases = { "battlepenta" })
	public void eventoCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Member sender = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		if (sender.isOnCooldown("connect-command")) {
			sender.sendMessage("§cVocê precisa esperar mais "
					+ DateUtils.formatTime(sender.getCooldown("connect-command"), CommonConst.DECIMAL_FORMAT)
					+ "s para se conectar novamente!");
			return;
		}

		BukkitMain.getInstance().sendServer(((BukkitMember) sender).getPlayer(), ServerType.EVENTO);
		sender.setCooldown("connect-command", 4);
	}

	@Command(name = "shutdown", aliases = { "stop" }, groupToUse = Group.ADMIN)
	public void stop(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			Bukkit.getOnlinePlayers().forEach(player -> BukkitMain.getInstance().sendPlayerToLobby(player));
			Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitMain.getInstance(), () -> Bukkit.shutdown(), 10l);
			return;
		}

		Integer t = null;

		try {
			t = Integer.valueOf(args[0]);
		} catch (NumberFormatException e) {
			sender.sendMessage("§cO formato de numero é inválido!");
			return;
		}

		if (t < 5) {
			t = 5;
		}

		int time = t;

		StringBuilder builder = new StringBuilder();

		if (args.length == 1)
			builder.append("Não especificado");
		else
			for (int x = 1; x < args.length; x++)
				builder.append(args[x]).append(" ");

		new BukkitRunnable() {

			int seconds = time;

			@Override
			public void run() {

				if (seconds == 4) {
					Bukkit.getOnlinePlayers().forEach(player -> BukkitMain.getInstance().sendPlayerToLobby(player));
					return;
				}

				if (seconds <= 0) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitMain.getInstance(), () -> Bukkit.shutdown(),
							7l);
					return;
				}

				seconds--;
			}

		}.runTaskTimer(BukkitMain.getInstance(), 0, 20);
	}

	@Command(name = "time", groupToUse = Group.ADMIN)
	public void timeCommand(CommandArgs cmdArgs) {
		BukkitCommandSender sender = (BukkitCommandSender) cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /time <set:add:query> <value>");
			return;
		}

		if (args[0].equalsIgnoreCase("set")) {
			Integer value;

			if (args[1].equalsIgnoreCase("day")) {
				value = 0;
			} else if (args[1].equalsIgnoreCase("night")) {
				value = 12500;
			} else {
				value = Integer.valueOf(args[1]);

				if (value == null) {
					value = 0;
				}
			}

			for (World world : Bukkit.getWorlds())
				world.setTime(value);

			org.bukkit.command.Command.broadcastCommandMessage(sender.getSender(), "Set time to " + value);
		} else if (args[0].equalsIgnoreCase("add")) {
			Integer value = null;

			try {
				value = Integer.valueOf(args[1]);
			} catch (Exception ex) {
				value = 0;
			}

			for (World world : Bukkit.getWorlds())
				world.setFullTime(world.getFullTime() + value);

			org.bukkit.command.Command.broadcastCommandMessage(sender.getSender(), "Added " + value + " to time");
		} else {
			sender.sendMessage("Unknown method. Usage: ");
		}
	}

	@Command(name = "tps", aliases = { "ticks" }, groupToUse = Group.TRIAL)
	public void tpsCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();

		if (cmdArgs.getArgs().length == 0) {
			double tps = MinecraftServer.getServer().recentTps[0];
			double[] ticks = MinecraftServer.getServer().recentTps;

			String[] tpsAvg = new String[ticks.length];

			for (int i = 0; i < ticks.length; i++)
				tpsAvg[i] = format(ticks[i]);

			long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2L / 1048576L;
			long allocatedMemory = Runtime.getRuntime().totalMemory() / 1048576L;

			long totalEntities = Bukkit.getWorlds().stream().map(World::getEntities).mapToInt(List::size).count();

			sender.sendMessage("         ");
			sender.sendMessage("§7TPS (1m, 5m, 15m): " + ChatColor.GREEN + StringUtils.join(tpsAvg, ", "));
			sender.sendMessage("§7TPS: " + format(tps) + "/" + BukkitConst.TPS);
			sender.sendMessage(
					"§7Online §a" + Bukkit.getServer().getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
			sender.sendMessage("§7Memória §a" + usedMemory + "/" + allocatedMemory + " MB");
			sender.sendMessage("§7Uptime §a" + DateUtils.formatDifference(
					(System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime()) / 1000));

			Bukkit.getWorlds().forEach(world -> {
				sender.sendMessage("         ");
				sender.sendMessage("§aMundo " + world.getName());
				sender.sendMessage("  §7Entidades: §a" + totalEntities);
				sender.sendMessage("  §7Chunks carregadas: §a" + world.getLoadedChunks().length);
			});

			return;
		}

		if (cmdArgs.getArgs()[0].equalsIgnoreCase("gc")) {
			Runtime.getRuntime().gc();
			sender.sendMessage(ChatColor.GRAY + "GarbageCollector took the trash from server!");
		}
	}

	@Command(name = "memoryinfo", groupToUse = Group.ADMIN)
	public void memoryinfoCommand(CommandArgs cmdArgs) {
		long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2L / 1048576L;
		long allocatedMemory = Runtime.getRuntime().totalMemory() / 1048576L;

		cmdArgs.getSender().sendMessage("§a" + allocatedMemory + "MB de memoria RAM Maxima");
		cmdArgs.getSender().sendMessage("§a" + usedMemory + "MB de memoria RAM Usada");
		cmdArgs.getSender().sendMessage("§a" + (allocatedMemory - usedMemory) + "MB de memoria RAM Livre");
		cmdArgs.getSender().sendMessage("§a" + ((usedMemory * 100) / allocatedMemory) + "% da memoria RAM");
	}

	private String format(double tps) {
		return (tps > BukkitConst.TPS * 0.9d ? ChatColor.GREEN
				: (tps > BukkitConst.TPS * 0.8d ? ChatColor.YELLOW : ChatColor.RED)).toString()
				+ (tps > BukkitConst.TPS ? "*" : "") + Math.min(Math.round(tps * 100.0D) / 100.0D, BukkitConst.TPS);
	}
}
