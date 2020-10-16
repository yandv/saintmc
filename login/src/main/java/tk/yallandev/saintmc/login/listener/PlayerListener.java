package tk.yallandev.saintmc.login.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.api.tablist.Tablist;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.login.LoginMain;

public class PlayerListener implements Listener {

	private static final Scoreboard SCOREBOARD = new SimpleScoreboard("§6§lLOGIN");
	private static final Tablist TABLIST = new Tablist(
			"\n§6§lSAINT§f§lMC\n§f\n§7Nome: §f%name% §9- §7Grupo: %group%\n§f                                                 §f",
			"\n§a" + CommonConst.SITE + "\n§b" + CommonConst.DISCORD.replace("http://", "") + "\n§f ") {

		@Override
		public String[] replace(Player player, String header, String footer) {
			Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

			header = header.replace("%group%", member.getGroup() == Group.MEMBRO ? "§7§lMEMBRO"
					: Tag.valueOf(member.getGroup().name()).getPrefix());
			header = header.replace("%name%", member.getPlayerName());

			footer = footer.replace("%name%", member.getPlayerName());
			footer = footer.replace(".br/", "");

			return new String[] { header, footer };
		}

	};

	{
		SCOREBOARD.blankLine(8);
		SCOREBOARD.setScore(7, new Score("§fLogue-se usando o", "2"));
		SCOREBOARD.setScore(6, new Score("§f/login <senha>", "1"));
		SCOREBOARD.blankLine(5);
		SCOREBOARD.setScore(4, new Score("§fRegistre-se usando", "3"));
		SCOREBOARD.setScore(3, new Score("§f/register <senha>", "4"));
		SCOREBOARD.blankLine(2);
		SCOREBOARD.setScore(1, new Score("§6" + CommonConst.SITE, "site"));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		player.sendMessage("§f");
		player.sendMessage("§f");
		event.setJoinMessage(null);

		player.teleport(BukkitMain.getInstance().getLocationFromConfig("spawn"));
		SCOREBOARD.createScoreboard(player);
		TABLIST.addViewer(player);

		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		new BukkitRunnable() {

			@Override
			public void run() {
				if (member.getLoginConfiguration().getAccountType() == AccountType.ORIGINAL)
					member.setTag(LoginMain.ORIGINAL_TAG, true);
				else
					member.setTag(LoginMain.LOGGING_TAG, true);
			}
		}.runTaskLater(LoginMain.getInstance(), 20l);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		TABLIST.removeViewer(event.getPlayer());
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.VOID)
			event.getEntity().teleport(BukkitMain.getInstance().getLocationFromConfig("spawn"));

		event.setCancelled(true);
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		event.setCancelled(true);
	}

}
