package tk.yallandev.saintmc.kitpvp.gamer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.kit.Kit;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

@Getter
public class Gamer {

	private Player player;

	private Kit kit;
	private Warp warp;

	private long combatStart;

	@Setter
	private boolean spawnProtection;
	@Setter
	private boolean teleporting;
	@Setter
	private boolean blockCommand;

	private Map<UUID, Long> freekillMap;
	private UUID lastKill;

	public Gamer(Player player) {
		this.player = player;

		this.spawnProtection = true;
		this.combatStart = -1l;

		this.freekillMap = new HashMap<>();
	}

	public void setWarp(Warp warp) {
		this.warp = warp;
	}

	public boolean isInWarp(Warp warp) {
		return getWarp() == warp;
	}

	public boolean isInCombat() {
		return combatStart + 10000 > System.currentTimeMillis();
	}

	public void setCombat() {
		combatStart = System.currentTimeMillis();
	}

	public void removeCombat() {
		combatStart = Long.MIN_VALUE;
	}

	public void setKit(Kit kit) {
		this.kit = kit;
	}

	public boolean hasKit(Kit kit) {
		return this.kit != null && this.kit.getKitName().equalsIgnoreCase(kit.getKitName());
	}

	public boolean hasKit(String kitName) {
		return this.kit != null && this.kit.getKitName().equalsIgnoreCase(kitName);
	}

	public String getKitName() {
		return this.kit == null ? "Nenhum" : this.kit.getKitName();
	}

	public boolean hasKit() {
		return this.kit != null;
	}

	public UUID getUuid() {
		return player.getUniqueId();
	}

	public boolean isStatusable(UUID uniqueId) {
		if (freekillMap.containsKey(uniqueId))
			if (freekillMap.get(uniqueId) > System.currentTimeMillis())
				return false;

		return true;
	}

	public void setLastKill(UUID uniqueId) {
		this.lastKill = uniqueId;
		this.freekillMap.put(uniqueId, System.currentTimeMillis() + (1000 * 60 * 5));
	}

	public boolean hasKitPermission(Kit kit) {
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(getUuid());

		if (player == null)
			return false;

		if (player.hasPermission("kitpvp.kit." + kit.getName().toLowerCase()))
			return true;

		if (player.hasGroupPermission(Group.SAINT))
			return true;

		if (player.hasGroupPermission(Group.BLIZZARD))
			if (GameMain.KITROTATE.get(Group.BLIZZARD).contains(kit.getName().toLowerCase()))
				return true;

		if (player.hasGroupPermission(Group.LIGHT))
			if (GameMain.KITROTATE.get(Group.LIGHT).contains(kit.getName().toLowerCase()))
				return true;

		return GameMain.KITROTATE.get(Group.MEMBRO).contains(kit.getName().toLowerCase());
	}
}
