package tk.yallandev.saintmc.common.data;

import static tk.yallandev.saintmc.common.utils.json.JsonUtils.elementToBson;
import static tk.yallandev.saintmc.common.utils.json.JsonUtils.elementToString;
import static tk.yallandev.saintmc.common.utils.json.JsonUtils.jsonTree;
import static tk.yallandev.saintmc.common.utils.json.JsonUtils.mapToObject;
import static tk.yallandev.saintmc.common.utils.json.JsonUtils.objectToMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

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
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.backend.data.PlayerData;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoConnection;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase;
import tk.yallandev.saintmc.common.utils.json.JsonUtils;

public class PlayerDataImpl implements PlayerData {

	private RedisDatabase redisDatabase;
	private MongoCollection<Document> memberCollection;

	public PlayerDataImpl(MongoConnection mongoDatabase, RedisDatabase redisDatabase) {
		com.mongodb.client.MongoDatabase database = mongoDatabase.getDb();
		memberCollection = database.getCollection("account");
		this.redisDatabase = redisDatabase;
	}

	@Override
	public MemberModel loadMember(UUID uniqueId) {
		MemberModel memberModel = CommonGeneral.getInstance().getMemberManager().getMemberAsMemberModel(uniqueId);

		if (memberModel == null) {
			memberModel = getRedisPlayer(uniqueId);

			if (memberModel == null) {
				Document found = memberCollection.find(Filters.eq("uniqueId", uniqueId.toString())).first();

				if (found != null) {
					memberModel = CommonConst.GSON.fromJson(CommonConst.GSON.toJson(found), MemberModel.class);
				}
			}
		}

		return memberModel;
	}

	public MemberModel getRedisPlayer(UUID uuid) {
		MemberModel player;

		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			if (!jedis.exists("account:" + uuid.toString()))
				return null;
			Map<String, String> fields = jedis.hgetAll("account:" + uuid.toString());

			if (fields == null || fields.isEmpty() || fields.size() < 40)
				return null;

			player = mapToObject(fields, MemberModel.class);
		}

		return player;
	}

	@Override
	public void saveMember(MemberModel memberModel) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			Document document = memberCollection.find(Filters.eq("uniqueId", memberModel.getUniqueId().toString())).first();
			
			if (document == null)
				memberCollection.insertOne(Document.parse(CommonConst.GSON.toJson(memberModel)));

			try (Jedis jedis = redisDatabase.getPool().getResource()) {
				jedis.hmset("account:" + memberModel.getUniqueId().toString(), objectToMap(memberModel));
			}
		});
	}

	@Override
	public void saveMember(Member member) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			Document document = memberCollection.find(Filters.eq("uniqueId", member.getUniqueId().toString())).first();
			
			if (document == null)
				memberCollection.insertOne(Document.parse(CommonConst.GSON.toJson(member)));

			try (Jedis jedis = redisDatabase.getPool().getResource()) {
				jedis.hmset("account:" + member.getUniqueId().toString(),
						JsonUtils.objectToMap(new MemberModel(member)));
			}
		});
	}

	@Override
	public void updateMember(Member member, String fieldName) {
		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			try {
				MemberModel memberModel = new MemberModel(member);
				JsonObject object = jsonTree(memberModel);
				if (object.has(fieldName)) {
					Object value = elementToBson(object.get(fieldName));
					memberCollection.updateOne(Filters.eq("uniqueId", member.getUniqueId().toString()),
							new Document("$set", new Document(fieldName, value)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			JsonObject tree = CommonConst.GSON.toJsonTree(member).getAsJsonObject();

			if (tree.has(fieldName)) {
				JsonElement element = tree.get(fieldName);
				try (Jedis jedis = redisDatabase.getPool().getResource()) {
					Pipeline pipe = jedis.pipelined();
					jedis.hset("account:" + member.getUniqueId().toString(), fieldName, elementToString(element));

					JsonObject json = new JsonObject();
					json.add("uniqueId", new JsonPrimitive(member.getUniqueId().toString()));
					json.add("source", new JsonPrimitive(CommonGeneral.getInstance().getServerId()));
					json.add("field", new JsonPrimitive(fieldName));
					json.add("value", element);
					pipe.publish("account-field", json.toString());

					pipe.sync();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public MemberModel loadMember(long discordId) {
		MemberModel memberModel = CommonGeneral.getInstance().getMemberManager().getMemberAsMemberModel(discordId);

		if (memberModel == null) {
			Document found = memberCollection.find(Filters.eq("discordId", discordId)).first();

			if (found != null) {
				memberModel = CommonConst.GSON.fromJson(CommonConst.GSON.toJson(found), MemberModel.class);
			}
		}

		return memberModel;
	}
	
	@Override
	public String checkNickname(String playerName) {
        Document found = memberCollection.find(Filters.eq("playerName", Pattern.compile("^" + playerName + "$", Pattern.CASE_INSENSITIVE))).first();
        
        if (found == null)
        	return null;
        
        MemberModel memberModel = CommonConst.GSON.fromJson(CommonConst.GSON.toJson(found), MemberModel.class);
        
        return memberModel.getPlayerName();
	}

	@Override
	public Collection<MemberModel> ranking(String fieldName) {
		MongoCursor<Document> mongo = memberCollection.find().sort(Filters.eq(fieldName, -1)).limit(10).iterator();
		List<MemberModel> memberList = new ArrayList<>();

		while (mongo.hasNext()) {
			memberList.add(CommonConst.GSON.fromJson(CommonConst.GSON.toJson(mongo.next()), MemberModel.class));
		}

		return memberList;
	}

	@Override
	public void cacheMember(UUID uniqueId) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			CommonGeneral.getInstance().debug(uniqueId + "");
			jedis.expire("account:" + uniqueId.toString(), 300);
		}
	}

	@Override
	public boolean checkCache(UUID uniqueId) {
		boolean bool = false;

		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			String key = "account:" + uniqueId.toString();
			if (jedis.ttl(key) >= 0) {
				bool = jedis.persist(key) == 1;
			}
		}

		if (bool)
			CommonGeneral.getInstance().debug("REDIS > SHOULD REMOVE");
		else
			CommonGeneral.getInstance().debug("REDIS > SUB-SERVER");
		return bool;
	}

	@Override
	public void closeConnection() {
		redisDatabase.close();
	}

}
