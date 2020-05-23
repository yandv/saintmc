package tk.yallandev.saintmc.kitpvp.warp.challenge.sumo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.api.vanish.VanishAPI;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowFightFinishEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowFightStartEvent;
import tk.yallandev.saintmc.kitpvp.warp.challenge.Challenge;
import tk.yallandev.saintmc.kitpvp.warp.challenge.ChallengeType;

@Getter
public class SumoChallenge implements Challenge {
	
	private Player player;
    private Player target;
	
    private long expire;
    
    public SumoChallenge(Player player, Player target) {
    	this.player = player;
    	this.target = target;
    }

	@Override
	public boolean isExpired() {
		return expire < System.currentTimeMillis();
	}

	@Override
	public void start(Location firstLocation, Location secondLocation) {
		Bukkit.getPluginManager().callEvent(new ShadowFightStartEvent(player, target, GameMain.getInstance().getWarpManager().getWarpByName("1v1"), getChallengeType()));
		
		player.teleport(firstLocation);
		target.teleport(secondLocation);
	}

	@Override
	public void finish(Player player) {
		Bukkit.getPluginManager().callEvent(new ShadowFightFinishEvent(getPlayer(), target, player, GameMain.getInstance().getWarpManager().getWarpByName("1v1")));
		
		VanishAPI.getInstance().getHideAllPlayers().remove(player.getUniqueId());
		VanishAPI.getInstance().updateVanishToPlayer(player);

		VanishAPI.getInstance().getHideAllPlayers().remove(target.getUniqueId());
		VanishAPI.getInstance().updateVanishToPlayer(target);
	}

	@Override
	public ChallengeType getChallengeType() {
		return ChallengeType.SUMO_NORMAL;
	}
	
    public void createInventory(Player player) {
    	player.getInventory().clear();
    	player.getInventory().setArmorContents(new ItemStack[4]);
    	
    	player.setHealth(20D);
    	player.setLevel(0);
    	
    	for (PotionEffect pot : player.getActivePotionEffects())
    		player.removePotionEffect(pot.getType());
    }

}
