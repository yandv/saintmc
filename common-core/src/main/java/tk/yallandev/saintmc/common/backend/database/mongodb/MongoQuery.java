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

	/**
	 * 
	 * @param key, value
	 * @return Collection<String> with all elements in this databse
	 */

	@Override
	public Collection<JsonElement> find(String key, String value) {
		MongoCursor<Document> mongoCursor = collection.find(Filters.eq(key, value)).iterator();
		List<JsonElement> documentList = new ArrayList<>();

		while (mongoCursor.hasNext())
			documentList.add(JsonParser.parseString(mongoCursor.next().toJson(SETTINGS)));

		return documentList;
	}

	@Override
	public Collection<JsonElement> find(String collection, String key, String value) {
		MongoCursor<Document> mongoCursor = database.getCollection(collection).find(Filters.eq(key, value)).iterator();
		List<JsonElement> documentList = new ArrayList<>();

		while (mongoCursor.hasNext())
			documentList.add(JsonParser.parseString(mongoCursor.next().toJson(SETTINGS)));

		return documentList;
	}

	@Override
	public JsonElement findOne(String key, String value) {
		JsonElement json = null;
		Document document = collection.find(Filters.eq(key, value)).first();

		if (document != null)
			json = JsonParser.parseString(document.toJson(SETTINGS));

		return json;
	}

	@Override
	public JsonElement findOne(String collection, String key, String value) {
		JsonElement json = null;
		Document document = database.getCollection(collection).find(Filters.eq(key, value)).first();

		if (document != null)
			json = JsonParser.parseString(document.toJson(SETTINGS));

		return json;
	}

	@Override
	public void find(QueryResponse<Collection<JsonElement>> response) {
		try {
			MongoCursor<Document> mongoCursor = collection.find().iterator();
			List<JsonElement> documentList = new ArrayList<>();

			while (mongoCursor.hasNext())
				documentList.add(JsonParser.parseString(mongoCursor.next().toJson(SETTINGS)));

			response.callback(documentList);

		} catch (Exception ex) {
			response.callback(new ArrayList<>());
			CommonGeneral.getInstance().getLogger().warning(ex.getMessage());
		}
	}

	@Override
	public void find(String collection, QueryResponse<Collection<JsonElement>> response) {
		try {
			MongoCursor<Document> mongoCursor = database.getCollection(collection).find().iterator();
			List<JsonElement> documentList = new ArrayList<>();

			while (mongoCursor.hasNext())
				documentList.add(JsonParser.parseString(mongoCursor.next().toJson(SETTINGS)));

			response.callback(documentList);

		} catch (Exception ex) {
			response.callback(new ArrayList<>());
			CommonGeneral.getInstance().getLogger().warning(ex.getMessage());
		}
	}

	@Override
	public void find(String key, String value, QueryResponse<Collection<JsonElement>> response) {
		try {
			MongoCursor<Document> mongoCursor = collection.find(Filters.eq(key, value)).iterator();
			List<JsonElement> documentList = new ArrayList<>();

			while (mongoCursor.hasNext())
				documentList.add(JsonParser.parseString(mongoCursor.next().toJson(SETTINGS)));

			response.callback(documentList);
		} catch (Exception ex) {
			response.callback(new ArrayList<>());
			CommonGeneral.getInstance().getLogger().warning(ex.getMessage());
		}
	}

	@Override
	public void find(String collection, String key, String value, QueryResponse<Collection<JsonElement>> response) {
		try {
			MongoCursor<Document> mongoCursor = database.getCollection(collection).find(Filters.eq(key, value))
					.iterator();
			List<JsonElement> documentList = new ArrayList<>();

			while (mongoCursor.hasNext())
				documentList.add(JsonParser.parseString(mongoCursor.next().toJson(SETTINGS)));

			response.callback(documentList);
		} catch (Exception ex) {
			response.callback(new ArrayList<>());
			CommonGeneral.getInstance().getLogger().warning(ex.getMessage());
		}
	}

	@Override
	public void findOne(String key, String value, QueryResponse<JsonElement> response) {
		try {
			Document document = collection.find(Filters.eq(key, value)).first();

			if (document == null) {
				response.callback(null);
			} else {
				response.callback(JsonParser.parseString(document.toJson(SETTINGS)));
			}
		} catch (Exception ex) {
			response.callback(null);
			CommonGeneral.getInstance().getLogger().warning(ex.getMessage());
		}
	}

	@Override
	public void findOne(String collection, String key, String value, QueryResponse<JsonElement> response) {
		try {
			Document document = database.getCollection(collection).find(Filters.eq(key, value)).first();

			if (document == null) {
				response.callback(null);
			} else {
				response.callback(JsonParser.parseString(document.toJson(SETTINGS)));
			}
		} catch (Exception ex) {
			response.callback(null);
			CommonGeneral.getInstance().getLogger().warning(ex.getMessage());
		}
	}

	@Override
	public void create(String[] jsons) {
		for (String json : jsons) {
			collection.insertOne(Document.parse(json));
		}
	}

	@Override
	public void create(String collection, String[] jsons) {
		for (String json : jsons) {
			database.getCollection(collection).insertOne(Document.parse(json));
		}
	}

	@Override
	public void deleteOne(String key, String value) {
		collection.deleteOne(Filters.eq(key, value));
	}

	@Override
	public void deleteOne(String collection, String key, String value) {
		database.getCollection(collection).deleteOne(Filters.eq(key, value));
	}

	@Override
	public void updateOne(String key, String value, JsonElement t) {
		JsonObject jsonObject = (JsonObject) t;

		if (jsonObject.has("fieldName") && jsonObject.has("value")) {
			Object object = JsonUtils.elementToBson(jsonObject.get("value"));

			collection.updateOne(Filters.eq(key, value),
					new Document("$set", new Document(jsonObject.get("fieldName").getAsString(), object)));
			return;
		}

		collection.updateOne(Filters.eq(key, value), Document.parse(t.toString()));
	}

	@Override
	public void updateOne(String collection, String key, String value, JsonElement t) {
		JsonObject jsonObject = (JsonObject) t;

		if (jsonObject.has("fieldName") && jsonObject.has("value")) {
			Object object = JsonUtils.elementToBson(jsonObject.get("value"));

			database.getCollection(collection).updateOne(Filters.eq(key, value),
					new Document("$set", new Document(jsonObject.get("fieldName").getAsString(), object)));
			return;
		}

		database.getCollection(collection).updateOne(Filters.eq(key, value), Document.parse(t.toString()));
	}

}
