package tk.yallandev.saintmc.game.games.hungergames.abilitie.constructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GladiatorFightController {
	
    private List<UUID> playersInFight;
	private List<Block> fightsBlocks;

	public GladiatorFightController() {
		this.playersInFight = new ArrayList<>();
		this.fightsBlocks = new ArrayList<>();
	}

	public void stop() {
		for (Block b : this.fightsBlocks) {
			b.setType(Material.AIR);
		}
	}

	public boolean isInFight(Player p) {
		return this.playersInFight.contains(p.getUniqueId());
	}

	public void removePlayerFromFight(UUID id) {
		this.playersInFight.remove(id);
	}

	public void addPlayerToFights(UUID id) {
		this.playersInFight.add(id);
	}
	
	public void removeBlock(Block b) {
		this.fightsBlocks.remove(b);
	}

	public void addBlock(Block b) {
		this.fightsBlocks.add(b);
	}

	public boolean isFightBlock(Block b) {
		return this.fightsBlocks.contains(b);
	}
}
