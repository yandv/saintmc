package tk.yallandev.saintmc.lobby.menu.collectable;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.lobby.LobbyMain;
import tk.yallandev.saintmc.lobby.collectable.Collectables.CollectableType;
import tk.yallandev.saintmc.lobby.collectable.Collectables.Particles;
import tk.yallandev.saintmc.lobby.gamer.Gamer;

public class ParticleInventory {

	public ParticleInventory(Player player) {
		Gamer gamer = LobbyMain.getInstance().getPlayerManager().getGamer(player);
		MenuInventory inv = new MenuInventory("§7Coletáveis - Partículas", 5);

		inv.setItem(4, new ItemBuilder().type(Material.ANVIL).name(" §a* §fRemover §apartículas §fativas!").build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						gamer.setUsingParticle(false);
//						gamer.setAlpha(0);
						player.sendMessage(" §a* §fVocê removeu todas as suas §apartículas§f ativas!");
						player.closeInventory();

					}
				});

		int i = 19;

		for (Particles particles : Particles.values()) {
			inv.setItem(i, new ItemBuilder().type(particles.getMaterial())
					.name("§bPartícula de " + particles.getParticleName()).build(), new MenuClickHandler() {

						@Override
						public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
							player.closeInventory();
							
							gamer.setUsingParticle(true);
							gamer.setUsingWing(false);
							player.sendMessage(
									" §a* §fPartícula §a" + particles.getParticleName() + " §fadicionada a você!");
							gamer.display(CollectableType.PARTICLE, particles.getParticleType());
						}
					});

			if (i % 9 == 7) {
				i += 3;
				continue;
			}

			i++;
		}

		inv.open(player);
	}

}
