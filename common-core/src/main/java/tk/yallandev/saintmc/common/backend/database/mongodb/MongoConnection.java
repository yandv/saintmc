package tk.yallandev.saintmc.common.backend.database.mongodb;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClientURI;

import lombok.Getter;
import tk.yallandev.saintmc.common.backend.Database;

@Getter
public class MongoConnection implements Database {

	private static final Pattern IP_PATTERN = Pattern.compile("([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])");

	@Getter
	private com.mongodb.MongoClient client;
	@Getter
	private com.mongodb.client.MongoDatabase db;

	private String url;

	private String dataBase;
	private int port;

	public MongoConnection(String hostName, String userName, String passWord, String dataBase, int port) {
		if (IP_PATTERN.matcher(hostName).matches()) {
			this.url = "mongodb://" + userName + ":" + passWord + "@" + hostName + "/" + dataBase + "?retryWrites=true&w=majority";
		} else {
			this.url = "mongodb+srv://" + userName + ":" + passWord + "@" + hostName + "/" + dataBase + "?retryWrites=true&w=majority";
		}
	}

	public MongoConnection(String url) {
		this.url = url;
	}

	public MongoConnection(String hostName, String userName, String passWord, String dataBase) {
		this(hostName, userName, passWord, dataBase, 27017);
	}

	@Override
	public void connect() {
		MongoClientURI uri = new MongoClientURI(getUrl());
		this.dataBase = uri.getDatabase();
		
		client = new com.mongodb.MongoClient(new MongoClientURI(getUrl()));

		Logger.getLogger("uri").setLevel(Level.SEVERE);

		db = client.getDatabase(dataBase);
	}

	public com.mongodb.client.MongoDatabase getDatabase(String database) {
		return client.getDatabase(database);
	}

	@Override
	public boolean isConnected() {
		return client != null;
	}

	@Override
	public void close() {
		client.close();
	}

	public static <T> void main(String[] args) {
		MongoConnection mongo;
		(mongo = new MongoConnection("mongodb+srv://root:kD2TqMzSyP78Keuk@cluster0-rcg3s.gcp.mongodb.net/test?retryWrites=true&w=majority"))
						.connect();

		mongo.getDb().getCollection("account").find().forEach(new Block<Document>() {

			@Override
			public void apply(Document t) {
				System.out.println(t.toString());
			}

		});
	}

}
