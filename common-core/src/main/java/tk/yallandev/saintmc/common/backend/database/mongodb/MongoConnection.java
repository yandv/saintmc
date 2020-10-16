package tk.yallandev.saintmc.common.backend.database.mongodb;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClientURI;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.CommonPlatform;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.backend.Credentials;
import tk.yallandev.saintmc.common.backend.Database;
import tk.yallandev.saintmc.common.backend.data.ClanData;
import tk.yallandev.saintmc.common.backend.data.PlayerData;
import tk.yallandev.saintmc.common.backend.data.ServerData;
import tk.yallandev.saintmc.common.backend.data.StatusData;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.data.impl.ClanDataImpl;
import tk.yallandev.saintmc.common.data.impl.PlayerDataImpl;
import tk.yallandev.saintmc.common.data.impl.ServerDataImpl;
import tk.yallandev.saintmc.common.data.impl.StatusDataImpl;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.common.utils.mojang.UUIDParser;

@Getter
public class MongoConnection implements Database {

	private static final String PATTERN = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

	private static final Pattern IP_PATTERN = Pattern
			.compile(PATTERN + "\\." + PATTERN + "\\." + PATTERN + "\\." + PATTERN);

	@Getter
	private com.mongodb.MongoClient client;
	@Getter
	private com.mongodb.client.MongoDatabase db;

	private String url;

	private String dataBase;
	private int port;

	public MongoConnection(String url) {
		this.url = url;
	}

	public MongoConnection(String hostName, String userName, String passWord, String dataBase, int port) {
		this(IP_PATTERN.matcher(hostName).matches()
				? "mongodb://" + (userName.isEmpty() ? "" : userName + ":" + passWord + "@") + hostName + "/" + dataBase
						+ "?retryWrites=true&w=majority"
				: "mongodb+srv://" + (userName.isEmpty() ? "" : userName + ":" + passWord + "@") + hostName + "/"
						+ dataBase + "?retryWrites=true&w=majority");
	}

	public MongoConnection(Credentials credentials) {
		this(credentials.getHostName(), credentials.getUserName(), credentials.getPassWord(), credentials.getDatabase(),
				credentials.getPort());
	}

	public MongoConnection(String hostName, String userName, String passWord, String dataBase) {
		this(hostName, userName, passWord, dataBase, 27017);
	}

	@Override
	public void connect() {
		MongoClientURI uri = new MongoClientURI(getUrl());
		this.dataBase = uri.getDatabase();

		client = new com.mongodb.MongoClient(new MongoClientURI(getUrl()));

		Logger.getLogger("uri").setLevel(Level.SEVERE);

		db = client.getDatabase(dataBase);
	}

	public com.mongodb.client.MongoDatabase getDatabase(String database) {
		return client.getDatabase(database);
	}

	@Override
	public boolean isConnected() {
		return client != null;
	}

	@Override
	public void close() {
		client.close();
	}

	public static void main(String[] args) {
		MongoConnection mongoConnection = new MongoConnection("177.54.152.149", "admin", "erANIaNutYpNeUBl", "admin",
				27017);

		RedisDatabase redisDatabase = new RedisDatabase("localhost", "", 6379);

		redisDatabase.connect();
		mongoConnection.connect();

		System.out.println(UUIDParser.parse("66ec14a471544df79cdae41c4c9a4c8d"));

		CommonGeneral general = new CommonGeneral(Logger.getLogger("OI"));

		general.setServerId("saintmc.net");
		general.setServerType(ServerType.HUNGERGAMES);
		general.setServerAddress("0.0.0.0:25565");

		general.setCommonPlatform(new CommonPlatform() {

			@Override
			public void runAsync(Runnable runnable) {
				runnable.run();
			}

			@Override
			public UUID getUuid(String playerName) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> T getPlayerByName(String playerName, Class<T> clazz) {
				return clazz.cast(null);
			}

			@Override
			public <T> T getExactPlayerByName(String playerName, Class<T> clazz) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> T getPlayerByUuid(UUID uniqueId, Class<T> clazz) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CommandSender getConsoleSender() {
				// TODO Auto-generated method stub
				return null;
			}

		});

		PlayerData playerData = new PlayerDataImpl(mongoConnection, redisDatabase);
		ClanData clanData = new ClanDataImpl(mongoConnection, redisDatabase);
		ServerData serverData = new ServerDataImpl(mongoConnection, redisDatabase);
		StatusData statusData = new StatusDataImpl(mongoConnection);

//		bdff

		serverData.startServer(80);
		serverData.updateStatus(MinigameState.GAMETIME, "pinto", 30);

		general.setPlayerData(playerData);
		general.setClanData(clanData);
		general.setStatusData(statusData);

//		MemberVoid member = playerData.loadMember(UUID.fromString("064d8897-68c5-4de5-8d0d-ee400e084f89"),
//				MemberVoid.class);
//
//		if (true) {
//			if (member == null) {
//				System.out.println("nao foi possivel achar o 064d8897-68c5-4de5-8d0d-ee400e084f89");
//			} else {
//				MemberVoid newMember = playerData.loadMember(UUID.fromString("c7facb9e-7bb1-4cc1-848e-ab4f9ac19bbc"),
//						MemberVoid.class);
//
//				if (newMember == null) {
//					System.out.println("nao foi possivel achar o c7facb9e-7bb1-4cc1-848e-ab4f9ac19bbc");
//				} else {
//					playerData.deleteMember(member.getUniqueId());
//					System.out.println("deletado!");
//
//					for (StatusType status : StatusType.values()) {
//						Status loadStatus = statusData.loadStatus(member.getUniqueId(), status);
//
//						if (loadStatus != null) {
//							loadStatus.setUniqueId(newMember.getUniqueId());
//							System.out.println("alterado id do negocio");
//							statusData.deleteStatus(member.getUniqueId(), status);
//						}
//					}
//
//					member.setUniqueId(newMember.getUniqueId());
//					member.setPlayerName("1aposenta");
//					System.out.println("alterado nick e id");
//				}
//
//			}
//
//			return;
//		}

		System.out.println(serverData.getTime(general.getServerId()));
		System.out.println(serverData.getMap(general.getServerId()));
		System.out.println(serverData.getState(general.getServerId()));

		System.out.println(CommonConst.GSON.toJson(CommonGeneral.getInstance().getStatusManager()
				.loadStatus(UUID.fromString("fa1a1461-8e39-4536-89ba-6a54143ddaeb"), StatusType.SHADOW)));

//		System.out.println(CommonGeneral.getInstance().getPlayerData().count(Filters.eq("tournamentGroup", "GROUP_A")));
//		System.out.println(CommonGeneral.getInstance().getPlayerData().count(Filters.eq("tournamentGroup", "GROUP_B")));
//		System.out.println(CommonGeneral.getInstance().getPlayerData().count(Filters.eq("tournamentGroup", "GROUP_C")));
//		System.out.println(CommonGeneral.getInstance().getPlayerData().count(Filters.eq("tournamentGroup", "GROUP_D")));

		AtomicInteger integer = new AtomicInteger(0);

		mongoConnection.getDatabase("saintmc-common").getCollection("account").find().forEach(new Block<Document>() {

			@Override
			public void apply(Document t) {
				MemberModel memberModel = CommonConst.GSON.fromJson(CommonConst.GSON.toJson(t), MemberModel.class);
				MemberVoid memberVoid = new MemberVoid(memberModel);

				if (memberVoid.hasPermission("kitpvp.kit.stomper")) {
					memberVoid.addMoney(25000);
					memberVoid.removePermission("kitpvp.kit.stomper");
				}

//				Iterator<Entry<RankType, Long>> iterator = memberVoid.getRanks().entrySet().iterator();
//				boolean needSave = false;
//
//				while (iterator.hasNext()) {
//					Entry<RankType, Long> entry = iterator.next();
//
//					entry.setValue(entry.getValue() + (1000 * 60 * 60 * 24 * 2));
//					needSave = true;
//				}
//
//				if (!memberVoid.getRanks().containsKey(RankType.SAINT)) {
//					memberVoid.getRanks().put(RankType.SAINT, System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 1));
//					needSave = true;
//				}
//
//				if (needSave) {
//					memberVoid.save("ranks");
//				}
//
//				if (memberVoid.getTournamentGroup() == null || memberVoid.getTournamentGroup() == TournamentGroup.NONE)
//					return;
//
//				Ban ban = memberVoid.getPunishmentHistory().getActiveBan();
//
//				if (ban != null) {
//					if (ban.isPermanent()) {
//						memberVoid.setTournamentGroup(TournamentGroup.NONE);
//						integer.addAndGet(1);
//					}
//					return;
//				}
//
//				if (memberVoid.getDiscordType() == DiscordType.DELINKED) {
//					memberVoid.setTournamentGroup(TournamentGroup.NONE);
//					integer.addAndGet(1);
//				}

			}

		});

		System.out.println(integer.get());

		System.exit(0);
	}

}
