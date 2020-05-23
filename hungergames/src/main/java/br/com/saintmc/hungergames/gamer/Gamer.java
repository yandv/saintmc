package br.com.saintmc.hungergames.gamer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.death.DeathCause;
import br.com.saintmc.hungergames.kit.Kit;
import br.com.saintmc.hungergames.kit.KitType;
import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;

@Getter
public class Gamer {

	/*
	 * Personal
	 */

	private Player player;

	private String playerName;
	private UUID uniqueId;

	private Map<KitType, Kit> kitMap;

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
	 * Config
	 */

	@Setter
	private boolean spectatorsEnabled;

	public Gamer(Player player) {
		this.player = player;

		this.playerName = player.getName();
		this.uniqueId = player.getUniqueId();

		this.kitMap = new HashMap<>();
	}

	public void setKit(KitType kitType, Kit kit) {
		this.kitMap.put(kitType, kit);
	}

	public void removeKit(KitType kitType) {
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

}
