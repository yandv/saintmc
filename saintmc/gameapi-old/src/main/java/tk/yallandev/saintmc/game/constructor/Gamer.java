package tk.yallandev.saintmc.game.constructor;

import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.game.GameMain;

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
		
		this.status = CommonGeneral.getInstance().getStatusData().loadStatus(uniqueId, StatusType.SHADOW);
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
//	public boolean hasKit(String kitName) {
//		if (allKitFree)
//			return allKitFree;
//		
//		if (GameKit.kitReward.getOrDefault(uniqueId, "Nenhum").equals(kitName.toLowerCase()))
//			return true;
//		
//		BattlePlayer player = BattlePlayer.getPlayer(uniqueId);
//		
//		if (player == null)
//			return false;
//		
//		if (player.hasPermission("kit." + kitName.toLowerCase()))
//			return true;
//		
//		if (player.hasGroupPermission(Group.ULTIMATE) || player.hasPermission("tag.winner"))
//			return true;
//		
//		if (player.hasGroupPermission(Group.PREMIUM))
//			if (HungerGamesMode.KITROTATE.get(Group.PREMIUM).contains(kitName))
//				return true;
//		
//		if (player.hasGroupPermission(Group.LIGHT))
//			if (HungerGamesMode.KITROTATE.get(Group.LIGHT).contains(kitName))
//				return true;
//		
//		return HungerGamesMode.KITROTATE.get(Group.NORMAL).contains(kitName);
//	}
//
//	public void addKill() {
//		addKill(1);
//	}
//
//	public void addKill(int i) {
//		setKills(kills + i);
//		matchkills += i;
//	}
//
//	public void addDeath() {
//		addDeath(1);
//	}
//
//	public void addDeath(int i) {
//		setDeaths(deaths + i);
//	}
//
//	public void addWin() {
//		addWin(1);
//	}
//
//	public void addWin(int i) {
//		setWins(wins + i);
//	}
//
//	public void setKills(int kills) {
//		this.kills = kills;
//		DataGamer.saveGamerField(this, "kills");
//	}
//
//	public void setDeaths(int deaths) {
//		this.deaths = deaths;
//		DataGamer.saveGamerField(this, "deaths");
//	}
//
//	public void setWins(int wins) {
//		this.wins = wins;
//		DataGamer.saveGamerField(this, "wins");
//	}
//
//	public int getKills() {
//		return kills;
//	}
//
//	public int getMatchkills() {
//		return matchkills;
//	}
//
//	public int getDeaths() {
//		return deaths;
//	}
//
//	public int getWins() {
//		return wins;
//	}
//
//	public int getMultiKill() {
//		return multiKill;
//	}
//
//	public String getKitName() {
//		return getKit() != null ? getKit().getName()
//				: "Nenhum";
//	}
//
	public Kit getKit() {
		return GameMain.getPlugin().getKitManager().getPlayerKit(uniqueId);
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

	public boolean hasKit(String name) {
		return false;
	}

	public boolean isSpectatorsEnabled() {
		return false;
	}

	public void setSpectatorsEnabled(boolean b) {
		
	}

	public String getKitName() {
		return null;
	}

	public boolean isInvisible() {
		return false;
	}

	public void setNoKit(boolean b) {
		
	}
	
//
//	public static enum CustomKitType {
//		NORMAL, VIP, FULL;
//	}
}
