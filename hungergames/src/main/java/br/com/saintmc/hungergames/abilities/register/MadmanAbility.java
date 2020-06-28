package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.abilities.constructor.GladiatorController;
import br.com.saintmc.hungergames.abilities.constructor.UltimatoController;
import br.com.saintmc.hungergames.game.GameState;
import tk.yallandev.saintmc.bukkit.api.actionbar.ActionBarAPI;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class MadmanAbility extends Ability {

	private static final int RADIUS = 15;

	private Map<UUID, Integer> madmanMap = new HashMap<>();

	public MadmanAbility() {
		super("Madman", new ArrayList<>());
	}

	@EventHandler
	public void onDamage(PlayerDamagePlayerEvent e) {
		if (madmanMap.containsKey(e.getPlayer().getUniqueId())) {
			e.setDamage(e.getDamage() + ((e.getDamage() / 500.0D) * madmanMap.get(e.getPlayer().getUniqueId())));
		}
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;

		if (GameGeneral.getInstance().getGameState() != GameState.GAMETIME)
			return;

		for (UUID uniqueId : getMyPlayers()) {
			Player player = Bukkit.getPlayer(uniqueId);

			if (player == null)
				continue;

			if (GameGeneral.getInstance().getGamerController().getGamer(player).isPlaying()) {
				List<Player> lista = getNearbyPlayers(player);

				if (lista.size() < 2)
					continue;

				int effect = 0;

				for (Player perto : lista) {
					int efeito = lista.size() * 2;
					addEffect(perto.getUniqueId(), efeito);

					if (efeito > effect)
						effect = efeito;
				}

				int percent = effect / 100;
				ActionBarAPI.send(player, "§fMadman §a0." + (percent >= 10 ? percent : "0" + percent) + "%");
			}
		}

		if (!madmanMap.isEmpty()) {
			Iterator<Entry<UUID, Integer>> iterator = madmanMap.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<UUID, Integer> entry = iterator.next();

				UUID uniqueId = entry.getKey();

				entry.setValue(entry.getValue() - 3);
				int effect = entry.getValue();

				if (effect <= 0) {
					iterator.remove();
					continue;
				}

				Player player = Bukkit.getPlayer(uniqueId);

				if (player == null)
					continue;

				int percent = effect / 100;
				ActionBarAPI.send(player, "§fMadman §c0." + (percent >= 10 ? percent : "0" + percent) + "%");
			}
		}
	}

	private void addEffect(UUID uniqueId, int playerCount) {
		int effect = madmanMap.computeIfAbsent(uniqueId, v -> 0);

		if (effect == 0) {
			if (Bukkit.getPlayer(uniqueId) != null) {
				Bukkit.getPlayer(uniqueId).sendMessage("§cHá um madman por perto!");
			}
		}

		madmanMap.put(uniqueId, effect + (playerCount + 3));
	}

	private List<Player> getNearbyPlayers(Player player) {
		List<Player> players = new ArrayList<Player>();

		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online == player)
				continue;

			if (AdminMode.getInstance().isAdmin(online)
					|| GameGeneral.getInstance().getGamerController().getGamer(online).isNotPlaying())
				continue;

			if (GladiatorController.GLADIATOR_CONTROLLER.isInFight(online)
					|| UltimatoController.ULTIMATO_CONTROLLER.isInFight(online))
				continue;

			double distance = player.getLocation().distance(online.getPlayer().getLocation());

			if (distance <= RADIUS) {
				players.add(online);
			}
		}

		return players;
	}

}
