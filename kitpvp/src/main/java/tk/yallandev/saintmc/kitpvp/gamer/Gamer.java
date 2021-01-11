package tk.yallandev.saintmc.kitpvp.gamer;

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

	private Freekill[] freekillArray = new Freekill[6];
	private int freekillCount;

	public Gamer(Player player) {
		this.player = player;

		this.spawnProtection = true;
		this.combatStart = -1l;
	}

	public boolean isStatusable(UUID uuid) {
		for (Freekill freekill : freekillArray) {
			if (freekill == null)
				continue;
			if (freekill.getPlayerId().equals(uuid)) {
				System.out.println(System.currentTimeMillis() - freekill.getTime() > 1000 * 60 * 180);
				System.out.println(System.currentTimeMillis() - freekill.getTime() < 1000 * 60 * 180);

				if (System.currentTimeMillis() - freekill.getTime() > 1000 * 60 * 180) {
					return false;
				}

				return true;
			}
		}

		return true;
	}

	public void setLastKill(Player player) {
		Freekill freekill = new Freekill(player.getUniqueId(), System.currentTimeMillis());

		freekillArray[freekillCount % freekillArray.length] = freekill;
		freekillCount++;
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

	public boolean hasKitPermission(Kit kit) {
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(getUuid());

		if (player == null)
			return false;

		if (player.hasPermission("kitpvp.kit." + kit.getName().toLowerCase())
				|| player.hasPermission("tag.torneioplus"))
			return true;

		if (player.hasGroupPermission(Group.ELITE))
			return true;

		if (player.hasGroupPermission(Group.PRO))
			if (GameMain.KITROTATE.get(Group.PRO).contains(kit.getName().toLowerCase()))
				return true;

		if (player.hasGroupPermission(Group.PRO))
			if (GameMain.KITROTATE.get(Group.PRO).contains(kit.getName().toLowerCase()))
				return true;

		return GameMain.KITROTATE.get(Group.MEMBRO).contains(kit.getName().toLowerCase());
	}
}
