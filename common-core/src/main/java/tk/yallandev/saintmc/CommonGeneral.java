package tk.yallandev.saintmc;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Charsets;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.common.backend.data.ClanData;
import tk.yallandev.saintmc.common.backend.data.PlayerData;
import tk.yallandev.saintmc.common.backend.data.PunishData;
import tk.yallandev.saintmc.common.backend.data.ReportData;
import tk.yallandev.saintmc.common.backend.data.ServerData;
import tk.yallandev.saintmc.common.backend.data.StatusData;
import tk.yallandev.saintmc.common.controller.ClanManager;
import tk.yallandev.saintmc.common.controller.MemberManager;
import tk.yallandev.saintmc.common.controller.ReportManager;
import tk.yallandev.saintmc.common.controller.StatusManager;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.utils.mojang.MojangFetcher;

public class CommonGeneral {

	@Getter
	private static CommonGeneral instance;

	/**
	 * 
	 * Backend
	 * 
	 */

	@Getter
	@Setter
	private PlayerData playerData;

	@Getter
	@Setter
	private ClanData clanData;

	@Getter
	@Setter
	private StatusData statusData;

	@Getter
	@Setter
	private ReportData reportData;

	@Getter
	@Setter
	private ServerData serverData;

	@Getter
	@Setter
	private PunishData punishData;

	@Getter
	@Setter
	private CommonPlatform commonPlatform;

	/**
	 * 
	 * Controller
	 * 
	 */

	@Getter
	private MemberManager memberManager = new MemberManager();
	
	@Getter
	private ClanManager clanManager = new ClanManager();

	@Getter
	private StatusManager statusManager = new StatusManager();

	@Getter
	private ReportManager reportManager = new ReportManager();

	@Getter
	private MojangFetcher mojangFetcher = new MojangFetcher();

	/**
	 * 
	 * Server Info
	 * 
	 */

	@Getter
	@Setter
	private String serverId;

	@Getter
	@Setter
	private ServerType serverType;

	@Getter
	@Setter
	private String serverAddress;

	/**
	 * 
	 * Utility
	 * 
	 */

	@Getter
	private Logger logger;

	@Getter
	@Setter
	private boolean onlyPremium = false;

	@Getter
	@Setter
	private boolean loginServer = true;

	@Setter
	private boolean debug = true;

	public CommonGeneral(Logger logger) {
		instance = this;

		this.logger = logger;

		logger.log(Level.INFO, "CommonGeneral has been loaded!");
	}

	public void debug(String string) {
		if (debug)
			logger.log(Level.INFO, "[DEBUG] " + string);
	}

	public UUID getUuid(String name) {
		UUID uuid = mojangFetcher.getUuid(name);

		if (uuid == null)
			uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));

		return uuid;
	}

}
