package tk.yallandev.saintmc.game.constructor;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.games.hungergames.HungerGamesMode;
import tk.yallandev.saintmc.game.games.hungergames.util.GameKit;

@Getter
public class Gamer {

	private UUID uniqueId;
	private Player player;

	private Status status;

	private int matchkills = 0;
	private int multiKill = 0;
	private long lastKill = Long.MIN_VALUE;

	private boolean spectator;
	private boolean gamemaker;

	public Gamer(UUID uniqueId) {
		this.uniqueId = uniqueId;

		this.status = CommonGeneral.getInstance().getStatusData().loadStatus(uniqueId, StatusType.HG);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void addKill() {
		status.addKills();
		matchkills += 1;
	}

	public void addDeath() {
		status.addDeaths();
	}

	public boolean isSpectator() {
		return spectator;
	}

	public void setSpectator(boolean spectator) {
		this.spectator = spectator;
	}

	public void setGamemaker(boolean gamemaker) {
		this.gamemaker = gamemaker;
	}

	public boolean isGamemaker() {
		return gamemaker;
	}

//	private UUID uniqueId;
//	
//	private HashMap<String, Long> ownedKits = new HashMap<>();
//	private List<String> favoriteKits = new ArrayList<>();
//	
//	private Status status;
//	
//	private GamerType gamerType = GamerType.BETA;
//	
//	private transient int matchkills = 0;
//	private transient int multiKill = 0;
//	private transient long lastKill = Long.MIN_VALUE;
//	
//	private transient boolean spectator = false;
//	private transient boolean gamermaker = false;
//	private transient boolean spectatorsEnabled = false;
//	
//	private transient boolean invisible = false;
//	private transient boolean noKit = false;
//
//	public Gamer(UUID uuid) {
//		this.uniqueId = uuid;
//	}
//
//	public UUID getUniqueId() {
//		return uniqueId;
//	}
//
//	public Player getPlayer() {
//		return Bukkit.getPlayer(uniqueId);
//	}
//
//	public void setGamemaker(boolean isGamemaker) {
//		this.gamermaker = isGamemaker;
//	}
//
//	public void setSpectator(boolean isSpectator) {
//		this.spectator = isSpectator;
//		if (isSpectator) {
//			PlayerSpectateEvent event = new PlayerSpectateEvent(getPlayer());
//			Bukkit.getPluginManager().callEvent(event);
//		}
//	}
//	
//	public void setSpectatorsEnabled(boolean spectatorsEnabled) {
//		this.spectatorsEnabled = spectatorsEnabled;
//	}
//	
//	public void setInvisible(boolean invisible) {
//		this.invisible = invisible;
//	}
//	
//	public void setNoKit(boolean noKit) {
//		this.noKit = noKit;
//	}
//
//	public GamerType getGamerType() {
//		return gamerType;
//	}
//	
//	public HashMap<String, Long> getOwnedKits() {
//		return ownedKits;
//	}
//	

	public List<Kit> getKit() {
		return GameMain.getPlugin().getKitManager().getPlayerKit(uniqueId);
	}

	public Kit getKit(int kitIndex) {

		List<Kit> kit = GameMain.getPlugin().getKitManager().getPlayerKit(uniqueId);

		if (kit.get(kitIndex) != null) {
			return kit.get(kitIndex);
		}

		return null;
//		return GameMain.getPlugin().getKitManager().getPlayerKit(uniqueId);
	}

//
//	public long getLastKill() {
//		return lastKill;
//	}
//
//
//	public boolean isGamemaker() {
//		return gamermaker;
//	}
//
//	public boolean isSpectator() {
//		return spectator;
//	}
//
//	public boolean isSpectatorsEnabled() {
//		return spectatorsEnabled;
//	}
//
	public boolean isNotPlaying() {
		return spectator;
	}

//	
//	public boolean isInvisible() {
//		return invisible;
//	}
//
//	public boolean isNoKit() {
//		return noKit;
//	}
//	
	public static Gamer getGamer(Player player) {
		return getGamer(player.getUniqueId());
	}

	public static Gamer getGamer(UUID uuid) {
		return GameMain.getPlugin().getGamerManager().getGamer(uuid);
	}

	public boolean hasKit(String kitName) {
		if (GameKit.kitReward.getOrDefault(uniqueId, "Nenhum").equals(kitName.toLowerCase()))
			return true;

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uniqueId);

		if (player == null)
			return false;

		if (player.hasPermission("kit." + kitName.toLowerCase()))
			return true;

		if (player.hasGroupPermission(Group.SAINT) || player.hasPermission("tag.winner"))
			return true;

		if (player.hasGroupPermission(Group.BLIZZARD))
			if (HungerGamesMode.KITROTATE.get(Group.BLIZZARD).contains(kitName))
				return true;

		if (player.hasGroupPermission(Group.LIGHT))
			if (HungerGamesMode.KITROTATE.get(Group.LIGHT).contains(kitName))
				return true;

		return HungerGamesMode.KITROTATE.get(Group.MEMBRO).contains(kitName);
	}

	public boolean isSpectatorsEnabled() {
		return true;
	}

	public void setSpectatorsEnabled(boolean b) {

	}

	public String getKitName() {
		return "Nenhum";
	}

	public boolean isInvisible() {
		return false;
	}

	public void setNoKit(boolean b) {

	}

	public boolean isNoKit() {
		return false;
	}

	public void addWin() {
		status.addWin();
	}

	public boolean hasAbility(String string) {
		for (Kit kit : getKit())
			if (kit.hasAbility(string))
				return true;
		return false;
	}

	public Ability getAbility(String string) {
		for (Kit kit : getKit())
			if (kit.hasAbility(string))
				return kit.getAbility(string);
		return null;
	}

//
//	public static enum CustomKitType {
//		NORMAL, VIP, FULL;
//	}
}
