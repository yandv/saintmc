package tk.yallandev.saintmc.skwyars.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;

public class MessageListener implements Listener {
	
	private Map<String, String> translateMap;

	public MessageListener() {
		translateMap = new HashMap<>();
		
		translateMap.put("death-message-null", "§e%player% morreu de forma desconhecida");
		translateMap.put("death-message-entityattackplayer",
				"§e%killed_By% levou %player% para conhecer jesus");
		translateMap.put("death-message-entityattackentity",
				"§e%player% levou uma rasteira de um %killed_By%");
		translateMap.put("death-message-border",
				"§e%player% morreu para a bordar do mundo");
		translateMap.put("death-message-leave", "§e%player% desistiu da partida");
		translateMap.put("death-message-kills", "§e%player% morreu");
		translateMap.put("death-message-lava", "§e%player% morreu na lava");
		translateMap.put("death-message-fall",
				"§e%player% esqueceu de abrir os paraquedas");
		translateMap.put("death-message-entityexplosion",
				"§e%player% morreu explodido por um mob");
		translateMap.put("death-message-suffocation", "§e%player% morreu sufocado");
		translateMap.put("death-message-fire", "§e%player% morreu pegando fogo");
		translateMap.put("death-message-firetick", "§e%player% morreu pegando fogo");
		translateMap.put("death-message-melting",
				"§e%player% morreu de forma desconhecidas");
		translateMap.put("death-message-blockexplosion", "§e%player% morreu explodido");
		translateMap.put("death-message-lightning",
				"§e%player% morreu por raios que cairam do ceu");
		translateMap.put("death-message-suicide", "§e%player% se matou");
		translateMap.put("death-message-starvation", "§e%player% morreu de fome");
		translateMap.put("death-message-poison", "§e%player% morreu envenenado");
		translateMap.put("death-message-magic", "§e%player% morreu por magia");
		translateMap.put("death-message-wither", "§e%player% secou até a morte");
		translateMap.put("death-message-fallingblock",
				"§e%player% foi esmagado por um bloco");
		translateMap.put("death-message-thorns", "§e%player% foi espetado até a morte");
		translateMap.put("death-message-projectileentity",
				"§e%player% morreu flechado por um esqueleto");
		translateMap.put("death-message-projectileplayer",
				"§e%killed_By% com seu arco levou %player% para conhecer jesus");
		translateMap.put("death-message-contact", "§e%player% morreu");
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		DamageCause cause = null;
		Player killer = null;

		if (player.getLastDamageCause() != null && player.getLastDamageCause().getCause() != null)
			cause = player.getLastDamageCause().getCause();

		String causeString = cause.toString().toLowerCase();
		HashMap<String, String> replaces = new HashMap<>();

		switch (cause) {
		case PROJECTILE:
			if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) player.getLastDamageCause();

				if (e.getDamager() instanceof Projectile) {
					Projectile projectile = (Projectile) e.getDamager();
					ProjectileSource shooter = projectile.getShooter();

					if (shooter instanceof Player) {
						killer = (Player) shooter;
						causeString = "projectile_player";
					} else if (shooter instanceof Entity) {
						causeString = "projectile_entity";
						replaces.put("%killed_By%",
								((Entity) shooter).getType().toString().replace("_", "").toLowerCase());
					} else {
						causeString = null;
					}

				} else {
					causeString = null;
				}
			} else {
				causeString = null;
			}
			break;
		case ENTITY_ATTACK:
			if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) player.getLastDamageCause();

				if (e.getDamager() instanceof Player) {
					causeString = "entity_attack_player";
					killer = player.getKiller();
				} else {
					causeString = "entity_attack_entity";
					replaces.put("%killed_By%", e.getDamager().getType().toString().replace("_", "").toLowerCase());
				}
			} else {
				causeString = null;
			}
			break;
		case CUSTOM:
			causeString = "border";
		default:
			break;
		}

		String deathMessageId = "death-message-" + (causeString != null ? causeString.replace("_", "") : "null");

		replaces.put("%player%", player.getName());

		if (killer != null) {
			replaces.put("%killed_By%", killer.getName());
		}

		deathMessage(player, deathMessageId, replaces);
	}
	
	public String deathMessage(Player player, String messageId, HashMap<String, String> replaces) {
		String messageReturn = "";
		String message = t(messageId);

		for (Entry<String, String> entry : replaces.entrySet()) {
			message = message.replace(entry.getKey(), entry.getValue());
		}
		
		Bukkit.broadcastMessage(message);
		System.out.println(message);

		return messageReturn;
	}

	private String t(String messageId) {
		if (translateMap.containsKey(messageId)) {
			return translateMap.get(messageId);
		}

		System.out.println("Missing id " + messageId);
		return translateMap.get("death-message-null").replace("&", "§");
	}

}
