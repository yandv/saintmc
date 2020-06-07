package tk.yallandev.saintmc.common.account;

import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import tk.yallandev.saintmc.common.account.configuration.AccountConfiguration;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration;
import tk.yallandev.saintmc.common.ban.PunishmentHistory;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.common.server.ServerType;
//import tk.yallandev.saintmc.discord.account.DiscordType;
import tk.yallandev.saintmc.common.tag.Tag;

@Getter
public class MemberModel {
	
    /*
     *  Player Information 
     *  
     */
    
    private String playerName;
    private final UUID uniqueId;
    
    private String fakeName;
    private Map<String, Long> cooldown;
    
    /*
     *  Configuration
     *  
     */
    
    private AccountConfiguration accountConfiguration;
    private LoginConfiguration loginConfiguration;
    
    /*
     *  History
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
     *  Permission Information 
     *  
     */
    
    private Group group;
	private Map<RankType, Long> ranks;
	private Map<String, Long> permissions;
    private Tag tag;
    
    /*
     *  Status Information
     *  
     */
    
	private int money;
	private int xp;
	private int totalXp;
	private League league;
	
	private int reputation;
    
    /*
     *  Player time
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
    
	private String serverId = "";
	private ServerType serverType;
	
	protected String lastServerId;
	protected ServerType lastServerType;
    
    private transient boolean online;
    
    public MemberModel(Member member) {
    	playerName = member.getPlayerName();
    	uniqueId = member.getUniqueId();
    	
    	fakeName = member.getFakeName();
    	cooldown = member.getCooldown();
    	
    	accountConfiguration = member.getAccountConfiguration();
    	loginConfiguration = member.getLoginConfiguration();
    	
    	punishmentHistory = member.getPunishmentHistory();
    	
    	discordId = member.getDiscordId();
    	discordName = member.getDiscordName();
    	discordType = member.getDiscordType();
    	
    	group = member.getGroup();
    	permissions = member.getPermissions();
    	ranks = member.getRanks();
    	tag = member.getTag();
    	
    	money = member.getMoney();
    	xp = member.getXp();
    	totalXp = member.getTotalXp();
    	league = member.getLeague();
    	reputation = member.getReputation();
    	
    	firstLogin = member.getFirstLogin();
    	lastLogin = member.getLastLogin();
    	joinTime = member.getJoinTime();
    	onlineTime = member.getOnlineTime();
    	
    	serverId = member.getServerId();
    	serverType = member.getServerType();
    	
    	online = member.isOnline();
	}

}
