package br.com.saintmc.hungergames.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.game.GameInvincibilityEndEvent;
import br.com.saintmc.hungergames.event.game.GameStartEvent;
import br.com.saintmc.hungergames.event.game.GameStateChangeEvent;
import br.com.saintmc.hungergames.event.player.PlayerItemReceiveEvent;
import br.com.saintmc.hungergames.listener.register.BlockListener;
import br.com.saintmc.hungergames.listener.register.DeathListener;
import br.com.saintmc.hungergames.listener.register.SpectatorListener;
import br.com.saintmc.hungergames.scheduler.types.GameScheduler;
import br.com.saintmc.hungergames.scheduler.types.InvincibilityScheduler;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class SchedulerListener implements Listener {
	
	private GameGeneral gameGeneral;

	public SchedulerListener(GameGeneral gameGeneral) {
		this.gameGeneral = gameGeneral;
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
		
		for (Gamer gamer : GameGeneral.getInstance().getGamerController().getGamers()) {
			Player player = gamer.getPlayer();
			
			if (gamer.isNotPlaying())
				continue;
			
			player.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 1f, 1f);
			player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 1f);
			player.closeInventory();
			player.getInventory().clear();
			player.getInventory().setArmorContents(new ItemStack[4]);
			player.setGameMode(org.bukkit.GameMode.SURVIVAL);
			player.setAllowFlight(false);
			
			Bukkit.getPluginManager().callEvent(new PlayerItemReceiveEvent(player));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*30, 1));
		}
		
		GameMain.getInstance().registerListener(new SpectatorListener());
		GameMain.getInstance().registerListener(new DeathListener());
		GameMain.getInstance().registerListener(new BlockListener());
		
		GameGeneral.getInstance().getAbilityController().registerAbilityListeners();
		
		GameMain.GAME.setStartPlayers(GameGeneral.getInstance().getPlayersInGame());
		GameMain.GAME.setStartTime(System.currentTimeMillis());
		
		Bukkit.broadcastMessage("§cA partida iniciou!");
	}
	
	@EventHandler
	public void onGameStart(GameInvincibilityEndEvent event) {
		gameGeneral.getSchedulerController().addSchedule(new GameScheduler());
		Bukkit.broadcastMessage("§eA invencibilidade acabou!");
	}
	
}
