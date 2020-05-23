package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.actionbar.ActionBarAPI;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.interfaces.Disableable;

/*
 * 
 * Add 0.125 a cada segundo
 * 
 */

public class MadmanAbility extends Ability implements Disableable {

	private Map<UUID, Double> madmanMap = new HashMap<>();

	public MadmanAbility() {
		super(new ItemStack(Material.POTION, 1, (short) 8232), AbilityRarity.RARE);
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

		for (Gamer gamer : GameMain.getPlugin().getGamerManager().getGamers()) {
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

		ActionBarAPI.send(player, "§eCooldown " + bar.toString());
	}

//	@EventHandler
//	public void onUpdate(UpdateEvent e) {
//		if (e.getType() != UpdateType.SECOND)
//			return;
//		
//		for (Gamer gamer : GameMain.getPlugin().getGamerManager().getGamers()) {
//			if (gamer.getPlayer() == null || gamer.isNotPlaying())
//				continue;
//			
//			if (hasAbility(gamer.getPlayer())) {
//				List<Player> lista = getNearbyPlayers(gamer, 15);
//				
//				if (lista.size() < 2) {
//					continue;
//				}
//				
//				for (Player perto : lista) {
//					int efeito = lista.size() * 2;
//					addEffect(perto.getUniqueId(), efeito);
//				}
//			}
//			
//			if (effect.containsKey(gamer.getPlayer().getUniqueId())) {
//				removeEffect(gamer.getPlayer().getUniqueId());
//			}
//		}
//	}
//
//	private void removeEffect(UUID u) {
//		int effect = this.effect.get(u);
//		effect = effect - 10;
//		this.effect.put(u, effect);
//		if (effect <= 0) {
//			this.effect.remove(u);
//		}
//	}
//
//	private void addEffect(UUID u, int efeito) {
//		int effect = (this.effect.containsKey(u) ? this.effect.get(u) : 0);
//		effect = effect + (efeito + 10);
//		this.effect.put(u, effect);
//	}

	private List<Player> getNearbyPlayers(Gamer p, int i) {
		List<Player> players = new ArrayList<Player>();
		List<Entity> entities = p.getPlayer().getNearbyEntities(i, i, i);
		for (Entity e : entities) {
			if (!(e instanceof Player))
				continue;

			if (Gamer.getGamer((Player) e).isNotPlaying())
				continue;
			
			if (hasAbility(p.getPlayer()))
				continue;

			players.add((Player) e);
		}
		return players;
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 20;
	}

}
