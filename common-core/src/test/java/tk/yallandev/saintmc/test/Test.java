package tk.yallandev.saintmc.test;

import java.util.UUID;
import java.util.logging.Logger;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.CommonPlatform;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.backend.data.PlayerData;
import tk.yallandev.saintmc.common.backend.data.ReportData;
import tk.yallandev.saintmc.common.backend.data.ServerData;
import tk.yallandev.saintmc.common.backend.data.StatusData;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoConnection;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase;
import tk.yallandev.saintmc.common.data.PlayerDataImpl;
import tk.yallandev.saintmc.common.data.ReportDataImpl;
import tk.yallandev.saintmc.common.data.ServerDataImpl;
import tk.yallandev.saintmc.common.data.StatusDataImpl;
import tk.yallandev.saintmc.common.tag.Tag;

public class Test {
	
	private CommonGeneral general = new CommonGeneral(Logger.getLogger("yandv"));
	
	public static void main(String[] args) {
		new Test();
	}
	
	public Test() {
		general.setCommonPlatform(new CommonPlatform() {
			
			@Override
			public void runAsync(Runnable runnable) {
				runnable.run();
			}
			
			@Override
			public UUID getUuid(String playerName) {
				return null;
			}
			
			@Override
			public <T> T getPlayerByUuid(UUID uniqueId, Class<T> clazz) {
				return null;
			}
			
			@Override
			public <T> T getPlayerByName(String playerName, Class<T> clazz) {
				return null;
			}
			
			@Override
			public <T> T getExactPlayerByName(String playerName, Class<T> clazz) {
				return null;
			}
		});
		
		System.out.println(Tag.TRIAL.equals(Tag.TRIAL));
		
		general.setServerId("saintmc.com.br");
		
		try {
			MongoConnection mongo = new MongoConnection(CommonConst.MONGO_URL);
			RedisDatabase redis = new RedisDatabase("127.0.0.1", "", 6379);

			mongo.connect();
			redis.connect();

			PlayerData playerData = new PlayerDataImpl(mongo, redis);
			ServerData serverData = new ServerDataImpl(mongo, redis);
			ReportData reportData = new ReportDataImpl(mongo, redis);
			StatusData statusData = new StatusDataImpl(mongo);

			general.setPlayerData(playerData);
			general.setServerData(serverData);
			general.setReportData(reportData);
			general.setStatusData(statusData);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		
		String playerName = "yandv";
		UUID uniqueId = UUID.fromString("fa1a1461-8e39-4536-89ba-6a54143ddaeb");
		
		MemberModel memberModel = CommonGeneral.getInstance().getPlayerData().loadMember(uniqueId);
		
		Member member = null;

		/*
		 * Create instance of Member using MemberModel from backend
		 */

		if (memberModel == null) {
			member = new MemberVoid(playerName, uniqueId);
			CommonGeneral.getInstance().getPlayerData().createMember(member);
			CommonGeneral.getInstance().debug("Create member!");
		} else {
			member = new MemberVoid(memberModel);
			CommonGeneral.getInstance().debug("Loaded!");
		}
		
		CommonGeneral.getInstance().getMemberManager().loadMember(member);
		member.setTag(Tag.BETA);
	}

}
