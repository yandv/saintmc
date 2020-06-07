package tk.yallandev.saintmc.bukkit.api.character;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.profile.Profile;
import com.github.juliarn.npc.profile.Profile.Property;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.player.TextureAPI;

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
		this.interactHandler = interactHandler;
		registerCharacter(this);
	}

	public Character(String name, UUID uniqueId, Location location, Interact interactHandler) {
		Profile profile = new Profile(uniqueId, name, new ArrayList<>());
		Property property = null;

		try {
			WrappedSignedProperty wrappedProperty = TextureAPI.textures.get(new WrappedGameProfile(uniqueId, name));
			property = new Profile.Property(wrappedProperty.getName(), wrappedProperty.getValue(),
					wrappedProperty.getSignature());
		} catch (Exception e) {
			e.printStackTrace();
		}

		profile.setProperties(Arrays.asList(property));
		
		this.npc = new NPC.Builder(profile).location(location).imitatePlayer(false).lookAtPlayer(false)
				.build(BukkitMain.getInstance().getServerConfig().getNpcPool());
		this.interactHandler = interactHandler;
		registerCharacter(this);
	}
	
	public Character(String name, UUID uniqueId, UUID skinUniqueId, Location location, Interact interactHandler) {
		Profile profile = new Profile(uniqueId, name, new ArrayList<>());
		Property property = null;

		try {
			WrappedSignedProperty wrappedProperty = TextureAPI.textures.get(new WrappedGameProfile(skinUniqueId, name));
			property = new Profile.Property(wrappedProperty.getName(), wrappedProperty.getValue(),
					wrappedProperty.getSignature());
		} catch (Exception e) {
			e.printStackTrace();
		}

		profile.setProperties(Arrays.asList(property));
		
		this.npc = new NPC.Builder(profile).location(location).imitatePlayer(false).lookAtPlayer(false)
				.build(BukkitMain.getInstance().getServerConfig().getNpcPool());
		this.interactHandler = interactHandler;
		registerCharacter(this);
	}

	public static Character createCharacter(String name, UUID uniqueId, Location location) {
		return new Character(name, uniqueId, location, new Interact() {

			@Override
			public boolean onInteract(Player player, NPC npc, EntityUseAction action) {
				return false;
			}
		});
	}

	public static NPC createNpc(String name, UUID uniqueId) {
		return new NPC.Builder(new Profile(uniqueId, name, new ArrayList<>())).imitatePlayer(false).lookAtPlayer(false)
				.build(BukkitMain.getInstance().getServerConfig().getNpcPool());
	}

	public void registerCharacter(Character character) {
		OBSERVER_MAP.put(character.getNpc().getEntityId(), character);
	}

	public static void unregisterCharacter(Integer id) {
		OBSERVER_MAP.remove(id);
	}

	public static Character getCharacter(Integer id) {
		return OBSERVER_MAP.get(id);
	}

	public enum InteractType {

		PLAYER, CLICK;

	}

	@Getter
	public static abstract class Interact {

		private EntityUseAction entityUseAction;

		public Interact() {
			this.entityUseAction = EntityUseAction.INTERACT;
		}

		public Interact(EntityUseAction entityUseAction) {
			this.entityUseAction = entityUseAction;
		}

		public abstract boolean onInteract(Player player, NPC npc, EntityUseAction action);

	}

}
