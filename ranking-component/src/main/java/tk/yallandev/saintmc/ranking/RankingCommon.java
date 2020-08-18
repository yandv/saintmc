package tk.yallandev.saintmc.ranking;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.Block;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.CommonPlatform;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.backend.data.ClanData;
import tk.yallandev.saintmc.common.backend.data.PlayerData;
import tk.yallandev.saintmc.common.backend.data.ServerData;
import tk.yallandev.saintmc.common.backend.data.StatusData;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoConnection;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.data.impl.ClanDataImpl;
import tk.yallandev.saintmc.common.data.impl.PlayerDataImpl;
import tk.yallandev.saintmc.common.data.impl.ServerDataImpl;
import tk.yallandev.saintmc.common.data.impl.StatusDataImpl;
import tk.yallandev.saintmc.common.permission.RankType;

public class RankingCommon implements Runnable {

	public RankingCommon() {
	}

	public static void main(String[] args) {
		new Thread(new RankingCommon()).start();
	}

	@Override
	public void run() {
		MongoConnection mongoConnection = new MongoConnection(CommonConst.MONGO_URL);
		RedisDatabase redisDatabase = new RedisDatabase("localhost", "", 6379);

		redisDatabase.connect();
		mongoConnection.connect();

		CommonGeneral general = new CommonGeneral(Logger.getLogger("ranking"));

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
		StatusData statusData = new StatusDataImpl(mongoConnection);

		general.setPlayerData(playerData);
		general.setClanData(clanData);
		general.setStatusData(statusData);

		AtomicInteger x = new AtomicInteger(1);

		mongoConnection.getDb().getCollection("account").find().sort(new Document("xp", -1))
				.limit(15000).forEach(new Block<Document>() {

					@Override
					public void apply(Document t) {
						MemberModel memberModel = CommonConst.GSON.fromJson(CommonConst.GSON.toJson(t),
								MemberModel.class);
						MemberVoid memberVoid = new MemberVoid(memberModel);
						
						memberVoid.setPosition(x.getAndIncrement());
					}

				});

		System.out.println(x.get());
	}

}
