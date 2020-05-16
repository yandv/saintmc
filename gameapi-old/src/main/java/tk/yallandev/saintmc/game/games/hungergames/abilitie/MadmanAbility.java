package br.com.battlebits.game.games.hungergames.abilitie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.GameMain;
import br.com.battlebits.game.ability.AbilityRarity;
import br.com.battlebits.game.constructor.Ability;
import br.com.battlebits.game.constructor.CustomOption;
import br.com.battlebits.game.constructor.Gamer;
import br.com.battlebits.game.interfaces.Disableable;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class MadmanAbility extends Ability implements Disableable {

	public MadmanAbility() {
		super(new ItemStack(Material.POTION, 1, (short) 8232), AbilityRarity.RARE);
	}
	
	HashMap<UUID, Integer> effect = new HashMap<UUID, Integer>();

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		
		if (effect.containsKey(e.getEntity().getUniqueId())) {
			e.setDamage(e.getDamage() + ((e.getDamage() / 100.0D) * this.effect.get(e.getEntity().getUniqueId())));
		}
	}
	

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getType() != UpdateType.SECOND)
			return;
		
		for (Gamer gamer : GameMain.getPlugin().getGamerManager().getGamers()) {
			if (gamer.getPlayer() == null || gamer.isNotPlaying())
				continue;
			
			if (hasAbility(gamer.getPlayer())) {
				List<Player> lista = getNearbyPlayers(gamer, 15);
				
				if (lista.size() < 2) {
					continue;
				}
				
				for (Player perto : lista) {
					int efeito = lista.size() * 2;
					addEffect(perto.getUniqueId(), efeito);
				}
			}
			
			if (effect.containsKey(gamer.getPlayer().getUniqueId())) {
				removeEffect(gamer.getPlayer().getUniqueId());
			}
		}
	}

	private void removeEffect(UUID u) {
		int effect = this.effect.get(u);
		effect = effect - 10;
		this.effect.put(u, effect);
		if (effect <= 0) {
			this.effect.remove(u);
		}
	}

	private void addEffect(UUID u, int efeito) {
		int effect = (this.effect.containsKey(u) ? this.effect.get(u) : 0);
		effect = effect + (efeito + 10);
		this.effect.put(u, effect);
	}

	private List<Player> getNearbyPlayers(Gamer p, int i) {
		List<Player> players = new ArrayList<Player>();
		List<Entity> entities = p.getPlayer().getNearbyEntities(i, i, i);
		for (Entity e : entities) {
			if (!(e instanceof Player))
				continue;
			
			if (Gamer.getGamer((Player)e).isNotPlaying())
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
