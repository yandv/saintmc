package tk.yallandev.saintmc.bukkit.command.register;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.spigotmc.CustomTimingsHandler;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandSender;
import tk.yallandev.saintmc.bukkit.event.player.PlayerScoreboardStateEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

public class ServerCommand implements CommandClass {

	public static long timingStart = 0L;

	@Command(name = "score", aliases = { "scoreboard" })
	public void scoreboardCommand(BukkitCommandArgs args) {
		if (!args.isPlayer())
			return;

		Player player = args.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		member.getAccountConfiguration().setScoreboardEnabled(!member.getAccountConfiguration().isScoreboardEnabled());
		Bukkit.getServer().getPluginManager().callEvent(
				new PlayerScoreboardStateEvent(player, member.getAccountConfiguration().isScoreboardEnabled()));

		if (member.getAccountConfiguration().isScoreboardEnabled()) {
			Scoreboard score = ((BukkitMember) member).getScoreboard();

			if (score != null)
				score.createScoreboard(player);
		} else {
			Objective objective = player.getScoreboard().getObjective("clear");

			if (objective == null)
				objective = player.getScoreboard().registerNewObjective("clear", "dummy");

			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}

		player.sendMessage(" §a* §fSua scoreboard foi "
				+ (member.getAccountConfiguration().isScoreboardEnabled() ? "§aativada" : "§cdesativada") + "§f!");
	}

	@Command(name = "shutdown", aliases = { "stop" }, groupToUse = Group.ADMIN)
	public void stop(BukkitCommandArgs cmdArgs) {
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
			sender.sendMessage(" §c* §fO formato de numero é inválido!");
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

	@Command(name = "timings", groupToUse = Group.ADMIN)
	public void timingsCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §a* §fUse §a/timings <on:off:reset:report>§f");
			return;
		}

		if (args[0].equalsIgnoreCase("on")) {
			((SimplePluginManager) Bukkit.getPluginManager()).useTimings(true);
			CustomTimingsHandler.reload();
			sender.sendMessage("Enabled Timings & Reset");
			return;
		}

		if (args[0].equalsIgnoreCase("off")) {
			((SimplePluginManager) Bukkit.getPluginManager()).useTimings(false);
			sender.sendMessage("Disabled Timings");
			return;
		}

		if (!Bukkit.getPluginManager().useTimings()) {
			sender.sendMessage("Please enable timings by typing /timings on");
			return;
		}

		if (args[0].equals("reset")) {
			CustomTimingsHandler.reload();
			sender.sendMessage("Timings reset");
			return;
		}

		if (args[0].equalsIgnoreCase("merged") || args[0].equalsIgnoreCase("report")
				|| args[0].equalsIgnoreCase("paste")) {
			long sampleTime = System.nanoTime() - timingStart;

			File timingFolder = new File("timings");
			timingFolder.mkdirs();

			File timings = new File(timingFolder, "timings.txt");
			ByteArrayOutputStream bout = args[0].equals("paste") ? new ByteArrayOutputStream() : null;

			int index = 0;

			while (timings.exists())
				timings = new File(timingFolder, "timings" + ++index + ".txt");

			PrintStream fileTimings = null;

			try {
				fileTimings = args[0].equalsIgnoreCase("paste") ? new PrintStream(bout) : new PrintStream(timings);
				CustomTimingsHandler.printTimings(fileTimings);
				fileTimings.println("Sample time " + sampleTime + " (" + (sampleTime / 1.0E9D) + "s)");
				fileTimings.println("<spigotConfig>");
				fileTimings.println(Bukkit.spigot().getConfig().saveToString());
				fileTimings.println("</spigotConfig>");

				if (args[0].equalsIgnoreCase("paste")) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								HttpURLConnection con = (HttpURLConnection) (new URL(
										"https://timings.spigotmc.org/paste")).openConnection();
								con.setDoOutput(true);
								con.setRequestMethod("POST");
								con.setInstanceFollowRedirects(false);
								OutputStream out = con.getOutputStream();
								out.write(bout.toByteArray());
								out.close();
								JsonObject location = (JsonObject) (new Gson())
										.fromJson(new InputStreamReader(con.getInputStream()), JsonObject.class);
								con.getInputStream().close();
								String pasteID = location.get("key").getAsString();
								sender.sendMessage(ChatColor.GREEN
										+ "Timings results can be viewed at https://www.spigotmc.org/go/timings?url="
										+ pasteID);
							} catch (IOException ex) {
								sender.sendMessage(ChatColor.RED
										+ "Error pasting timings, check your console for more information");
								Bukkit.getServer().getLogger().log(Level.WARNING, "Could not paste timings", ex);
							}
						}
					}).start();
					return;
				}

				sender.sendMessage("Timings written to " + timings.getPath());
			} catch (IOException iOException) {

			} finally {
				if (fileTimings != null)
					fileTimings.close();
			}

			if (fileTimings != null)
				fileTimings.close();
		}
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

		sender = null;
		args = null;
	}

	@Command(name = "tps", aliases = { "ticks" }, groupToUse = Group.MODPLUS)
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

			World world = (sender instanceof Player) ? ((Player) sender).getWorld() : Bukkit.getWorlds().get(0);
			Chunk[] loadedChunks = world.getLoadedChunks();
			double lag = Math.round(((tps > 20 ? 20 : -tps) / 20.0D) * 100.0D);

			sender.sendMessage("         ");
			sender.sendMessage("§7TPS from last 1m, 5m, 15m: " + ChatColor.GREEN + StringUtils.join(tpsAvg, ", "));
			sender.sendMessage("§7Current TPS: " + format(tps) + ChatColor.GREEN + '/' + 20.0D);
			sender.sendMessage("§7Server Lag " + ChatColor.GREEN + (Math.round(lag * 10000.0D) / 10000.0D) + '%');
			sender.sendMessage("§7Online " + ChatColor.GREEN + Bukkit.getServer().getOnlinePlayers().size() + "/"
					+ Bukkit.getMaxPlayers());
			sender.sendMessage("§7Memory " + ChatColor.GREEN + usedMemory + "/" + allocatedMemory + " MB");
			sender.sendMessage("§7Uptime " + ChatColor.GREEN + DateUtils.formatDifference(
					(System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime()) / 1000));
			sender.sendMessage("         ");
			sender.sendMessage("§7Entities " + ChatColor.GREEN + totalEntities);
			sender.sendMessage(
					"§7Chunks Loaded " + ChatColor.GREEN + loadedChunks.length + " §7(" + world.getName() + "§7)");
			return;
		}

		if (cmdArgs.getArgs()[0].equalsIgnoreCase("gc")) {
			Runtime.getRuntime().gc();
			sender.sendMessage(ChatColor.GRAY + "GarbageCollector took the trash from server!");
		}
	}

	@Command(name = "memoryinfo", groupToUse = Group.DONO)
	public void memoryinfoCommand(BukkitCommandArgs cmdArgs) {
		double total = Runtime.getRuntime().maxMemory();
		double free = Runtime.getRuntime().freeMemory();
		double used = total - free;

		double divisor = 1024 * 1024 * 1024;
		double usedPercentage = (used / total) * 100;

		DecimalFormat format = new DecimalFormat("#.###");

		cmdArgs.getSender().sendMessage("§a" + format.format(total / divisor) + "GB de memoria RAM Maxima");

		cmdArgs.getSender().sendMessage("§a" + format.format(used / divisor) + "GB de memoria RAM Usada");
		cmdArgs.getSender().sendMessage("§a" + format.format(free / divisor) + "GB de memoria RAM Livre");
		cmdArgs.getSender().sendMessage("§a" + format.format(usedPercentage) + "% da memoria RAM");
	}

	@Command(name = "plugins", aliases = { "pl", "plugin" }, groupToUse = Group.LIGHT)
	public void pluginCommand(CommandArgs cmdArgs) {
		cmdArgs.getSender()
		.sendMessage("Plugins (" + Bukkit.getPluginManager().getPlugins().length + "): "
				+ Joiner.on("§f, ").join(Arrays.asList(Bukkit.getPluginManager().getPlugins()).stream().map(
						plugin -> (plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED) + plugin.getName())
						.collect(Collectors.toList())));
	}

	private String format(double tps) {
		return ((tps > 18.0D) ? ChatColor.GREEN : ((tps > 16.0D) ? ChatColor.YELLOW : ChatColor.RED)).toString()
				+ ((tps > 20.0D) ? "*" : "") + Math.min(Math.round(tps * 100.0D) / 100.0D, 20.0D);
	}
}
