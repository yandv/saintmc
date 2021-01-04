package tk.yallandev.saintmc.common.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.configuration.AccountConfiguration;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.common.account.medal.Medal;
import tk.yallandev.saintmc.common.ban.PunishmentHistory;
import tk.yallandev.saintmc.common.clan.Clan;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.common.profile.Profile;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.tag.Tag;

@Getter
public abstract class Member implements CommandSender {

	/*
	 * Player Information
	 * 
	 */

	private String playerName;
	private UUID uniqueId;

	/*
	 * Skin Information
	 * 
	 */

	private Profile skinProfile;

	private String fakeName;
	private Map<String, Long> cooldown;

	private String lastIpAddress;

	/*
	 * Configuration
	 * 
	 */

	private AccountConfiguration accountConfiguration;
	private LoginConfiguration loginConfiguration;

	/*
	 * History
	 * 
	 */

	private PunishmentHistory punishmentHistory;

	/*
	 * Social Midia
	 * 
	 */

	private long discordId;
	private String discordName;
	private DiscordType discordType;

	private UUID clanUniqueId;
	private UUID partyId;

	/*
	 * Permission Information
	 * 
	 */

	private Group group;
	private Map<RankType, Long> ranks;
	private Map<String, Long> permissions;

	private Tag tag;
	private boolean chroma;

	private List<Medal> medalList;
	private Medal medal = Medal.NONE;

	/*
	 * Tournament
	 * 
	 */

	private TournamentGroup tournamentGroup;

	/*
	 * Status Information
	 * 
	 */

	private int money;
	private int xp;
	private int position = -1;
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

	private String lastServerId;
	private ServerType lastServerType;

	private boolean online;

	public Member(MemberModel memberModel) {
		playerName = memberModel.getPlayerName();
		uniqueId = memberModel.getUniqueId();

		skinProfile = memberModel.getSkinProfile();

		fakeName = memberModel.getFakeName();
		cooldown = memberModel.getCooldown();

		lastIpAddress = memberModel.getLastIpAddress();

		accountConfiguration = memberModel.getAccountConfiguration();
		loginConfiguration = memberModel.getLoginConfiguration();

		punishmentHistory = memberModel.getPunishmentHistory();

		discordId = memberModel.getDiscordId();
		discordName = memberModel.getDiscordName();
		discordType = memberModel.getDiscordType();

		clanUniqueId = memberModel.getClanUniqueId();

		group = memberModel.getGroup();
		ranks = memberModel.getRanks();
		permissions = memberModel.getPermissions();

		tag = memberModel.getTag();
		chroma = memberModel.isChroma();

		medalList = memberModel.getMedalList();
		medal = memberModel.getMedal();

		tournamentGroup = memberModel.getTournamentGroup();

		money = memberModel.getMoney();
		xp = memberModel.getXp();
		position = memberModel.getPosition();
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

	public Member(String playerName, UUID uniqueId, AccountType accountType) {
		this.playerName = playerName;
		this.uniqueId = uniqueId;

		this.skinProfile = new Profile(playerName, uniqueId);

		this.fakeName = "";
		this.cooldown = new HashMap<>();

		this.accountConfiguration = new AccountConfiguration(this);
		this.loginConfiguration = new LoginConfiguration(this, accountType);

		this.punishmentHistory = new PunishmentHistory();

		this.discordName = "";
		this.discordId = 0l;
		this.discordType = DiscordType.DELINKED;

		this.group = Group.MEMBRO;
		this.permissions = new HashMap<>();
		this.ranks = new HashMap<>();

		this.tag = Tag.MEMBRO;

		this.medalList = new ArrayList<>();

		this.league = League.values()[0];
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

	public void setUniqueId(UUID uniqueId) {
		this.uniqueId = uniqueId;
		save("uniqueId");
	}

	public boolean hasParty() {
		return this.partyId != null;
	}

	public void setClanUniqueId(UUID clanUniqueId) {
		this.clanUniqueId = clanUniqueId;
		save("clanUniqueId");
	}

	public Clan getClan() {
		if (this.clanUniqueId == null)
			return null;

		return CommonGeneral.getInstance().getClanManager().getClan(clanUniqueId);
	}

	public boolean hasClan() {
		return this.clanUniqueId != null;
	}

	public void setMedal(Medal medal) {
		this.medal = medal;
		save("medal");
	}

	public void addMedal(Medal medal) {
		if (!this.medalList.contains(medal)) {
			this.medalList.add(medal);
			save("medalList");
		}
	}

	public void removeMedal(Medal medal) {
		if (this.medalList.contains(medal)) {
			this.medalList.remove(medal);
			save("medalList");
		}
	}

	public Medal getMedal() {
		if (medal == null)
			return Medal.NONE;

		return medal;
	}

	/*
	 * Fake
	 */

	public void setFakeName(String fakeName) {
		this.fakeName = fakeName;
		save("fakeName");
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
			save("cooldown");
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
		save("cooldown");
	}

	public void setCooldown(String cooldownKey, int cooldownTime) {
		cooldown.put(cooldownKey.toLowerCase(), System.currentTimeMillis() + (1000 * cooldownTime));
		save("cooldown");
	}

	public void setSkinProfile(Profile skinProfile) {
		this.skinProfile = skinProfile;
		save("skinProfile");
	}

	/*
	 * Social Midia
	 */

	public boolean hasDiscord() {
		return discordId > 0;
	}

	public void setDiscordId(Long discordId, String discordName) {
		this.discordId = discordId;
		this.discordName = discordName;
		this.discordType = discordId == 0l ? DiscordType.DELINKED : DiscordType.NORMAL;
		save("discordId", "discordName", "discordType");
	}

	public String getDiscordName() {
		if (this.discordName.isEmpty() || this.discordName == null) {
			return "Não vinculado";
		}

		return discordName;
	}

	public void setDiscordType(DiscordType discordType) {
		this.discordType = discordType;
		save("discordType");
	}

	@Override
	public String getName() {
		return this.playerName;
	}

	/*
	 * Group
	 */

	public boolean setTag(Tag tag) {
		this.tag = tag;

		if (!tag.isCustom())
			save("tag");
		return true;
	}

	public void setChroma(boolean chroma) {
		this.chroma = chroma;
		save("chroma");
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
		save("playerName");
	}

	public boolean isLastServer(ServerType serverType) {
		return this.serverType == serverType;
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
		save("group");
		setChroma(false);
	}

	public boolean hasGroupPermission(Group groupToUse) {
		if (getServerGroup() == Group.YOUTUBERPLUS)
			return Group.AJUDANTE.ordinal() >= groupToUse.ordinal();
		else if (getServerGroup() == Group.STREAMER)
			return Group.MODPLUS.ordinal() >= groupToUse.ordinal();
		return getServerGroup().ordinal() >= groupToUse.ordinal();
	}

	public boolean hasRank(Group group) {
		RankType rankType = null;

		try {
			rankType = RankType.valueOf(group.name());
		} catch (Exception ex) {
			return false;
		}

		return getRanks().containsKey(rankType);
	}

	public boolean hasRank(List<Group> groupList) {
		for (Group group : groupList) {
			try {
				if (getRanks().containsKey(RankType.valueOf(group.name())))
					return true;
			} catch (Exception ex) {
			}
		}

		return false;
	}

	public boolean hasRank(RankType rankType) {
		return getRanks().containsKey(rankType);
	}

	public boolean isGroup(Group groupToUse) {
		return getServerGroup().ordinal() == groupToUse.ordinal();
	}

	public boolean hasSkin() {
		return this.skinProfile != null && (!this.skinProfile.getPlayerName().equals(this.playerName)
				&& !this.skinProfile.getUniqueId().equals(this.uniqueId));
	}

	public void saveRanks() {
		save("ranks");
	}

	/*
	 * Status Information
	 */

	public void setReputation(int reputation) {
		this.reputation = reputation;

		if (this.reputation < -15)
			this.reputation = -15;

		if (this.reputation > 30)
			this.reputation = 30;

		save("reputation");
	}

	public void setLeague(League league) {
		this.league = league;
		save("league");
	}

	public void setXp(int xp) {
		this.xp = xp;

		if (this.xp < 0)
			this.xp = 0;

		save("xp");
	}

	public int addXp(int xp) {
		if (xp < 0)
			xp = 0;

		Clan clan = getClan();

		if (clan != null)
			clan.addXp(xp);

		setXp(getXp() + xp);
		return xp;
	}

	public int removeXp(int xp) {
		if (xp < 0)
			xp = 0;

		Clan clan = getClan();

		if (clan != null)
			clan.removeXp(xp);

		if (getXp() - xp < 0)
			setXp(0);
		else
			setXp(getXp() - xp);
		return xp;
	}

	public int addMoney(int money) {
		if (money < 0)
			money = 0;

		setMoney(getMoney() + money);
		return money;
	}

	public void setPosition(int position) {
		this.position = position;
		save("position");
	}

	public String getRanking() {
		return this.position < 0 ? ">15000" : "" + position;
	}

	public int removeMoney(int money) {
		if (money < 0)
			money = 0;

		setMoney(getMoney() - money);
		return money;
	}

	public void setMoney(int money) {
		this.money = money;

		if (this.money <= 0)
			this.money = 0;

		save("money");
	}

	public boolean hasPermission(String string) {
		if (permissions.containsKey(string.toLowerCase()))
			if (permissions.get(string.toLowerCase()) == -1l)
				return true;
			else if (permissions.get(string.toLowerCase()) > System.currentTimeMillis())
				return true;
			else
				return false;

		return false;
	}

	public void addPermission(String string) {
		if (permissions.containsKey(string.toLowerCase()))
			return;

		permissions.put(string.toLowerCase(), -1l);
		save("permissions");
	}

	public void removePermission(String string) {
		if (!permissions.containsKey(string.toLowerCase()))
			return;

		permissions.remove(string.toLowerCase());
		save("permissions");
	}

	public void setTournamentGroup(TournamentGroup tournamentGroup) {
		this.tournamentGroup = tournamentGroup;
		save("tournamentGroup");
	}

	/*
	 * Server Info
	 */

	public void setServerId(String serverId) {
		this.lastServerId = this.serverId;
		this.serverId = serverId;
		save("serverId", "lastServerId");
	}

	public void setServerType(ServerType serverType) {
		this.lastServerType = this.serverType;
		this.serverType = serverType;
		save("lastServerType");
	}

	/*
	 * Player Info
	 */

	public void setOnline(boolean online) {
		this.online = online;
		save("online");
	}

	/*
	 * Player Manager
	 */

	public long getSessionTime() {
		if (isOnline())
			return System.currentTimeMillis() - joinTime;
		else
			return 1500l;
	}

	public long getOnlineTime() {
		return onlineTime;
	}

	public void updateTime() {
		this.joinTime = System.currentTimeMillis();
		this.lastLogin = System.currentTimeMillis();
		save("joinTime", "lastLogin");
	}

	public void setJoinData(String playerName, String hostString) {
		this.playerName = playerName;
		this.lastIpAddress = hostString;

		this.accountConfiguration.setPlayer(this);
		this.loginConfiguration.setPlayer(this);
		save("playerName", "lastIpAddress", "online");
	}

	public void connect(String serverId, ServerType type) {
		checkRanks();

		if (this.serverId != null && !this.serverId.isEmpty())
			this.lastServerId = this.serverId + "";

		if (this.serverType != null)
			this.lastServerType = this.serverType;

		this.serverId = serverId;
		this.serverType = type;

		save("serverId", "serverType", "lastServerId", "lastServerType");
	}

	public void setLeaveData() {
		this.online = false;
		this.onlineTime = onlineTime + (System.currentTimeMillis() - lastLogin);

		save("online", "lastLogin", "onlineTime");
	}

	public void checkRanks() {
		if (getRanks() != null && !getRanks().isEmpty()) {
			Iterator<Entry<RankType, Long>> it = getRanks().entrySet().iterator();
			boolean save = false;

			while (it.hasNext()) {
				Entry<RankType, Long> entry = it.next();

				if (System.currentTimeMillis() > entry.getValue()) {
					it.remove();

					sendMessage("§c§l> §fO seu rank " + Tag.valueOf(entry.getKey().name()).getPrefix() + "§f expirou!");
					sendMessage("§c§l> §fVocê pode comprar novamente em §b" + CommonConst.STORE + "§f!");
					save = true;
				}
			}

			if (save)
				saveRanks();
		}
	}

	public void save(String... fieldName) {
		for (String field : fieldName)
			CommonGeneral.getInstance().getPlayerData().updateMember(this, field);
	}

	@Override
	public boolean isPlayer() {
		return true;
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Member) {
			Member member = (Member) obj;

			return member.getPlayerName().equals(getPlayerName()) && member.getUniqueId().equals(getUniqueId());
		}

		return super.equals(obj);
	}

}
