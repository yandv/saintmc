package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.saintmc.hungergames.abilities.Ability;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;

public class JeenAbility extends Ability {

	private Map<Player, Long> jeenMap;

	public JeenAbility() {
		super("jeen", Arrays.asList(new ItemBuilder().name("§aJeen").type(Material.BLAZE_POWDER).build()));
		jeenMap = new HashMap<>();
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player) && isAbilityItem(event.getItem())) {
			if (isCooldown(player))
				return;

			jeenMap.put(player, System.currentTimeMillis());

			player.setFallDistance(-5f);
			player.setVelocity(player.getEyeLocation().getDirection().multiply(2.0F).setY(0.7F));
			player.sendMessage("§aVocê está invisível e voltará ao normal em 5 segundos!");
			player.sendMessage("§eCaso você bata em alguém, o efeito de invisibilidade irá passar!");
			Bukkit.getOnlinePlayers().forEach(target -> target.getPlayer().hidePlayer(player));

			addCooldown(player, 25);
		}
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getCurrentTick() % 5 == 0) {
			Iterator<Entry<Player, Long>> iterator = jeenMap.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Player, Long> entry = iterator.next();

				Player player = entry.getKey();
				long timeElapsed = System.currentTimeMillis() - entry.getValue();

				if (timeElapsed < 750) {
					Bukkit.getOnlinePlayers().stream()
							.forEach(viewer -> ((CraftPlayer) viewer).getHandle().playerConnection
									.sendPacket(new PacketPlayOutWorldParticles(EnumParticle.SPELL_WITCH, true,
											(float) player.getLocation().getX(), (float) player.getLocation().getY(),
											(float) player.getLocation().getZ(), 0.1F, 0.1F, 0.1F, 1, 30)));
					return;
				}

				if (timeElapsed > 5000) {
					Bukkit.getOnlinePlayers().forEach(target -> target.getPlayer().showPlayer(player));
					player.sendMessage("§cO efeito do jeen acabou!");
					iterator.remove();
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (jeenMap.containsKey(player)) {
			jeenMap.remove(player);

			Bukkit.getOnlinePlayers().forEach(target -> target.getPlayer().showPlayer(player));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteract(PlayerDamagePlayerEvent event) {
		if (jeenMap.containsKey(event.getDamager())) {
			jeenMap.remove(event.getDamager());

			if (!AdminMode.getInstance().isAdmin(event.getDamager()))
				Bukkit.getOnlinePlayers().forEach(target -> target.getPlayer().showPlayer(event.getDamager()));

			event.getDamager().sendMessage("§cO efeito do jeen acabou!");
		}
	}

}
