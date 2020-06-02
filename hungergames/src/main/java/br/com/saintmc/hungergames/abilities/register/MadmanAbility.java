package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.constructor.Gamer;
import tk.yallandev.saintmc.bukkit.api.actionbar.ActionBarAPI;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

/*
 * 
 * Add 0.125 a cada segundo
 * 
 */

public class MadmanAbility extends Ability {

	private Map<UUID, Double> madmanMap = new HashMap<>();

	public MadmanAbility() {
		super("Madman", new ArrayList<>());
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}

		if (madmanMap.containsKey(e.getEntity().getUniqueId())) {
			if (hasAbility(e.getEntity().getUniqueId()))
				e.setDamage(e.getDamage() + (e.getDamage() * madmanMap.get(e.getEntity().getUniqueId())));
		}
	}

	public void addEffect(Player player, int players) {
		double effect = madmanMap.computeIfAbsent(player.getUniqueId(), v -> 0d);
		effect += (players - 1) * 0.0005;
		madmanMap.put(player.getUniqueId(), effect);
	}

	public void removeEffect(Player player) {
		double effect = madmanMap.get(player.getUniqueId()) - 0.0005;

		if (effect <= 0)
			madmanMap.remove(player.getUniqueId());
		else
			madmanMap.put(player.getUniqueId(), effect);
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;

		for (Gamer gamer : GameGeneral.getInstance().getGamerController().getGamers()) {
			if (gamer.getPlayer() == null || gamer.isNotPlaying())
				continue;

			if (hasAbility(gamer.getUniqueId())) {
				List<Player> list = getNearbyPlayers(gamer, 15);

				if (list.size() < 2)
					continue;

				for (Player player : list) {
					addEffect(player, list.size());
				}
			}

			if (madmanMap.containsKey(gamer.getUniqueId())) {
				removeEffect(gamer.getPlayer());
				display(gamer.getPlayer(), madmanMap.get(gamer.getUniqueId()));
			}
		}
	}

	private void display(Player player, double percentage) {
		StringBuilder bar = new StringBuilder();
		double count = 20 - Math.max(percentage > 0D ? 1 : 0, percentage / 5);

		for (int a = 0; a < count; a++)
			bar.append("§a" + '|');
		for (int a = 0; a < 20 - count; a++)
			bar.append("§c" + '|');

		ActionBarAPI.send(player, "§fMadman " + bar.toString());
	}

	private List<Player> getNearbyPlayers(Gamer p, int i) {
		List<Player> players = new ArrayList<Player>();
		List<Entity> entities = p.getPlayer().getNearbyEntities(i, i, i);
		for (Entity e : entities) {
			if (!(e instanceof Player))
				continue;

			if (GameGeneral.getInstance().getGamerController().getGamer((Player) e).isNotPlaying())
				continue;

			if (hasAbility(p.getPlayer()))
				continue;

			players.add((Player) e);
		}
		return players;
	}

}
