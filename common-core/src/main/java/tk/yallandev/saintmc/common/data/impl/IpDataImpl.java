package tk.yallandev.saintmc.common.data.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.backend.data.IpData;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoConnection;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoQuery;
import tk.yallandev.saintmc.common.utils.ip.IpInfo;
import tk.yallandev.saintmc.common.utils.json.JsonBuilder;
import tk.yallandev.saintmc.common.utils.json.JsonUtils;

public class IpDataImpl implements IpData {

	private MongoQuery mongoQuery;

	public IpDataImpl(MongoConnection mongoConnection) {
		mongoQuery = new MongoQuery(mongoConnection, mongoConnection.getDataBase(), "ip");
	}

	@Override
	public IpInfo loadIp(String ipAddress) {
		JsonElement jsonElement = mongoQuery.findOne("query", ipAddress);
		return jsonElement == null ? null : CommonConst.GSON.fromJson(jsonElement, IpInfo.class);
	}

	@Override
	public void registerIp(IpInfo ipInfo) {
		boolean alreadyExist = mongoQuery.findOne("query", ipInfo.getIpAddress()) != null;

		if (alreadyExist)
			CommonGeneral.getInstance().debug("The address " + ipInfo.getIpAddress() + " already exist!");
		else
			mongoQuery.create(new String[] { CommonConst.GSON.toJson(ipInfo) });
	}

	@Override
	public void updateIp(IpInfo ipInfo, String fieldName) {
		JsonObject tree = JsonUtils.jsonTree(ipInfo);

		boolean alreadyExist = mongoQuery.findOne("query", ipInfo.getIpAddress()) != null;

		if (alreadyExist)
			mongoQuery.updateOne("query", ipInfo.getIpAddress(),
					new JsonBuilder().addProperty("fieldName", fieldName).add("value", tree.get(fieldName)).build());
		else
			registerIp(ipInfo);
	}

	@Override
	public void deleteIp(String ipAddress) {
		mongoQuery.deleteOne("ipAddress", ipAddress);
	}

}
