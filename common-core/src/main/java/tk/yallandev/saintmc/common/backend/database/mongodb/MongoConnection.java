package tk.yallandev.saintmc.common.backend.database.mongodb;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClientURI;
import com.mongodb.client.model.Filters;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.CommonPlatform;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.account.status.StatusType;
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

@Getter
public class MongoConnection implements Database {

	private static final Pattern IP_PATTERN = Pattern.compile("([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])");

	@Getter
	private com.mongodb.MongoClient client;
	@Getter
	private com.mongodb.client.MongoDatabase db;

	private String url;

	private String dataBase;
	private int port;

	public MongoConnection(String hostName, String userName, String passWord, String dataBase, int port) {
		if (IP_PATTERN.matcher(hostName).matches()) {
			this.url = "mongodb://" + userName + ":" + passWord + "@" + hostName + "/" + dataBase
					+ "?retryWrites=true&w=majority";
		} else {
			this.url = "mongodb+srv://" + userName + ":" + passWord + "@" + hostName + "/" + dataBase
					+ "?retryWrites=true&w=majority";
		}
	}

	public MongoConnection(String url) {
		this.url = url;
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

		System.out.println(new Date(1596069705979l));

		MongoConnection mongoConnection = new MongoConnection(CommonConst.MONGO_URL);
		RedisDatabase redisDatabase = new RedisDatabase("localhost", "", 6379);

		redisDatabase.connect();
		mongoConnection.connect();

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

		System.out.println(serverData.getTime(general.getServerId()));
		System.out.println(serverData.getMap(general.getServerId()));
		System.out.println(serverData.getState(general.getServerId()));

		general.setPlayerData(playerData);
		general.setClanData(clanData);
		general.setStatusData(statusData);

		System.out.println(CommonConst.GSON.toJson(CommonGeneral.getInstance().getStatusManager()
				.loadStatus(UUID.fromString("fa1a1461-8e39-4536-89ba-6a54143ddaeb"), StatusType.SHADOW)));

//		AtomicInteger x = new AtomicInteger(0);
//
		mongoConnection.getDb().getCollection("account").find(Filters.eq("playerName", "yandv"))
				.forEach(new Block<Document>() {

					@Override
					public void apply(Document t) {
						MemberModel memberModel = CommonConst.GSON.fromJson(CommonConst.GSON.toJson(t),
								MemberModel.class);
						MemberVoid memberVoid = new MemberVoid(memberModel);

						memberVoid.setClanUniqueId(UUID.fromString("0b83ad08-7510-4f12-a218-706a6a5cbe90"));
					}

				});
//
//		System.out.println(x.get());

		System.exit(0);
	}

}
