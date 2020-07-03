package tk.yallandev.saintmc.common.backend.database.mongodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.backend.Query;
import tk.yallandev.saintmc.common.utils.json.JsonUtils;

public class MongoQuery implements Query<JsonElement> {

	/**
	 * To replace the mongo int64 to java long
	 */

	private static final JsonWriterSettings SETTINGS = JsonWriterSettings.builder()
			.int64Converter((value, writer) -> writer.writeNumber(value.toString())).build();

	private MongoDatabase database;
	private MongoCollection<Document> collection;

	public MongoQuery(MongoConnection mongoConnection, String collectionName) {
		this.database = mongoConnection.getDb();
		this.collection = database.getCollection(collectionName);
	}

	public MongoQuery(MongoConnection mongoConnection, String databaseName, String collectionName) {
		this.database = mongoConnection.getDatabase(databaseName);
		this.collection = database.getCollection(collectionName);
	}

	@Override
	public Collection<JsonElement> find() {
		MongoCursor<Document> mongoCursor = collection.find().iterator();
		List<JsonElement> documentList = new ArrayList<>();

		while (mongoCursor.hasNext())
			documentList.add(JsonParser.parseString(mongoCursor.next().toJson(SETTINGS)));

		return documentList;
	}

	@Override
	public Collection<JsonElement> find(String collection) {
		MongoCursor<Document> mongoCursor = database.getCollection(collection).find().iterator();
		List<JsonElement> documentList = new ArrayList<>();

		while (mongoCursor.hasNext())
			documentList.add(JsonParser.parseString(mongoCursor.next().toJson(SETTINGS)));

		return documentList;
	}

	@Override
	public <GenericType> Collection<JsonElement> find(String key, GenericType value) {
		MongoCursor<Document> mongoCursor = collection.find(Filters.eq(key, value)).iterator();
		List<JsonElement> documentList = new ArrayList<>();

		while (mongoCursor.hasNext())
			documentList.add(JsonParser.parseString(mongoCursor.next().toJson(SETTINGS)));

		return documentList;
	}

	@Override
	public <GenericType> Collection<JsonElement> find(String collection, String key, GenericType value) {
		MongoCursor<Document> mongoCursor = database.getCollection(collection).find(Filters.eq(key, value)).iterator();
		List<JsonElement> documentList = new ArrayList<>();

		while (mongoCursor.hasNext())
			documentList.add(JsonParser.parseString(mongoCursor.next().toJson(SETTINGS)));

		return documentList;
	}

	@Override
	public <GenericType> JsonElement findOne(String key, GenericType value) {
		JsonElement json = null;
		Document document = collection.find(Filters.eq(key, value)).first();

		if (document != null)
			json = JsonParser.parseString(document.toJson(SETTINGS));

		return json;
	}

	@Override
	public <GenericType> JsonElement findOne(String collection, String key, GenericType value) {
		JsonElement json = null;
		Document document = database.getCollection(collection).find(Filters.eq(key, value)).first();

		if (document != null)
			json = JsonParser.parseString(document.toJson(SETTINGS));

		return json;
	}

	@Override
	public void create(String[] jsons) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

			@Override
			public void run() {
				for (String json : jsons) {
					collection.insertOne(Document.parse(json));
				}
			}
		});
	}

	@Override
	public void create(String collection, String[] jsons) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

			@Override
			public void run() {
				for (String json : jsons) {
					database.getCollection(collection).insertOne(Document.parse(json));
				}
			}
		});
	}

	@Override
	public <GenericType> void deleteOne(String key, GenericType value) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

			@Override
			public void run() {
				collection.deleteOne(Filters.eq(key, value));
			}
		});
	}

	@Override
	public <GenericType> void deleteOne(String collection, String key, GenericType value) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

			@Override
			public void run() {
				database.getCollection(collection).deleteOne(Filters.eq(key, value));
			}
		});
	}

	@Override
	public <GenericType> void updateOne(String key, GenericType value, JsonElement t) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

			@Override
			public void run() {
				JsonObject jsonObject = (JsonObject) t;

				if (jsonObject.has("fieldName") && jsonObject.has("value")) {
					Object object = JsonUtils.elementToBson(jsonObject.get("value"));

					collection.updateOne(Filters.eq(key, value),
							new Document("$set", new Document(jsonObject.get("fieldName").getAsString(), object)));
					return;
				}

				collection.updateOne(Filters.eq(key, value), Document.parse(t.toString()));
			}
		});
	}

	@Override
	public <GenericType> void updateOne(String collection, String key, GenericType value, JsonElement t) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

			@Override
			public void run() {
				JsonObject jsonObject = (JsonObject) t;

				if (jsonObject.has("fieldName") && jsonObject.has("value")) {
					Object object = JsonUtils.elementToBson(jsonObject.get("value"));

					database.getCollection(collection).updateOne(Filters.eq(key, value),
							new Document("$set", new Document(jsonObject.get("fieldName").getAsString(), object)));
					return;
				}

				database.getCollection(collection).updateOne(Filters.eq(key, value), Document.parse(t.toString()));
			}
		});
	}

	@Override
	public <GenericType> Collection<JsonElement> ranking(String key, GenericType value, int limit) {

		MongoCursor<Document> mongoCursor = collection.find().sort(Filters.eq(key, value)).limit(limit).iterator();
		List<JsonElement> documentList = new ArrayList<>();

		while (mongoCursor.hasNext())
			documentList.add(JsonParser.parseString(mongoCursor.next().toJson(SETTINGS)));

		return documentList;
	}

}
