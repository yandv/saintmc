package tk.yallandev.saintmc.kitpvp.warp.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.ActionType;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerLostProtectionEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpRespawnEvent;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.kit.Kit;
import tk.yallandev.saintmc.kitpvp.menu.KitInventory;
import tk.yallandev.saintmc.kitpvp.menu.KitInventory.InventoryType;
import tk.yallandev.saintmc.kitpvp.menu.WarpInventory;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.scoreboard.types.SpawnScoreboard;

public class SpawnWarp extends Warp {

	private ActionItemStack kitSelector = new ActionItemStack(
			new ItemBuilder().type(Material.CHEST).name("§aKit Selector").build(), new ActionItemStack.Interact() {

				@Override
				public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
						ActionType action) {
					new KitInventory(player, InventoryType.OWN);
					return false;
				}
			}.setInventoryClick(true));

	private ActionItemStack warpSelector = new ActionItemStack(
			new ItemBuilder().type(Material.COMPASS).name("§aWarp Selector").build(), new ActionItemStack.Interact() {

				@Override
				public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
						ActionType action) {
					new WarpInventory(player);
					return false;
				}
			}.setInventoryClick(true));

	public SpawnWarp() {
		super("Spawn", BukkitMain.getInstance().getLocationFromConfig("spawn"), new SpawnScoreboard());
		getWarpSettings().setKitEnabled(true);
		getWarpSettings().setSpawnProtection(true);
		getScoreboard().setWarp(this);
	}

	@EventHandler
	public void onPlayerWarpRespawn(PlayerWarpRespawnEvent event) {
		Player player = event.getPlayer();

		if (event.getWarp() != this)
			return;

		handleInventory(player);
	}

	@EventHandler
	public void onPlayerWarpJoin(PlayerWarpJoinEvent event) {
		Player player = event.getPlayer();

		if (event.getWarp() != this)
			return;

		handleInventory(player);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (event.getCause() != DamageCause.VOID)
			return;

		Player player = (Player) event.getEntity();
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (gamer.getWarp() == this)
			event.setDamage(Integer.MAX_VALUE);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (inWarp(event.getPlayer()))
			if (GameMain.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId()).isSpawnProtection())
				if (event.getItemDrop().getItemStack().getType() == Material.CHEST
						|| event.getItemDrop().getItemStack().getType() == Material.COMPASS) {
					event.setCancelled(true);
				}
	}

	@EventHandler
	public void onPlayerLostProtection(PlayerLostProtectionEvent event) {
		if (event.getWarp() == this) {
			Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId());

			if (!gamer.hasKit()) {
				Kit kit = GameMain.getInstance().getKitManager().getDefaultKit();

				gamer.setKit(kit);
				GameMain.getInstance().getKitManager().selectKit(event.getPlayer(), kit);
			}
		}
	}

	private void handleInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		for (PotionEffect potion : player.getActivePotionEffects())
			player.removePotionEffect(potion.getType());

		player.setLevel(0);
		player.setFoodLevel(20);
		player.setHealth(20D);

		player.getInventory().setItem(3, warpSelector.getItemStack());
		player.getInventory().setItem(4, kitSelector.getItemStack());
		player.updateInventory();
	}

	@Override
	public ItemStack getItem() {
		return null;
	}

}
