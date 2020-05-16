package tk.yallandev.saintmc.common.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bson.Document;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.backend.data.PunishData;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoDatabase;
import tk.yallandev.saintmc.common.ban.constructor.Ban;
import tk.yallandev.saintmc.common.ban.constructor.Mute;
import tk.yallandev.saintmc.common.ban.constructor.Warn;

public class PunishDataImpl implements PunishData {

	private com.mongodb.client.MongoDatabase database;

	public PunishDataImpl(MongoDatabase mongoDatabase) {
		database = mongoDatabase.getDb();
	}

	@Override
	public Collection<Ban> loadBan(UUID uniqueId, int limit) {
		MongoCursor<Document> mongo = database.getCollection("banList")
				.find(Filters.eq("uniqueId", uniqueId.toString())).limit(10).iterator();
		List<Ban> banList = new ArrayList<>();

		while (mongo.hasNext()) {
			banList.add(CommonConst.GSON.fromJson(CommonConst.GSON.toJson(mongo.next()), Ban.class));
		}

		return banList;
	}

	@Override
	public boolean hasBan(UUID uniqueId) {
		return database.getCollection("banList").countDocuments(Filters.eq("uniqueId", uniqueId.toString())) != 0;
	}

	@Override
	public void addBan(Ban ban) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(
				() -> database.getCollection("banList").insertOne(Document.parse(CommonConst.GSON.toJson(ban))));
	}

	@Override
	public int countBan() {
		return (int) database.getCollection("banList").countDocuments();
	}

	@Override
	public Collection<Mute> loadMute(UUID uniqueId, int limit) {
		MongoCursor<Document> mongo = database.getCollection("muteList")
				.find(Filters.eq("uniqueId", uniqueId.toString())).limit(10).iterator();
		List<Mute> muteList = new ArrayList<>();

		while (mongo.hasNext()) {
			muteList.add(CommonConst.GSON.fromJson(CommonConst.GSON.toJson(mongo.next()), Mute.class));
		}

		return muteList;
	}

	@Override
	public boolean hasMute(UUID uniqueId) {
		return database.getCollection("muteList").countDocuments(Filters.eq("uniqueId", uniqueId.toString())) != 0;
	}

	@Override
	public void addMute(Mute mute) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(
				() -> database.getCollection("muteList").insertOne(Document.parse(CommonConst.GSON.toJson(mute))));
	}

	@Override
	public int countMute() {
		return (int) database.getCollection("muteList").countDocuments();
	}

	@Override
	public Collection<Warn> loadWarn(UUID uniqueId, int limit) {
		return null;
	}

	@Override
	public boolean hasWarn(UUID uniqueId) {
		return database.getCollection("warnList").countDocuments(Filters.eq("uniqueId", uniqueId.toString())) != 0;
	}

	@Override
	public void addWarn(Warn warn) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			warn.setId(countWarn() + 1);
			database.getCollection("warnList").insertOne(Document.parse(CommonConst.GSON.toJson(warn)));
		});
	}

	@Override
	public int countWarn() {
		return (int) database.getCollection("warnList").countDocuments();
	}

}
