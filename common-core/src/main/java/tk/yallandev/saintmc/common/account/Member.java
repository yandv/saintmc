package tk.yallandev.saintmc.common.account;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.configuration.AccountConfiguration;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration;
import tk.yallandev.saintmc.common.ban.PunishmentHistory;
import tk.yallandev.saintmc.common.music.MusicKit;
import tk.yallandev.saintmc.common.music.MusicType;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.common.permission.Tag;
import tk.yallandev.saintmc.common.server.ServerType;

@Getter
public abstract class Member {

	/*
	 * Player Information
	 * 
	 */

	private String playerName;
	private final UUID uniqueId;

	private String fakeName;
	private Map<String, Long> cooldown;

	private String lastIpAddress;

	/*
	 * Configuration
	 * 
	 */

	private AccountConfiguration accountConfiguration;
	private LoginConfiguration loginConfiguration;

	private MusicKit musicKit;

	/*
	 * History
	 * 
	 */

	private PunishmentHistory punishmentHistory;

	/*
	 * Social Midia
	 * 
	 */

	private Long discordId;
	private String discordName;
	private DiscordType discordType;

	/*
	 * Permission Information
	 * 
	 */

	private Group group;
	private Map<RankType, Long> ranks;
	private Map<String, Long> permissions;
	private Tag tag;

	/*
	 * Status Information
	 * 
	 */

	private int money;
	private int xp;
	private int totalXp;
	private League league;

	private int reputation;

	/*
	 * Player time
	 * 
	 */

	private long firstLogin;
	private long lastLogin;
	private long joinTime;
	private long onlineTime;

	/*
	 * Server Info
	 * 
	 */

	private String serverId;
	private ServerType serverType;

	protected String lastServerId;
	protected ServerType lastServerType;

	private transient boolean online = false;

	public Member(MemberModel memberModel) {
		playerName = memberModel.getPlayerName();
		uniqueId = memberModel.getUniqueId();

		fakeName = memberModel.getFakeName();
		cooldown = memberModel.getCooldown();

		accountConfiguration = memberModel.getAccountConfiguration();
		loginConfiguration = memberModel.getLoginConfiguration();

		musicKit = memberModel.getMusicKit();

		punishmentHistory = memberModel.getPunishmentHistory();

		discordId = memberModel.getDiscordId();
		discordName = memberModel.getDiscordName();
		discordType = memberModel.getDiscordType();

		group = memberModel.getGroup();
		ranks = memberModel.getRanks();
		permissions = memberModel.getPermissions();
		tag = memberModel.getTag();

		money = memberModel.getMoney();
		xp = memberModel.getXp();
		totalXp = memberModel.getTotalXp();
		league = memberModel.getLeague();

		reputation = memberModel.getReputation();

		firstLogin = memberModel.getFirstLogin();
		lastLogin = memberModel.getLastLogin();
		joinTime = memberModel.getJoinTime();
		onlineTime = memberModel.getOnlineTime();

		serverId = memberModel.getServerId();
		serverType = memberModel.getServerType();

		lastServerId = memberModel.getLastServerId();
		lastServerType = memberModel.getLastServerType();

		online = memberModel.isOnline();
	}

	public Member(String playerName, UUID uniqueId) {
		this.playerName = playerName;
		this.uniqueId = uniqueId;

		this.fakeName = "";
		this.cooldown = new HashMap<>();

		this.accountConfiguration = new AccountConfiguration(this);
		this.loginConfiguration = new LoginConfiguration(this);

		this.musicKit = MusicKit.NONE;

		this.punishmentHistory = new PunishmentHistory();

		this.discordName = "";
		this.discordId = 0l;
		this.discordType = DiscordType.DELINKED;

		this.group = Group.MEMBRO;
		this.permissions = new HashMap<>();
		this.ranks = new HashMap<>();
		this.tag = Tag.MEMBRO;

		this.league = League.UNRANKED;
		this.reputation = 5;

		this.firstLogin = System.currentTimeMillis();
		this.lastLogin = -1l;
		this.joinTime = System.currentTimeMillis();
		this.onlineTime = 0l;

		this.serverId = "";
		this.serverType = ServerType.NONE;

		this.lastServerId = "";
		this.lastServerType = ServerType.NONE;
	}

	/*
	 * Fake
	 */

	public void setFakeName(String fakeName) {
		this.fakeName = fakeName;
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "fakeName");
	}

	public boolean isUsingFake() {
		return fakeName != null && !fakeName.isEmpty() && !fakeName.equals(playerName);
	}

	public boolean isOnCooldown(String cooldownKey) {
		cooldownKey = cooldownKey.toLowerCase();
		
		if (cooldown.containsKey(cooldownKey)) {
			if (cooldown.get(cooldownKey) > System.currentTimeMillis()) {
				return true;
			}
			
			cooldown.remove(cooldownKey);
		}
		
		return false;
	}

	public void removeCooldown(String cooldownKey) {
		cooldown.remove(cooldownKey.toLowerCase());
	}
	
	public long getCooldown(String cooldownKey) {
		return cooldown.get(cooldownKey.toLowerCase());
	}

	public void setCooldown(String cooldownKey, long cooldownTime) {
		cooldown.put(cooldownKey.toLowerCase(), cooldownTime);
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "cooldown");
	}

	/*
	 * Social Midia
	 */

	public boolean hasDiscord() {
		if (discordId == null)
			discordId = 0l;

		return discordId != 0l;
	}

	public void setDiscordId(Long discordId, String discordName) {
		this.discordId = discordId;
		this.discordName = discordName;
		this.discordType = discordId == 0l ? DiscordType.DELINKED : DiscordType.NORMAL;
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "discordId");
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "discordName");
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "discordType");
	}

	public String getDiscordName() {
		if (this.discordName.isEmpty() || this.discordName == null) {
			return "Não tem";
		}

		return discordName;
	}

	public void setDiscordType(DiscordType discordType) {
		this.discordType = discordType;
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "discordType");
	}

	/*
	 * Group
	 */

	public boolean setTag(Tag tag) {
		this.tag = tag;
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "tag");

		return true;
	}

	public Group getServerGroup() {
		if (group == Group.MEMBRO) {
			if (!getRanks().isEmpty()) {
				RankType expire = null;

				for (Entry<RankType, Long> expireRank : getRanks().entrySet()) {
					if (expire == null) {
						expire = expireRank.getKey();
					} else if (expireRank.getKey().ordinal() > expire.ordinal()) {
						expire = expireRank.getKey();
					}
				}

				if (expire != null)
					group = Group.valueOf(expire.name());
			}
		}

		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "group");
	}

	public boolean hasGroupPermission(Group groupToUse) {
//		if (getServerGroup() == Group.YOUTUBERPLUS) {
//			return Group.MOD.ordinal() >= groupToUse.ordinal();
//		}
		return getServerGroup().ordinal() >= groupToUse.ordinal();
	}

	public boolean isGroup(Group groupToUse) {
		return getServerGroup().ordinal() == groupToUse.ordinal();
	}

	public void saveRanks() {
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "ranks");
	}

	/*
	 * Status Information
	 */

	public void setReputation(int reputation) {
		this.reputation = reputation;

		if (this.reputation < -10)
			this.reputation = -10;

		if (this.reputation > 20)
			this.reputation = 20;

		CommonGeneral.getInstance().getPlayerData().updateMember(this, "reputation");
	}

	public void setLeague(League league) {
		this.league = league;
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "league");
	}

	public void setXp(int xp) {
		this.xp = xp;
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "xp");
	}

	public int addXp(int xp) {
		if (xp < 0)
			xp = 0;
		
		setTotalXp(getTotalXp() + xp);
		setXp(getXp() + xp);
		return xp;
	}

	public int removeXp(int xp) {
		if (xp < 0)
			xp = 0;

		setTotalXp(getTotalXp() - xp);
		setXp(getXp() - xp);
		return xp;
	}

	public void setTotalXp(int xp) {
		this.totalXp = xp;
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "totalXp");
	}
	
	public boolean hasPermission(String string) {
		return false;
	}

	public void addPermission(String string) {
		
	}

	public void addMoney(int nextInt) {
		
	}

	/*
	 * Server Info
	 */

	public void setServerId(String serverId) {
		this.lastServerId = this.serverId;
		this.serverId = serverId;
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "serverId");
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "lastServerId");
	}

	public void setServerType(ServerType serverType) {
		this.lastServerType = this.serverType;
		this.serverType = serverType;
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "lastServerType");
	}

	/*
	 * Player Info
	 */

	public void setOnline(boolean online) {
		this.online = online;
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "online");
	}

	/*
	 * Player Manager
	 */

	public long getSessionTime() {
		return System.currentTimeMillis() - joinTime;
	}
	
	public long getOnlineTime() {
		return onlineTime;
	}
	
	public void updateTime() {
		this.joinTime = System.currentTimeMillis();
		this.lastLogin = System.currentTimeMillis();
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "joinTime");
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "lastLogin");
	}

	public void setJoinData(String playerName, String hostString) {
		this.playerName = playerName;
		this.lastIpAddress = hostString;
		this.online = true;

		this.accountConfiguration.setPlayer(this);
		this.loginConfiguration.setPlayer(this);

		CommonGeneral.getInstance().getPlayerData().updateMember(this, "playerName");
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "lastIpAddress");
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "online");
	}

	public void connect(String serverId, ServerType type) {
		checkRanks();
		this.serverId = serverId;
		this.serverType = type;
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "serverId");
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "serverType");
	}

	public void setLeaveData() {
		this.online = false;
		this.onlineTime = onlineTime + (System.currentTimeMillis() - lastLogin);

		CommonGeneral.getInstance().getPlayerData().updateMember(this, "online");
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "lastLogin");
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "onlineTime");
	}

	public boolean playMusic(MusicType musicType) {
		if (getMusicKit() == MusicKit.NONE)
			return false;

		return true;
	}

	public void setMusicKit(MusicKit musicKit) {
		this.musicKit = musicKit;
		CommonGeneral.getInstance().getPlayerData().updateMember(this, "musicKit");
	}

	public MusicKit getMusicKit() {
		return musicKit == null ? MusicKit.NONE : musicKit;
	}

	public void checkRanks() {
		if (getRanks() != null && !getRanks().isEmpty()) {
			Iterator<Entry<RankType, Long>> it = getRanks().entrySet().iterator();
			boolean save = false;

			while (it.hasNext()) {
				Entry<RankType, Long> entry = it.next();

				if (System.currentTimeMillis() > entry.getValue()) {
					it.remove();

					sendMessage("§c§l> §fO seu tempo de tag " + Tag.valueOf(entry.getKey().name()).getPrefix() + "§f expirou!");
					sendMessage("§c§l> §fVocê pode comprar novamente em §b" + CommonConst.STORE + "§f!");

					save = true;
				}
			}

			if (save)
				CommonGeneral.getInstance().getPlayerData().updateMember(this, "ranks");
		}
	}

	public abstract void sendMessage(String message);

	public abstract void sendMessage(BaseComponent message);

	public abstract void sendMessage(BaseComponent[] message);
	
	public static Member getMember(UUID uniqueId) {
		return CommonGeneral.getInstance().getMemberManager().getMember(uniqueId);
	}

	public static boolean hasGroupPermission(UUID uniqueId, Group group) {
		return CommonGeneral.getInstance().getMemberManager().getMember(uniqueId).hasGroupPermission(group);
	}

	public static boolean isGroup(UUID uniqueId, Group group) {
		return CommonGeneral.getInstance().getMemberManager().getMember(uniqueId).isGroup(group);
	}
	
	public static boolean isLogged(UUID uniqueId) {
		return CommonGeneral.getInstance().getMemberManager().getMember(uniqueId).getLoginConfiguration().isLogged();
	}

	public static Group getGroup(UUID uniqueId) {
		return CommonGeneral.getInstance().getMemberManager().getMember(uniqueId).getGroup();
	}

}
