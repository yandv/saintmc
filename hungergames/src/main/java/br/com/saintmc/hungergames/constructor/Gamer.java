package br.com.saintmc.hungergames.constructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.death.DeathCause;
import br.com.saintmc.hungergames.game.Game;
import br.com.saintmc.hungergames.kit.Kit;
import br.com.saintmc.hungergames.kit.KitType;
import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.permission.Group;

/**
 * 
 * Store player state in server
 * 
 * @author yandv
 *
 */

@Getter
public class Gamer {

	/*
	 * Personal
	 */

	private Player player;

	private String playerName;
	private UUID uniqueId;

	private Map<KitType, Kit> kitMap;
	private Set<KitType> noKitList;

	/*
	 * State
	 */

	@Setter
	private boolean spectator;
	@Setter
	private boolean gamemaker;

	@Setter
	private boolean timeout;
	@Setter
	private DeathCause deathCause;

	/*
	 * Status
	 */

	private Status status;
	@Setter
	private Game game;
	private int matchKills;

	/*
	 * Config
	 */

	@Setter
	private boolean spectatorsEnabled;

	public Gamer(Player player) {
		this.player = player;

		this.playerName = player.getName();
		this.uniqueId = player.getUniqueId();

		this.noKitList = new HashSet<>();
		this.kitMap = new HashMap<>();

		this.status = CommonGeneral.getInstance().getStatusManager().loadStatus(getUniqueId(), StatusType.HG);
	}

	public void setKit(KitType kitType, Kit kit) {
		
		if (getKit(kitType) != null) {
			for (Ability ability : getKit(kitType).getAbilities())
				ability.unregisterPlayer(getPlayer());
		}
		
		this.kitMap.put(kitType, kit);
	}

	public void removeKit(KitType kitType) {
		if (getKit(kitType) != null) {
			for (Ability ability : getKit(kitType).getAbilities())
				ability.unregisterPlayer(getPlayer());
		}
		
		this.kitMap.remove(kitType);
	}

	public String getKitName(KitType kitType) {
		if (kitMap.containsKey(kitType))
			return kitMap.get(kitType).getName();
		else
			return "Nenhum";
	}

	public Kit getKit(KitType kitType) {
		return kitMap.get(kitType);
	}

	public boolean hasKit(KitType kitType) {
		return kitMap.containsKey(kitType);
	}

	public boolean hasAbility(String abilityName) {
		for (Kit kit : kitMap.values())
			for (Ability ability : kit.getAbilities())
				if (ability.getName().equalsIgnoreCase(abilityName))
					return true;

		return false;
	}

	public Ability getAbility(String abilityName) {
		for (Kit kit : kitMap.values())
			for (Ability ability : kit.getAbilities())
				if (ability.getName().equalsIgnoreCase(abilityName))
					return ability;

		return null;
	}

	public boolean isAbilityItem(ItemStack item) {
		for (Kit kit : kitMap.values())
			for (Ability ability : kit.getAbilities()) {
				if (ability.isAbilityItem(item))
					return true;
			}

		return false;
	}

	public boolean isNotPlaying() {
		return gamemaker || spectator;
	}

	public boolean hasKit(String kitName) {
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uniqueId);

		if (player == null)
			return false;

		if (player.hasPermission("kit." + kitName.toLowerCase()))
			return true;

		if (player.hasGroupPermission(Group.SAINT) || player.hasPermission("tag.winner"))
			return true;

		if (player.hasGroupPermission(Group.BLIZZARD))
			if (GameMain.KITROTATE.get(Group.BLIZZARD).contains(kitName))
				return true;

		if (player.hasGroupPermission(Group.LIGHT))
			if (GameMain.KITROTATE.get(Group.LIGHT).contains(kitName))
				return true;

		return GameMain.KITROTATE.get(Group.MEMBRO).contains(kitName);
	}

	public boolean isNoKit(KitType kitType) {
		return noKitList.contains(kitType);
	}
	
	public void setNoKit(KitType kitType) {
		noKitList.add(kitType);
	}
	
	public void removeNoKit(KitType kitType) {
		noKitList.remove(kitType);
	}

	public void addKill() {
		matchKills++;
		getStatus().addKill();
	}

}
