package tk.yallandev.saintmc.common.data.impl;

import static tk.yallandev.saintmc.common.utils.json.JsonUtils.elementToBson;
import static tk.yallandev.saintmc.common.utils.json.JsonUtils.jsonTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.bson.Document;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.StatusType.Type;
import tk.yallandev.saintmc.common.account.status.types.game.GameModel;
import tk.yallandev.saintmc.common.account.status.types.game.GameStatus;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalModel;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalStatus;
import tk.yallandev.saintmc.common.backend.data.StatusData;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoConnection;

public class StatusDataImpl implements StatusData {

	private com.mongodb.client.MongoDatabase database;

	public StatusDataImpl(MongoConnection mongoDatabase) {
		database = mongoDatabase.getDatabase("saintmc-status");
	}

	@Override
	public Status loadStatus(UUID uniqueId, StatusType statusType) {
		Document document = database.getCollection(statusType.getMongoCollection())
				.find(Filters.eq("uniqueId", uniqueId.toString())).first();

		if (document == null)
			return null;

		return statusType.getType() == Type.NORMAL
				? CommonConst.GSON.fromJson(CommonConst.GSON.toJson(document), NormalStatus.class)
				: CommonConst.GSON.fromJson(CommonConst.GSON.toJson(document), GameStatus.class);
	}

	@Override
	public void saveStatus(Status status) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			MongoCollection<Document> collection = database.getCollection(status.getStatusType().getMongoCollection());

			if (status instanceof GameStatus) {
				GameModel gameModel = new GameModel((GameStatus) status);

				if (collection.find(Filters.eq("uniqueId", gameModel.getUniqueId().toString())).first() == null)
					collection.insertOne(Document.parse(CommonConst.GSON.toJson(gameModel)));

			} else if (status instanceof NormalStatus) {
				NormalModel normalModel = new NormalModel((NormalStatus) status);

				if (collection.find(Filters.eq("uniqueId", normalModel.getUniqueId().toString())).first() == null)
					collection.insertOne(Document.parse(CommonConst.GSON.toJson(normalModel)));
			} else {
				new NoSuchElementException("Cannot define the type of StatusModel");
			}
		});
	}

	@Override
	public void updateStatus(Status status, String fieldName) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			MongoCollection<Document> collection = database.getCollection(status.getStatusType().getMongoCollection());

			if (status instanceof GameStatus) {
				try {
					GameModel gameModel = new GameModel((GameStatus) status);
					JsonObject object = jsonTree(gameModel);

					if (object.has(fieldName)) {
						Object value = elementToBson(object.get(fieldName));
						collection.updateOne(Filters.eq("uniqueId", gameModel.getUniqueId().toString()),
								new Document("$set", new Document(fieldName, value)));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (status instanceof NormalStatus) {
				try {
					NormalModel normalModel = new NormalModel((NormalStatus) status);
					JsonObject object = jsonTree(normalModel);

					if (object.has(fieldName)) {
						Object value = elementToBson(object.get(fieldName));
						collection.updateOne(Filters.eq("uniqueId", normalModel.getUniqueId().toString()),
								new Document("$set", new Document(fieldName, value)));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				new NoSuchElementException("Cannot define the type of StatusModel");
			}
		});
	}
	
	@Override
	public Collection<Object> ranking(StatusType statusType, String fieldName) {
		MongoCollection<Document> collection = database.getCollection(statusType.getMongoCollection());
		
		MongoCursor<Document> mongo = collection.find().sort(Filters.eq(fieldName, -1)).limit(10).iterator();
		List<Object> memberList = new ArrayList<>();
		
		while (mongo.hasNext()) {
			String json = CommonConst.GSON.toJson(mongo.next());
			memberList.add(CommonConst.GSON.fromJson(json, statusType.getType() == Type.NORMAL ? NormalModel.class : GameModel.class));
		}
		
		return memberList;
	}

}
