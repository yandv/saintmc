package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.constructor.Gamer;

public class KayaAbility extends Ability {

	private static final int RADIUS = 1;
	private static final int HEIGHT = 1;

	public KayaAbility() {
		super("Kaya", Arrays.asList(new ItemStack(Material.GRASS, 32)));

		ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(Material.GRASS));
		recipe.addIngredient(Material.DIRT);
		recipe.addIngredient(Material.SEEDS);
		Bukkit.getServer().addRecipe(recipe);
	}

	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		if (event.getRecipe().getResult() == null)
			return;

		if (event.getRecipe().getResult().getType() == Material.GRASS) {
			for (HumanEntity entity : event.getViewers())
				if (hasAbility((Player) entity))
					return;

			event.getInventory().setItem(0, new ItemStack(Material.AIR));
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

		if (gamer.isNotPlaying())
			return;

		Location loc = event.getPlayer().getLocation();

		for (int z = -RADIUS; z <= RADIUS; z++) {
			for (int x = -RADIUS; x <= RADIUS; x++) {
				for (int y = -HEIGHT; y <= HEIGHT; y++) {
					Block block = loc.clone().add(x, y, z).getBlock();

					if (block.getType() == Material.GRASS && block.hasMetadata("kaya")) {
						
						if (hasAbility(player)) {
							MetadataValue metadata = block.getMetadata("kaya").stream().findFirst().orElse(null);
							
							if (metadata.asString().equalsIgnoreCase(player.getName())) {
								player.sendMessage("§aVocê não pode quebrar o seu kaya.");
								break;
							}
						}
						
						block.setType(Material.AIR);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (hasAbility(event.getPlayer()))
			if (event.getBlock().getType() == Material.GRASS)
				event.getBlock().setMetadata("kaya",
						new FixedMetadataValue(GameMain.getInstance(), event.getPlayer().getName()));
	}

}
