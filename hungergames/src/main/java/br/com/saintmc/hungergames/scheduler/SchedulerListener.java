package br.com.saintmc.hungergames.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.event.game.GameInvincibilityEndEvent;
import br.com.saintmc.hungergames.event.game.GameStartEvent;
import br.com.saintmc.hungergames.event.game.GameStateChangeEvent;
import br.com.saintmc.hungergames.event.player.PlayerItemReceiveEvent;
import br.com.saintmc.hungergames.kit.KitType;
import br.com.saintmc.hungergames.listener.register.BlockListener;
import br.com.saintmc.hungergames.listener.register.DeathListener;
import br.com.saintmc.hungergames.listener.register.SpectatorListener;
import br.com.saintmc.hungergames.scheduler.types.GameScheduler;
import br.com.saintmc.hungergames.scheduler.types.InvincibilityScheduler;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;

public class SchedulerListener implements Listener {

	private GameGeneral gameGeneral;

	public SchedulerListener(GameGeneral gameGeneral) {
		this.gameGeneral = gameGeneral;
	}

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		if (event.getBlock().getBiome().name().contains("JUNGLE"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;

		gameGeneral.pulse();
	}

	@EventHandler
	public void onGameStateChange(GameStateChangeEvent event) {
		CommonGeneral.getInstance().getLogger().info(event.getFromState().name() + " > " + event.getToState().name());
	}

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		gameGeneral.getSchedulerController().addSchedule(new InvincibilityScheduler());

		GameGeneral.getInstance().getAbilityController().registerAbilityListeners();

		Bukkit.getWorlds().forEach(world -> {
			world.setGameRuleValue("doDaylightCycle", "true");
			world.setTime(0l);
		});

		World world = Bukkit.getWorlds().stream().findFirst().orElse(null);

		world.playSound(new Location(world, 0, 120, 0), Sound.AMBIENCE_THUNDER, 1f, 1f);
		world.playSound(new Location(world, 0, 120, 0), Sound.ENDERDRAGON_GROWL, 1f, 1f);

		GameGeneral.getInstance().getGamerController().getGamers().stream().filter(gamer -> gamer.isPlaying())
				.forEach(gamer -> {
					Player player = gamer.getPlayer();

					player.closeInventory();

					player.getInventory().clear();
					player.getInventory().setArmorContents(new ItemStack[4]);

					Bukkit.getPluginManager().callEvent(new PlayerItemReceiveEvent(player));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 30, 1));

					if (Member.hasGroupPermission(player.getUniqueId(), Group.VIP) || gamer.isWinner()) {
						if (!gamer.hasKit(KitType.PRIMARY))
							gamer.setNoKit(KitType.PRIMARY);

						if (!gamer.hasKit(KitType.SECONDARY))
							gamer.setNoKit(KitType.SECONDARY);
					}

					if (!AdminMode.getInstance().isAdmin(player)) {
						player.setAllowFlight(false);
						player.setGameMode(org.bukkit.GameMode.SURVIVAL);
					}

					gamer.setGame(GameMain.GAME);
					gamer.getStatus().addMatch();
					player.updateInventory();
				});

		GameMain.getInstance().registerListener(new SpectatorListener());
		GameMain.getInstance().registerListener(new DeathListener());
		GameMain.getInstance().registerListener(new BlockListener());

		GameMain.GAME.setStartPlayers(GameGeneral.getInstance().getPlayersInGame());
		GameMain.GAME.setStartTime(System.currentTimeMillis());

		Bukkit.broadcastMessage("§cA partida iniciou!");
	}

	@EventHandler
	public void onGameStart(GameInvincibilityEndEvent event) {
		gameGeneral.getSchedulerController().addSchedule(new GameScheduler());
	}

}
