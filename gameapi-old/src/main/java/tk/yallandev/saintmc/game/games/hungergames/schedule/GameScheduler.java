package tk.yallandev.saintmc.game.games.hungergames.schedule;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.common.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.CustomKit;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.constructor.ScheduleArgs;
import tk.yallandev.saintmc.game.games.hungergames.HungerGamesMode;
import tk.yallandev.saintmc.game.games.hungergames.listener.DeathListener;
import tk.yallandev.saintmc.game.games.hungergames.manager.FeastManager;
import tk.yallandev.saintmc.game.games.hungergames.structure.FinalBattleStructure;
import tk.yallandev.saintmc.game.games.hungergames.structure.MinifeastStructure;
import tk.yallandev.saintmc.game.scheduler.Schedule;

public class GameScheduler implements Schedule {
	
	private static FeastManager feastManager = new FeastManager();
	private MinifeastStructure minifeast;
	private FinalBattleStructure finalBattle;
	private int nextMinifeast;
	private Random r = new Random();
	
	private ArmorStand title;
	private ArmorStand subTitle;

	public GameScheduler() {
		minifeast = new MinifeastStructure();
		finalBattle = new FinalBattleStructure();
		nextMinifeast = 240 + r.nextInt(300);
	}

	private void killPlayer(Player p) {
		Gamer gamer = Gamer.getGamer(p);
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());
		
		DeathListener.deathPlayer(p);
		gamer.addDeath();
		
		HashMap<String, String> replaces = new HashMap<>();
		replaces.put("%player%", p.getName());
		replaces.put("%player_Kit%", (gamer.getKit() != null ? ((gamer.getKit() instanceof CustomKit) ? ChatColor.DARK_GRAY : "") : "") + NameUtils.formatString(gamer.getKitName())); // TODO
		// KITS
		String kickMessage = DeathListener.deathMessage(p, "death-message-kills", replaces);
		
		if (player.hasGroupPermission(Group.TRIAL)) {
			AdminMode.getInstance().setAdmin(p, player);
			gamer.setGamemaker(true);
		} else {
			gamer.setSpectator(true);
		}
		
		if (!player.hasGroupPermission(Group.SAINT)) {
			int number = GameMain.getPlugin().playersLeft() + 1;
			
			if (number <= 10) {
				p.kickPlayer(T.t(GameMain.getPlugin(),player.getLanguage(), "you-lost-last-10").replace("%number%", number + "").replace("%total%", GameMain.getPlugin().getTotalPlayers() + "") + " " + kickMessage);
				return;
			}
			
			p.kickPlayer(T.t(GameMain.getPlugin(),player.getLanguage(), "you-lost") + " " + kickMessage);
		}
	}

	@Override
	public void pulse(ScheduleArgs args) {
		/**
		 * FEAST
		 */
		if (args.getTimer() == 60 * 60) {
			Player p = null;
			int kills = 0;
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				Gamer gamer = Gamer.getGamer(player);
				
				if (gamer.isGamemaker())
					continue;
				
				if (gamer.isSpectator())
					continue;
				
				if (gamer.getMatchkills() >= kills) {
					if (p != null) {
						killPlayer(p);
					}
					kills = gamer.getMatchkills();
					p = player;
				} else {
					killPlayer(player);
					((HungerGamesMode)GameMain.getPlugin().getGameMode()).checkWinner();
				}
			}
		}
		
		if (!feastManager.isChestSpawned() && args.getTimer() >= HungerGamesMode.FEAST_SPAWN - feastManager.getCounter()) {
			if (feastManager.getCounter() > 0) {
				if (!feastManager.isSpawned()) {
					feastManager.spawnFeast();
					((HungerGamesMode) GameMain.getPlugin().getGameMode()).getScoreBoardManager().addFeastTimer(feastManager.getFeastLocation(), feastManager.getCounter());
					
					Location hologramLocation = feastManager.getFeastLocation().clone().add(0.5, 1.5, 0.5);
					
					title = (ArmorStand) feastManager.getFeastLocation().getWorld().spawnEntity(hologramLocation, EntityType.ARMOR_STAND);
					
					title.setGravity(false);
					title.setCustomNameVisible(true);
					title.setCustomName("�%feast-hologram%�");
					title.setVisible(false);
					
					subTitle = (ArmorStand) feastManager.getFeastLocation().getWorld().spawnEntity(hologramLocation.subtract(0, 0.35, 0), EntityType.ARMOR_STAND);
					
					subTitle.setGravity(false);
					subTitle.setCustomNameVisible(true);
					subTitle.setCustomName("�b" + StringTimeUtils.format(feastManager.getCounter()));
					subTitle.setVisible(false);
				} else {
					((HungerGamesMode) GameMain.getPlugin().getGameMode()).getScoreBoardManager().updateFeastTimer(feastManager.getCounter());
					subTitle.setCustomName("�b" + StringTimeUtils.format(feastManager.getCounter()));
				}
				
				if ((feastManager.getCounter() % 60 == 0 || (feastManager.getCounter() < 60 && (feastManager.getCounter() % 15 == 0 || feastManager.getCounter() == 10 || feastManager.getCounter() <= 5))))
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.sendMessage(MessageManager.getCountDownMessage(CountDownMessageType.FEAST, BattlebitsAPI.getAccountCommon().getBattlePlayer(p.getUniqueId()).getLanguage(), feastManager.getCounter(), feastManager.getFeastLocation()));
					}
			} else {
				((HungerGamesMode) GameMain.getPlugin().getGameMode()).getScoreBoardManager().removeFeastTimer(feastManager.getFeastLocation());
				feastManager.spawnChests();
				
				title.setCustomName("�%feast-spawned%�");
				subTitle.remove();
				
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage(MessageManager.getCountDownMessage(CountDownMessageType.FEAST_SPAWNED, BattlebitsAPI.getAccountCommon().getBattlePlayer(p.getUniqueId()).getLanguage(), feastManager.getCounter(), feastManager.getFeastLocation()));
				}
			}
			feastManager.count();
		}
		/**
		 * MINIFEAST
		 */
		if (nextMinifeast <= 0) {
			nextMinifeast = 240 + r.nextInt(300);
			Location place = minifeast.findPlace();
			minifeast.place(place);
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(MessageManager.getMinifeastMessage(BattlePlayer.getLanguage(p.getUniqueId()), place));
			}
		} else {
			--nextMinifeast;
		}
		
		/**
		 * BONUS FEAST
		 */
		
		if (args.getTimer() == HungerGamesMode.BONUSFEAST_SPAWN) {
			feastManager.spawnBonusFeast();
			Bukkit.broadcastMessage("�%bonusfeast-spawned%�");
		}
		
		/**
		 * FINAL BATTLE
		 */
		
		if (args.getTimer() == HungerGamesMode.FINALBATTLE_TIME) {
			Location loc = finalBattle.findPlace();
			finalBattle.place(loc);
			finalBattle.teleportPlayers(loc);
			Bukkit.broadcastMessage("�%finalbattle-spawned%�");
		}
	}

	public static FeastManager getFeastManager() {
		return feastManager;
	}

}
