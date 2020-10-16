package tk.yallandev.saintmc.common.data.impl;

import static tk.yallandev.saintmc.common.utils.json.JsonUtils.elementToString;
import static tk.yallandev.saintmc.common.utils.json.JsonUtils.mapToObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bson.conversions.Bson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.backend.Query;
import tk.yallandev.saintmc.common.backend.data.PlayerData;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoConnection;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoQuery;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase;
import tk.yallandev.saintmc.common.utils.json.JsonBuilder;
import tk.yallandev.saintmc.common.utils.json.JsonUtils;

public class PlayerDataImpl implements PlayerData {

	private RedisDatabase redisDatabase;
	private Query<JsonElement> query;

	public PlayerDataImpl(MongoConnection mongoConnection, RedisDatabase redisDatabase) {
		this.query = createDefault(mongoConnection);
		this.redisDatabase = redisDatabase;
	}

	public PlayerDataImpl(Query<JsonElement> query, RedisDatabase redisDatabase) {
		this.query = query;
		this.redisDatabase = redisDatabase;
	}

	@Override
	public MemberModel loadMember(UUID uniqueId) {
		MemberModel memberModel = CommonGeneral.getInstance().getMemberManager().getMemberAsMemberModel(uniqueId);

		if (memberModel == null) {
			memberModel = getRedisPlayer(uniqueId);

			if (memberModel == null) {
				JsonElement found = query.findOne("uniqueId", uniqueId.toString());

				if (found != null) {
					memberModel = CommonConst.GSON.fromJson(CommonConst.GSON.toJson(found), MemberModel.class);
				}
			}
		}

		return memberModel;
	}

	@Override
	public <T extends Member> T loadMember(UUID uniqueId, Class<T> clazz) {
		MemberModel memberModel = CommonGeneral.getInstance().getMemberManager().getMemberAsMemberModel(uniqueId);

		if (memberModel == null) {
			memberModel = getRedisPlayer(uniqueId);

			if (memberModel == null) {
				JsonElement found = query.findOne("uniqueId", uniqueId.toString());

				if (found != null) {
					memberModel = CommonConst.GSON.fromJson(CommonConst.GSON.toJson(found), MemberModel.class);
				}
			}
		}

		try {
			return memberModel == null ? null : clazz.getConstructor(MemberModel.class).newInstance(memberModel);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public <T extends Member> T loadMember(String playerName, Class<T> clazz) {
		JsonElement jsonElement = query.findOne("playerName", Pattern.compile(playerName, Pattern.CASE_INSENSITIVE));

		if (jsonElement != null) {
			MemberModel memberModel = CommonConst.GSON.fromJson(CommonConst.GSON.toJson(jsonElement),
					MemberModel.class);

			try {
				return memberModel == null ? null : clazz.getConstructor(MemberModel.class).newInstance(memberModel);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public MemberModel loadMember(String playerName) {
		JsonElement found = query.findOne("playerName",
				Pattern.compile("^" + playerName + "$", Pattern.CASE_INSENSITIVE));

		if (found == null)
			return null;

		return CommonConst.GSON.fromJson(CommonConst.GSON.toJson(found), MemberModel.class);
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
	public void createMember(MemberModel memberModel) {
		boolean needCreate = query.findOne("uniqueId", memberModel.getUniqueId().toString()) == null;

		if (needCreate)
			query.create(new String[] { CommonConst.GSON.toJson(memberModel) });

		if (CommonGeneral.getInstance().getServerType().canSendData())
			CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

				@Override
				public void run() {
					try (Jedis jedis = redisDatabase.getPool().getResource()) {
						jedis.hmset("account:" + memberModel.getUniqueId().toString(),
								JsonUtils.objectToMap(memberModel));
					}
				}
			});
	}

	@Override
	public void createMember(Member member) {
		MemberModel memberModel = new MemberModel(member);
		boolean needCreate = query.findOne("uniqueId", memberModel.getUniqueId().toString()) == null;

		if (needCreate)
			query.create(new String[] { CommonConst.GSON.toJson(memberModel) });

		if (CommonGeneral.getInstance().getServerType().canSendData())
			CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

				@Override
				public void run() {
					try (Jedis jedis = redisDatabase.getPool().getResource()) {
						jedis.hmset("account:" + memberModel.getUniqueId().toString(),
								JsonUtils.objectToMap(memberModel));
					}
				}
			});
	}

	@Override
	public void deleteMember(UUID uniqueId) {
		boolean exist = query.findOne("uniqueId", uniqueId.toString()) != null;

		if (exist)
			query.deleteOne("uniqueId", uniqueId.toString());
	}

	@Override
	public void updateMember(Member member, String fieldName) {
		MemberModel memberModel = new MemberModel(member);
		JsonObject tree = JsonUtils.jsonTree(memberModel);

		query.updateOne("uniqueId", member.getUniqueId().toString(),
				new JsonBuilder().addProperty("fieldName", fieldName).add("value", tree.get(fieldName)).build());

		if (CommonGeneral.getInstance().getServerType().canSendData())
			CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

				@Override
				public void run() {
					if (tree.has(fieldName)) {
						JsonElement element = tree.get(fieldName);
						try (Jedis jedis = redisDatabase.getPool().getResource()) {
							Pipeline pipe = jedis.pipelined();
							jedis.hset("account:" + member.getUniqueId().toString(), fieldName,
									elementToString(element));

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
				}
			});
	}

	@Override
	public int count(Bson bson) {
		return (int) ((MongoQuery) query).getCollection().countDocuments(bson);
	}

	@Override
	public MemberModel loadMember(long discordId) {
		MemberModel memberModel = CommonGeneral.getInstance().getMemberManager().getMemberAsMemberModel(discordId);

		if (memberModel == null) {
			JsonElement found = query.findOne("discordId", discordId);

			if (found != null) {
				memberModel = CommonConst.GSON.fromJson(CommonConst.GSON.toJson(found), MemberModel.class);
			}
		}

		return memberModel;
	}

	@Override
	public Collection<MemberModel> ranking(String fieldName) {
		List<MemberModel> list = new ArrayList<>();

		for (JsonElement element : query.ranking(fieldName, -1, 10)) {
			list.add(CommonConst.GSON.fromJson(CommonConst.GSON.toJson(element), MemberModel.class));
		}

		return list;
	}

	@Override
	public void cacheMember(UUID uniqueId) {
		if (CommonGeneral.getInstance().getServerType().canSendData())
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return bool;
	}

	@Override
	public void closeConnection() {
		redisDatabase.close();
	}

	public static Query<JsonElement> createDefault(MongoConnection mongoConnection) {
		return new MongoQuery(mongoConnection, "saintmc-common", "account");
	}

}
