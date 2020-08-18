package tk.yallandev.saintmc.discord;

import java.util.UUID;
import java.util.logging.Logger;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.CommonPlatform;
import tk.yallandev.saintmc.common.backend.data.ClanData;
import tk.yallandev.saintmc.common.backend.data.PlayerData;
import tk.yallandev.saintmc.common.backend.data.StatusData;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoConnection;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.data.impl.ClanDataImpl;
import tk.yallandev.saintmc.common.data.impl.PlayerDataImpl;
import tk.yallandev.saintmc.common.data.impl.StatusDataImpl;

public class DiscordTest {

	public static void main(String[] args) {
		CommonGeneral general = new CommonGeneral(Logger.getLogger("discordTest"));

		general.setCommonPlatform(new CommonPlatform() {

			@Override
			public void runAsync(Runnable runnable) {
				new Thread(runnable).start();
			}

			@Override
			public UUID getUuid(String playerName) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> T getPlayerByUuid(UUID uniqueId, Class<T> clazz) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> T getPlayerByName(String playerName, Class<T> clazz) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> T getExactPlayerByName(String playerName, Class<T> clazz) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CommandSender getConsoleSender() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
		MongoConnection mongoConnection = new MongoConnection(
				CommonConst.MONGO_URL);
		RedisDatabase redisDatabase = new RedisDatabase("localhost", "", 6379);
		
		redisDatabase.connect();
		mongoConnection.connect();
		
		PlayerData playerData = new PlayerDataImpl(mongoConnection, redisDatabase);
		ClanData clanData = new ClanDataImpl(mongoConnection, redisDatabase);
		StatusData statusData = new StatusDataImpl(mongoConnection);

		general.setPlayerData(playerData);
		general.setClanData(clanData);
		general.setStatusData(statusData);

		new DiscordMain();
	}

}
