package tk.yallandev.saintmc.common.data;

import static tk.yallandev.saintmc.common.utils.json.JsonUtils.elementToBson;
import static tk.yallandev.saintmc.common.utils.json.JsonUtils.jsonTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bson.Document;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.backend.data.ReportData;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoConnection;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.utils.supertype.Callback;

public class ReportDataImpl implements ReportData {

	private RedisDatabase redisDatabase;
	private MongoCollection<Document> reportCollection;

	public ReportDataImpl(MongoConnection mongoDatabase, RedisDatabase redisDatabase) {
		com.mongodb.client.MongoDatabase database = mongoDatabase.getDb();
		reportCollection = database.getCollection("report");
		this.redisDatabase = redisDatabase;
	}

	@Override
	public Collection<Report> loadReports() {
		List<Report> list = new ArrayList<>();

		MongoCursor<Document> mongoCursor = reportCollection.find().iterator();

		while (mongoCursor.hasNext()) {
			list.add(CommonConst.GSON.fromJson(CommonConst.GSON.toJson(mongoCursor.next()), Report.class));
		}

		return list;
	}

	@Override
	public Report loadReport(UUID uniqueId) {
		Document document = reportCollection.find(Filters.eq("uniqueId", uniqueId.toString())).first();

		if (document == null)
			return null;

		return CommonConst.GSON.fromJson(CommonConst.GSON.toJson(document), Report.class);
	}
	
	@Override
	public void saveReport(Report report) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			if (reportCollection.find(Filters.eq("uniqueId", report.getPlayerUniqueId().toString())).first() == null)
				reportCollection.insertOne(Document.parse(CommonConst.GSON.toJson(report)));
			
	        try (Jedis jedis = redisDatabase.getPool().getResource()) {
	            Pipeline pipeline = jedis.pipelined();
	            JsonObject publish = new JsonObject();
	            publish.addProperty("uniqueId", report.getPlayerUniqueId().toString());
	            publish.add("value", jsonTree(report));
	            publish.addProperty("action", "create");
	            publish.addProperty("source", CommonGeneral.getInstance().getServerId());
	            pipeline.publish("report-action", publish.toString());
	            jedis.sync();
	        }
		});
	}

	@Override
	public void saveReport(Report report, Callback<Report> callback) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			Document document = reportCollection.find(Filters.eq("uniqueId", report.getPlayerUniqueId().toString())).first();
			
			if (document == null) {
				reportCollection.insertOne(Document.parse(CommonConst.GSON.toJson(report)));
				callback.callback(null);
			} else
				callback.callback(CommonConst.GSON.fromJson(CommonConst.GSON.toJson(document), Report.class));
			
	        try (Jedis jedis = redisDatabase.getPool().getResource()) {
	            Pipeline pipeline = jedis.pipelined();
	            JsonObject publish = new JsonObject();
	            publish.addProperty("uniqueId", report.getPlayerUniqueId().toString());
	            publish.add("value", jsonTree(report));
	            publish.addProperty("action", "create");
	            publish.addProperty("source", CommonGeneral.getInstance().getServerId());
	            pipeline.publish("report-action", publish.toString());
	            jedis.sync();
	        }
		});
	}
	
	@Override
	public void deleteReport(UUID uniqueId) {
        CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
        	
        	reportCollection.deleteOne(Filters.eq("uniqueId", uniqueId.toString()));
        	
        	try (Jedis jedis = redisDatabase.getPool().getResource()) {
                Pipeline pipeline = jedis.pipelined();
                JsonObject publish = new JsonObject();
                publish.addProperty("uniqueId", uniqueId.toString());
                publish.addProperty("action", "remove");
                publish.addProperty("source", CommonGeneral.getInstance().getServerId());
                pipeline.publish("report-action", publish.toString());
                jedis.sync();
            }
        });
	}

	@Override
	public void updateReport(Report report, String fieldName) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			JsonObject object = jsonTree(report);
			
			try {

				if (object.has(fieldName)) {
					Object value = elementToBson(object.get(fieldName));
					reportCollection.updateOne(Filters.eq("uniqueId", report.getPlayerUniqueId().toString()),
							new Document("$set", new Document(fieldName, value)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
            try (Jedis jedis = redisDatabase.getPool().getResource()) {
            	JsonElement element = object.get(fieldName);
                Pipeline pipe = jedis.pipelined();

                JsonObject json = new JsonObject();
                json.add("uniqueId", new JsonPrimitive(report.getPlayerUniqueId().toString()));
                json.add("source", new JsonPrimitive(CommonGeneral.getInstance().getServerId()));
                json.add("field", new JsonPrimitive(fieldName));
                json.add("value", element);
                pipe.publish("report-field", json.toString());

                pipe.sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
		});
	}

	@Override
	public void updateName(UUID uniqueId, String playerName) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			reportCollection.updateOne(Filters.eq("uniqueId", uniqueId.toString()),
							new Document("$set", new Document("playerName", playerName)));
		});
	}

}
