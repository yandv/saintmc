package tk.yallandev.saintmc.bukkit.api.character;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public class Character {

	private static final Map<Integer, Character> OBSERVER_MAP;

	static {
		OBSERVER_MAP = new HashMap<>();
	}

	private Interact interactHandler;
	private NPC npc;

	public Character(NPC npc, Interact interactHandler) {
		this.npc = npc;
		this.npc.spawn();
		this.interactHandler = interactHandler;
		registerCharacter(this);
	}

	public Character(String name, String skinName, Location location, Interact interactHandler) {
		this.npc = new NPC(location, skinName);
		this.npc.spawn();
		this.interactHandler = interactHandler;
		registerCharacter(this);
	}

	public static Character createCharacter(String name, String skinName, Location location) {
		return new Character(name, skinName, location, new Interact() {

			@Override
			public boolean onInteract(Player player) {
				return false;
			}
		});
	}

	public void show(Player player) {
		npc.show(player);
	}

	public void hide(Player player) {
		npc.hide(player);
	}

	public static NPC createNpc(Location location, String skinName) {
		return new NPC(location, skinName);
	}

	public void registerCharacter(Character character) {
		OBSERVER_MAP.put(character.getNpc().getEntityPlayer().getId(), character);
	}

	public static void unregisterCharacter(Integer id) {
		OBSERVER_MAP.remove(id);
	}

	public static Character getCharacter(Integer id) {
		return OBSERVER_MAP.get(id);
	}

	public static Collection<Character> getCharacters() {
		return OBSERVER_MAP.values();
	}

	public enum InteractType {

		PLAYER, CLICK;

	}

	@Getter
	public static abstract class Interact {

		public abstract boolean onInteract(Player player);

	}

}
